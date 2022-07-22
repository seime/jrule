/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.automation.jrule.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DelayedDebouncingExecutor} schedules actions to be executed in the future. Any duplicate requests will
 * cancel the existing one and add another delay.
 *
 * @author Arne Seime - Initial contribution
 */

public class DelayedDebouncingExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DelayedDebouncingExecutor.class);

    private final ScheduledExecutorService executorService;
    private static final int TERMINATION_AWAIT_TIME_SECONDS = 20;
    private final int delay;
    private final TimeUnit timeUnit;
    @Nullable
    private ScheduledFuture<Boolean> existingInvocationFuture = null;

    public DelayedDebouncingExecutor(int delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Cancel any pending future execution
     */
    public void cancel() {
        if (existingInvocationFuture != null && !existingInvocationFuture.isDone()) {
            existingInvocationFuture.cancel(true);
        }
    }

    /**
     * Schedule a new execution of a Callable. Any pending executions will be cancelled and replace by this new one.
     * Actual execution of the callable happens after the given delay - if not replaced by a subsequent call to this
     * method.
     * 
     * @param callable the callable to schedule
     */
    public synchronized void call(Callable<Boolean> callable) {
        boolean shouldSchedule = true;
        if (existingInvocationFuture != null && !existingInvocationFuture.isDone()) {
            logger.debug("Cancelling existing delayed execution");
            shouldSchedule = existingInvocationFuture.cancel(false);
        }
        if (shouldSchedule) {
            existingInvocationFuture = executorService.schedule(callable, delay, timeUnit);
        }
    }

    public void shutdown() {
        logger.debug("Shutting down delayed debouncing executor");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(TERMINATION_AWAIT_TIME_SECONDS, TimeUnit.SECONDS);
            logger.debug("Delayed debouncing executor shutdown complete");
        } catch (InterruptedException e) {
            logger.warn("Got interrupted while shutting down delayed debouncing executor", e);
        }
    }
}
