package com.miracle.monke.domain.authorization.preferences

import kotlinx.coroutines.flow.Flow

interface AuthorizationPreferncesProvier {

    fun saveAuthorization(authorization: Boolean)

    fun getAuthorization(): Boolean

    suspend fun getAuthorizationFlow(): Flow<Boolean>

}