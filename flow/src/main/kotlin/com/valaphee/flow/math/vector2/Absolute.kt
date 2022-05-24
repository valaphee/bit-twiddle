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

package com.valaphee.flow.math.vector2

import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.flow.DataPath
import com.valaphee.flow.DataPathException
import com.valaphee.flow.StatelessNode
import com.valaphee.flow.spec.In
import com.valaphee.flow.spec.Node
import com.valaphee.flow.spec.Out
import com.valaphee.foundry.math.Double2
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Int2
import kotlin.math.absoluteValue

/**
 * @author Kevin Ludwig
 */
@Node("Math/Vector 2/Absolute")
class Absolute(
    @get:In ("X"  , Vec2) @get:JsonProperty("in" ) val `in`: DataPath,
    @get:Out("|X|", Vec2) @get:JsonProperty("out") val out : DataPath
) : StatelessNode() {
    override fun initialize() {
        out.set {
            val `in` = `in`.get()
            when (`in`) {
                is Int2    -> `in`.abs()
                is Float2  -> `in`.abs()
                is Double2 -> `in`.abs()
                else       -> DataPathException.invalidTypeInExpression("|$`in`|")
            }
        }
    }

    companion object {
        private fun Int2.abs() = Int2(x.absoluteValue, y.absoluteValue)

        private fun Float2.abs() = Float2(x.absoluteValue, y.absoluteValue)

        private fun Double2.abs() = Double2(x.absoluteValue, y.absoluteValue)
    }
}
