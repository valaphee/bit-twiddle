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

package com.valaphee.cran.radio.modulation

import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.cran.Scope
import com.valaphee.cran.node.Arr
import com.valaphee.cran.node.Int
import com.valaphee.cran.node.Node
import com.valaphee.cran.radio.Deinterleave
import com.valaphee.cran.spec.In
import com.valaphee.cran.spec.NodeType
import com.valaphee.cran.spec.Out
import kotlin.math.PI
import kotlin.math.atan2

/**
 * @author Kevin Ludwig
 */
@NodeType("Radio/Demodulation/FM")
class FmDemodulation(
    type: String,
    @get:In ("Deviation"  , Int) @get:JsonProperty("in_deviation"  ) val inDeviation : Int,
    @get:In ("Sample Rate", Int) @get:JsonProperty("in_sample_rate") val inSampleRate: Int,
    @get:In (""           , Arr) @get:JsonProperty("in"            ) val `in`      : Int,
    @get:Out(""           , Arr) @get:JsonProperty("out"           ) val out       : Int
) : Node(type) {
    private val states = mutableMapOf<Scope, State>()

    override fun initialize(scope: Scope) {
        val state = states.getOrPut(scope) { State() }
        val inDeviation = scope.dataPath(inDeviation)
        val inSampleRate = scope.dataPath(inSampleRate)
        val `in` = scope.dataPath(`in`)
        val out = scope.dataPath(out)

        out.set {
            val (inRe, inIm) = Deinterleave.deinterleave(`in`.getOfType(), 2)
            val size = inRe.size
            var prevRe = if (state.prevRe.isNaN()) inRe.first().also { state.prevRe = it } else state.prevRe
            var prevIm = if (state.prevIm.isNaN()) inIm.first().also { state.prevIm = it } else state.prevIm
            val outRe = FloatArray(size)
            val outIm = FloatArray(size)
            val gain = inSampleRate.getOfType<Int>() / (2 * PI.toFloat() * inDeviation.getOfType<Int>())
            repeat(size) {
                val _inRe = inRe[it]
                val _inIm = inIm[it]
                val re = _inRe * prevRe + _inIm * prevIm
                val im = _inIm * prevRe - _inRe * prevIm
                outRe[it] = atan2(im, re) * gain
                outIm[it] = im
                prevRe = _inRe
                prevIm = _inIm
            }
            /*Interleave.interleave(*/outRe/*, outIm)*/
        }
    }

    override fun shutdown(scope: Scope) {
        states.remove(scope)
    }

    private class State {
        var prevRe = Float.NaN
        var prevIm = Float.NaN
    }
}
