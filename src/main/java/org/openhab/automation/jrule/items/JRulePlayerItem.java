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
import org.openhab.automation.jrule.rules.value.JRuleNextPreviousValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRewindFastforwardValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRulePlayerItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRulePlayerItem extends JRuleItem<JRulePlayPauseValue> {
    String PLAY = JRulePlayPauseValue.PLAY.asStringValue();
    String PAUSE = JRulePlayPauseValue.PAUSE.asStringValue();
    String NEXT = JRuleNextPreviousValue.NEXT.asStringValue();
    String PREVIOUS = JRuleNextPreviousValue.PREVIOUS.asStringValue();
    String REWIND = JRuleRewindFastforwardValue.REWIND.asStringValue();
    String FASTFORWARD = JRuleRewindFastforwardValue.FASTFORWARD.asStringValue();

    static JRulePlayerItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRulePlayerItem.class);
    }

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRulePlayPauseValue.class;
    }

    void sendCommand(boolean command);

    void postUpdate(boolean command);
}
