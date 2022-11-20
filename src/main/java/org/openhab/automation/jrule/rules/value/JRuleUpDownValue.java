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

    @Override
    public String asStringValue() {
        return name();
    }

    @Override
    public Command toOhCommand() {
        return this.ohType;
    }

    @Override
    public State toOhState() {
        throw new IllegalStateException("not a state type");
    }
}
