/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.internal.compiler;

import static org.openhab.automation.jrule.internal.JRuleConstants.JAVA_FILE_TYPE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.JRuleConstants;
import org.openhab.automation.jrule.internal.JRuleLog;
import org.openhab.automation.jrule.internal.JRuleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleCompiler}
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleCompiler {

    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    private static final String CLASSPATH_OPTION = "-classpath";
    public static final String JAR_JRULE_NAME = "jrule.jar";
    public static final String JAR_JRULE_ITEMS_NAME = "jrule-items.jar";
    private static final String LOG_NAME_COMPILER = "JRuleCompiler";

    private final Logger logger = LoggerFactory.getLogger(JRuleCompiler.class);

    private final JRuleConfig jRuleConfig;

    public JRuleCompiler(JRuleConfig jRuleConfig) {
        this.jRuleConfig = jRuleConfig;
    }

    public void loadClasses(ClassLoader classLoader, File classFolder, String classPackage, boolean createInstance) {
        try {
            final File[] classItems = classFolder.listFiles(JRuleFileNameFilter.CLASS_FILTER);
            if (classItems == null || classItems.length == 0) {
                logInfo("Found no user defined java rules to load into memory in folder: {}",
                        classFolder.getAbsolutePath());
                return;
            }
            logInfo("Number of Java Rules classes to load in to memory: {} folder: {}", classItems.length,
                    classFolder.getAbsolutePath());
            Arrays.stream(classItems).forEach(classItem -> logDebug("Attempting to load class: {}", classItem));

            Arrays.stream(classItems).forEach(classItem -> {
                logDebug("Loading instance for class: {}", classItem.getName());

                try {
                    Class<?> loadedClass = classLoader.loadClass(classPackage
                            + JRuleUtil.removeExtension(classItem.getName(), JRuleConstants.CLASS_FILE_TYPE));
                    logDebug("Loaded class with classLoader: {}", classItem.getName());

                    if (createInstance) {
                        if (Modifier.isAbstract(loadedClass.getModifiers())) {
                            logDebug("Not creating and instance of abstract class: {}", classItem.getName());
                        } else {
                            try {
                                final Object obj = loadedClass.getDeclaredConstructor().newInstance();
                                logDebug("Created instance: {} obj: {}", classItem.getName(), obj);
                            } catch (Exception x) {
                                logDebug("Could not create create instance using default constructor: {}",
                                        classItem.getName());
                            }
                        }
                    }
                } catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
                    logError("Could not load class", e);
                }
            });
        } catch (Exception e) {
            logError("error instance", e);
        }
    }

    public void compile(File javaSourceFile, String classPath) {
        compile(List.of(javaSourceFile), classPath);
    }

    public void compile(List<File> javaSourceFiles, String classPath) {
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        final List<String> optionList = new ArrayList<>();
        optionList.add(CLASSPATH_OPTION);
        optionList.add(classPath);
        logDebug("Compiling classes using classpath: {}", classPath);
        javaSourceFiles.stream().filter(javaSourceFile -> javaSourceFile.exists() && javaSourceFile.canRead())
                .forEach(javaSourceFile -> logDebug("Compiling java Source file: {}", javaSourceFile));

        final Iterable<? extends JavaFileObject> compilationUnit = fileManager
                .getJavaFileObjectsFromFiles(javaSourceFiles);
        final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
                compilationUnit);
        try {
            if (task.call()) {
                logDebug("Compilation of classes successfully!");
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    logInfo("Error on line {} in {}", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
                }
            }
            fileManager.close();
        } catch (Exception x) {
            logError("error", x);
        }
    }

    public File[] getJavaSourceItemsFromFolder(File folder) {
        return folder.listFiles(JRuleFileNameFilter.JAVA_FILTER);
    }

    public void compileItemsInFolder(File itemsFolder) {
        final String itemsClassPath = System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator
                + getJarPath(JAR_JRULE_NAME);
        logDebug("Compiling items in folder: {}", itemsFolder.getAbsolutePath());
        final File[] javaItems = getJavaSourceItemsFromFolder(itemsFolder);
        final File[] classItems = itemsFolder.listFiles(JRuleFileNameFilter.CLASS_FILTER);
        final Set<String> classNames = new HashSet<>();
        Arrays.stream(classItems).forEach(classItem -> classNames
                .add(JRuleUtil.removeExtension(classItem.getName(), JRuleConstants.CLASS_FILE_TYPE)));

        logDebug("ClassNameSetSize: {}", classNames.size());
        Arrays.stream(javaItems)
                .filter(javaItem -> !classNames.contains(JRuleUtil.removeExtension(javaItem.getName(), JAVA_FILE_TYPE)))
                .forEach(javaItem -> compile(javaItem, itemsClassPath));
        classNames.clear();
    }

    public String getJarPath(String jarName) {
        return new StringBuilder().append(jRuleConfig.getJarDirectory()).append(File.separator).append(jarName)
                .toString();
    }

    public void compileItems() {
        compileItemsInFolder(new File(jRuleConfig.getItemsDirectory()));
    }

    public void compileRules() {
        String rulesClassPath = //
                System.getProperty(JAVA_CLASS_PATH_PROPERTY) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_ITEMS_NAME) + File.pathSeparator //
                        + getJarPath(JAR_JRULE_NAME) + File.pathSeparator; //
        String extLibPath = getExtLibPaths();
        logDebug("extLibPath: {}", extLibPath);
        if (extLibPath != null && !extLibPath.isEmpty()) {
            rulesClassPath = rulesClassPath.concat(extLibPath);
        }
        logDebug("Compiling rules in folder: {}", jRuleConfig.getRulesDirectory());

        try (Stream<Path> paths = Files.walk(Paths.get(jRuleConfig.getRulesDirectory()))) {
            List<File> ruleJavaFiles = paths.filter(Files::isRegularFile) // is a file
                    .filter(f -> f.getFileName().toString().endsWith(JAVA_FILE_TYPE)).map(Path::toFile)
                    .collect(Collectors.toList());
            if (!ruleJavaFiles.isEmpty()) {
                compile(ruleJavaFiles, rulesClassPath);
            } else {
                logWarn("Found no java rules to compile and use in folder {}", jRuleConfig.getRulesDirectory());
            }
        } catch (IOException e) {
            logError("Error listing java files in folder: {}", jRuleConfig.getRulesDirectory(), e);

        }
    }

    public List<URL> getExtLibsAsUrls() {
        try {
            File[] extLibsFiles = getExtLibsAsFiles();
            return Arrays.stream(extLibsFiles).map(this::getUrl).collect(Collectors.toList());
        } catch (Exception x) {
            logError("Failed to get extLib urls");
            return new ArrayList<>();
        }
    }

    private URL getUrl(File f) {
        try {
            return f.toURI().toURL();
        } catch (MalformedURLException e) {
            logError("Failed to convert to URL: {}", f.getAbsolutePath(), e);
        }
        return null;
    }

    public File[] getExtLibsAsFiles() {
        return new File(jRuleConfig.getExtlibDirectory()).listFiles(JRuleFileNameFilter.JAR_FILTER);
    }

    private String getExtLibPaths() {
        final File[] extLibs = getExtLibsAsFiles();
        final StringBuilder builder = new StringBuilder();
        if (extLibs != null && extLibs.length > 0) {
            Arrays.stream(extLibs).forEach(extLib -> builder.append(createJarPath(extLib)));
        }
        return builder.toString();
    }

    private String createJarPath(File extLib) {
        if (!extLib.canRead()) {
            logError("Invalid permissions for external lib jar, ignored: {}", extLib.getAbsolutePath());
            return JRuleConstants.EMPTY;
        }
        return extLib.getAbsolutePath().concat(File.pathSeparator);
    }

    private static class JRuleFileNameFilter implements FilenameFilter {

        private static final JRuleFileNameFilter JAVA_FILTER = new JRuleFileNameFilter(JAVA_FILE_TYPE);
        private static final JRuleFileNameFilter CLASS_FILTER = new JRuleFileNameFilter(JRuleConstants.CLASS_FILE_TYPE);
        private static final JRuleFileNameFilter JAR_FILTER = new JRuleFileNameFilter(JRuleConstants.JAR_FILE_TYPE);

        private final String fileType;

        public JRuleFileNameFilter(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(fileType);
        }
    }

    private void logDebug(String message, Object... parameters) {
        JRuleLog.debug(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logInfo(String message, Object... parameters) {
        JRuleLog.info(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logError(String message, Object... parameters) {
        JRuleLog.error(logger, LOG_NAME_COMPILER, message, parameters);
    }

    private void logWarn(String message, Object... parameters) {
        JRuleLog.warn(logger, LOG_NAME_COMPILER, message, parameters);
    }
}
