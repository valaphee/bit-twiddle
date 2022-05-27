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

package com.valaphee.flow.node.math.vector3

import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.flow.Scope
import com.valaphee.flow.node.Node
import com.valaphee.flow.node.Num
import com.valaphee.flow.spec.In
import com.valaphee.flow.spec.NodeType
import com.valaphee.flow.spec.Out

/**
 * @author Kevin Ludwig
 */
@NodeType("Math/Vector 3/Distance")
class Distance(
    type: String,
    @get:In ("p"      , Vec3) @get:JsonProperty("in_p") val inP: Int,
    @get:In ("q"      , Vec3) @get:JsonProperty("in_q") val inQ: Int,
    @get:Out("d(p, q)", Num ) @get:JsonProperty("out" ) val out: Int
) : Node(type) {
    override fun initialize(scope: Scope) {
        val inP = scope.dataPath(inP)
        val inQ = scope.dataPath(inQ)
        val out = scope.dataPath(out)

        out.set { vector3Op(inP.get(), inQ.get(), { a, b -> a.toFloat3().distance(b.toFloat3()) }, { a, b -> a.distance(b) }, { a, b -> a.distance(b) }, scope.objectMapper) }
    }
}
