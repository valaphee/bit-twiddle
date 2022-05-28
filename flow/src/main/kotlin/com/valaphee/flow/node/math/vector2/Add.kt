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

package com.valaphee.flow.node.math.vector2

import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.flow.Scope
import com.valaphee.flow.node.Node
import com.valaphee.flow.spec.In
import com.valaphee.flow.spec.NodeType
import com.valaphee.flow.spec.Out

/**
 * @author Kevin Ludwig
 */
@NodeType("Math/Vector 2/Add")
class Add(
    type: String,
    @get:In ("A"    , Vec2) @get:JsonProperty("in_a") val inA: Int,
    @get:In ("B"    , Vec2) @get:JsonProperty("in_b") val inB: Int,
    @get:Out("A + B", Vec2) @get:JsonProperty("out" ) val out: Int
) : Node(type) {
    override fun initialize(scope: Scope) {
        val inA = scope.dataPath(inA)
        val inB = scope.dataPath(inB)
        val out = scope.dataPath(out)

        out.set { vector2Op(inA.get(), inB.get(), { a, b -> a + b }, { a, b -> a + b }, { a, b -> a + b }, scope.objectMapper) }
    }
}