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
 * The {@link JRulePlayPauseValue} JRule Command
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public enum JRulePlayPauseValue implements JRuleValue {
    PLAY,
    PAUSE,
    UNDEF;

    public static JRulePlayPauseValue getValueFromString(String value) {
        if (value.equals(PLAY.name())) {
            return PLAY;
        }
        if (value.equals(PAUSE.name())) {
            return PAUSE;
        }
        return UNDEF;
    }

    @Override
    public String asStringValue() {
        return name();
    }
}
