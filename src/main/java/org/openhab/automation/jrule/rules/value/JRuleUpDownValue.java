/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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
package org.openhab.automation.jrule.rules.value;

import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * The {@link JRuleUpDownValue} JRule Command
 *
 * @author Timo Litzius- Initial contribution
 */
public enum JRuleUpDownValue implements JRuleValue {
    UP(UpDownType.UP),
    DOWN(UpDownType.DOWN);

    private final UpDownType ohType;

    JRuleUpDownValue(UpDownType ohType) {
        this.ohType = ohType;
    }

    public static JRuleUpDownValue getValueFromString(String value) {
        if (value.equals("UP")) {
            return UP;
        }
        if (value.equals("DOWN")) {
            return DOWN;
        }
        return null;
    }

    public static JRuleValue valueOf(boolean command) {
        return command ? UP : DOWN;
    }

    @Override
    public String stringValue() {
        return name();
    }

    @Override
    public Command toOhCommand() {
        return this.ohType;
    }

    @Override
    public State toOhState() {
        return this.ohType;
    }

    @Override
    public <T extends JRuleValue> T as(Class<T> target) {
        throw new IllegalStateException("cannot cast to '%s'".formatted(target));
    }

    @Override
    public String toString() {
        return ohType.toString();
    }
}
