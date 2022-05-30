/*
 * Copyright (c) 2022, Valaphee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.valaphee.flow.graph

import eu.mihosoft.vrl.workflow.Connector
import eu.mihosoft.vrl.workflow.VFlow
import eu.mihosoft.vrl.workflow.fx.FXNewConnectionSkin
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory
import javafx.scene.Parent

/**
 * @author Kevin Ludwig
 */
class NewConnectionSkin(
    skinFactory: FXSkinFactory,
    parent: Parent,
    sender: Connector,
    flow: VFlow,
    type: String
) : FXNewConnectionSkin(skinFactory, parent, sender, flow, type) {
    override fun initSenderAndReceiver() {
        super.initSenderAndReceiver()

        receiverConnectorUI.isVisible = false
    }
}