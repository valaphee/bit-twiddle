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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.protobuf.ByteString
import com.valaphee.flow.GraphManager
import com.valaphee.flow.Scope
import com.valaphee.flow.spec.Spec
import com.valaphee.svc.graph.v1.DeleteGraphRequest
import com.valaphee.svc.graph.v1.DeleteGraphResponse
import com.valaphee.svc.graph.v1.GetSpecRequest
import com.valaphee.svc.graph.v1.GetSpecResponse
import com.valaphee.svc.graph.v1.GraphServiceGrpc.GraphServiceImplBase
import com.valaphee.svc.graph.v1.ListGraphRequest
import com.valaphee.svc.graph.v1.ListGraphResponse
import com.valaphee.svc.graph.v1.UpdateGraphRequest
import com.valaphee.svc.graph.v1.UpdateGraphResponse
import io.github.classgraph.ClassGraph
import io.grpc.stub.StreamObserver
import org.apache.logging.log4j.LogManager
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Singleton
class GraphServiceImpl @Inject constructor(
    private val objectMapper: ObjectMapper
) : GraphServiceImplBase(), GraphManager {
    private val spec: Spec
    private val graphs = mutableSetOf<GraphImpl>()

    init {
        ClassGraph().scan().use {
            spec = Spec(it.getResourcesMatchingWildcard("spec.*.dat").urLs.flatMap { objectMapper.readValue<Spec>(it).nodes })
            graphs += it.getResourcesMatchingWildcard("**.flw").urLs.map { objectMapper.readValue(it) }
        }

        spec.nodes.forEach { log.info("Built-in node {} found", it.name) }
        graphs.forEach { log.info("Built-in graph node {} found", it.name) }
    }

    override fun getGraph(name: String) = graphs.find { it.name == name }

    override fun getSpec(request: GetSpecRequest, responseObserver: StreamObserver<GetSpecResponse>) {
        responseObserver.onNext(GetSpecResponse.newBuilder().setSpec(ByteString.copyFrom(objectMapper.writeValueAsBytes(Spec(spec.nodes + graphs.map { it.toSpec() })))).build())
        responseObserver.onCompleted()
    }

    override fun listGraph(request: ListGraphRequest, responseObserver: StreamObserver<ListGraphResponse>) {
        responseObserver.onNext(ListGraphResponse.newBuilder().setGraphs(ByteString.copyFrom(objectMapper.writeValueAsBytes(graphs))).build())
        responseObserver.onCompleted()
    }

    override fun updateGraph(request: UpdateGraphRequest, responseObserver: StreamObserver<UpdateGraphResponse>) {
        val graph = objectMapper.readValue<GraphImpl>(request.graph.toByteArray())
        graphs.find { it.id == graph.id }?.let {
            graphs -= it
            it.shutdown()
        }
        graphs += graph
        graph.initialize(Scope(objectMapper, this))
        responseObserver.onNext(UpdateGraphResponse.getDefaultInstance())
        responseObserver.onCompleted()
    }

    override fun deleteGraph(request: DeleteGraphRequest, responseObserver: StreamObserver<DeleteGraphResponse>) {
        val graphId = UUID.fromString(request.graphId)
        graphs.find { it.id == graphId }?.let {
            graphs -= it
            it.shutdown()
        }
        responseObserver.onNext(DeleteGraphResponse.getDefaultInstance())
        responseObserver.onCompleted()
    }

    companion object {
        private val log = LogManager.getLogger(GraphServiceImpl::class.java)
    }
}
