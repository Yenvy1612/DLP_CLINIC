package com.acare.clinic.agent.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AgentApiService {

    @POST("api/agents/register")
    suspend fun registerAgent(
        @Body request: AgentRegisterRequest
    ): ApiResponse<AgentStatusResponse>

    @POST("api/agents/{deviceId}/heartbeat")
    suspend fun sendHeartbeat(
        @Path("deviceId") deviceId: String,
        @Body request: HeartbeatRequest
    ): ApiResponse<String>

    @GET("api/agents/{deviceId}/status")
    suspend fun getAgentStatus(
        @Path("deviceId") deviceId: String
    ): ApiResponse<AgentStatusResponse>

    @GET("api/agents/policy")
    suspend fun getPolicy(): ApiResponse<AgentPolicyResponse>

    @POST("api/agent-events")
    suspend fun sendAgentEvent(
        @Body request: AgentEventRequest
    ): ApiResponse<String>
}