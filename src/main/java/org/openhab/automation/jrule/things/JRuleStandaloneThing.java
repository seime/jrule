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
package org.openhab.automation.jrule.things;

/**
 * The {@link JRuleStandaloneThing} thing represents a thing that is standalone (not a bridge nor connected to a bridge)
 *
 * @author Arne Seime - Initial contribution
 */
public abstract class JRuleStandaloneThing extends JRuleAbstractThing {
    public JRuleStandaloneThing(String thingUID) {
        super(thingUID);
    }

    public static JRuleStandaloneThing forName(String thingUID) {
        return JRuleThingRegistry.get(thingUID, JRuleStandaloneThing.class);
    }
}
