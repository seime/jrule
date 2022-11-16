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

/**
 * The {@link JRuleStopMoveValue} JRule Command
 *
 * @author Timo Litzius- Initial contribution
 */
public enum JRuleStopMoveValue implements JRuleValue {
    STOP,
    MOVE,
    UNDEF;

    public static JRuleStopMoveValue getValueFromString(String value) {
        if (value.equals("STOP")) {
            return STOP;
        }
        if (value.equals("MOVE")) {
            return MOVE;
        }
        return UNDEF;
    }

    @Override
    public String asStringValue() {
        return name();
    }
}
