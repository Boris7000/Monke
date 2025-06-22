package com.miracle.monke.domain.authorization.usecase

import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import kotlinx.coroutines.flow.Flow

class GetAuthorizationFlowUseCase(private val prefs: AuthorizationPreferncesProvier) {
    suspend fun invoke(): Flow<Boolean> {
        return prefs.getAuthorizationFlow()
    }
}