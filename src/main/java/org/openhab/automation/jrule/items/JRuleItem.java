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

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleItem} Items
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public abstract class JRuleItem {

    private final Logger logger = LoggerFactory.getLogger(JRuleItem.class);

    protected String itemName;

    public JRuleItem(String itemName) {
        this.itemName = itemName;
    }

    public static JRuleItem forName(String itemName) throws JRuleItemNotFoundException {
        return JRuleItemRegistry.get(itemName);
    }

    public String getStateAsString() {
        return JRuleEventHandler.get().getStringValue(itemName);
    }

    public String getName() {
        return itemName;
    }

    public abstract String getLabel();

    public abstract String getType();

    public abstract String getId();

    public Optional<ZonedDateTime> lastUpdated() {
        return lastUpdated(null);
    }

    public Optional<ZonedDateTime> lastUpdated(String persistenceServiceId) {
        return JRulePersistenceExtensions.lastUpdate(itemName, persistenceServiceId);
    }

    public boolean changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    public boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.changedSince(itemName, timestamp, persistenceServiceId);
    }

    public boolean updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    public boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
        return JRulePersistenceExtensions.updatedSince(itemName, timestamp, persistenceServiceId);
    }

    /**
     * Resend the command representing the historic state at the current timestamp, ie restore a switch to the same
     * value it had at the given time
     * 
     * @param timestamp the timestamp to restore to
     * @param skipIfStateIdentical if true, do not send command if current state is the same as the historic state
     */
    public void resendCommand(ZonedDateTime timestamp, boolean skipIfStateIdentical) {
        Optional<String> stateAtTimestampOptional = JRulePersistenceExtensions.historicState(itemName, timestamp);
        logger.debug("Item {} state at {} is {}", itemName, stateAtTimestampOptional, timestamp);
        if (stateAtTimestampOptional.isPresent()) {
            String stateAtTimestamp = stateAtTimestampOptional.get();
            if (!JRuleEventHandler.get().getStringValue(itemName).equals(stateAtTimestamp) && !skipIfStateIdentical) {
                JRuleEventHandler.get().sendCommand(itemName, stateAtTimestamp);
            } else {
                logger.info("Not resending command to {} as state {} is the same as it was at {}", itemName,
                        stateAtTimestamp, timestamp);
            }
        } else {
            logger.warn("Unable to resend command for item {} since there is no historic state available at {}",
                    itemName, timestamp);
        }
    }
}
