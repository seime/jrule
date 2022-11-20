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

import java.util.Set;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

/**
 * The {@link JRuleGroupItem} JRule Item
 *
 * @author Robert Delbrück - Initial contribution
 */
public interface JRuleGroupItem<T extends JRuleValue> extends JRuleItem<T> {
    @Deprecated
    default Set<String> members() {
        return JRuleEventHandler.get().getGroupMemberNames(getName(), false);
    }

    default Set<JRuleItem<? extends JRuleValue>> memberItems() {
        return memberItems(false);
    }

    default Set<JRuleItem<? extends JRuleValue>> memberItems(boolean recursive) {
        return JRuleEventHandler.get().getGroupMemberItems(getName(), recursive);
    }

    default void sendCommand(String value) {
        members().forEach(m -> JRuleEventHandler.get().sendCommand(m, new JRuleStringValue(value)));
    }

    default void postUpdate(String value) {
        members().forEach(m -> JRuleEventHandler.get().postUpdate(m, new JRuleStringValue(value)));
    }

    default void sendCommand(T value) {
        members().forEach(m -> JRuleEventHandler.get().sendCommand(m, value));
    }

    default void postUpdate(T value) {
        members().forEach(m -> JRuleEventHandler.get().postUpdate(m, value));
    }

    @Override
    default boolean isGroup() {
        return true;
    }
}
