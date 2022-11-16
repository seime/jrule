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
import org.openhab.automation.jrule.rules.value.JRuleHsbValue;
import org.openhab.automation.jrule.rules.value.JRuleIncreaseDecreaseValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleColorItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleColorItem extends JRuleItem<JRuleHsbValue> {
    static JRuleColorItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName, JRuleColorItem.class);
    }

    void sendCommand(JRuleOnOffValue command);

    void sendCommand(JRuleIncreaseDecreaseValue command);

    void postUpdate(JRuleHsbValue value);

    void postUpdate(JRuleOnOffValue state);

    void postUpdate(int value);

    @Override
    default Class<? extends JRuleValue> getDefaultValueClass() {
        return JRuleHsbValue.class;
    }
}
