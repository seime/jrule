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
package org.openhab.automation.jrule.items;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.rules.value.JRulePercentValue;
import org.openhab.automation.jrule.rules.value.JRuleStopMoveValue;
import org.openhab.automation.jrule.rules.value.JRuleUpDownValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleRollershutterItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleRollershutterItem extends JRuleItem<JRulePercentValue> {
    String STOP = JRuleStopMoveValue.STOP.asStringValue();
    String MOVE = JRuleStopMoveValue.MOVE.asStringValue();
    String UP = JRuleUpDownValue.UP.asStringValue();
    String DOWN = JRuleUpDownValue.DOWN.asStringValue();

    static JRuleRollershutterItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleRollershutterItem.class);
    }

    /**
     * Sends a percent command.
     * 
     * @param command as number via JRulePercentValue will be send.
     */
    void sendCommand(int command);

    /**
     * Sends an UP or DOWN
     * 
     * @param command true will send an JRuleUpDownValue.UP, false an DOWN.
     */
    void sendCommand(boolean command);

    /**
     * Sends an UP or DOWN
     * 
     * @param value true will send an JRuleUpDownValue.UP, false an DOWN.
     */
    void postUpdate(boolean value);

    /**
     * Sends a percent update.
     * 
     * @param value as number via JRulePercentValue will be send.
     */
    void postUpdate(int value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRulePercentValue.class;
    }
}
