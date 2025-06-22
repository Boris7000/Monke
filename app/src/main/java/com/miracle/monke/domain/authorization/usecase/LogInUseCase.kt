package com.miracle.monke.domain.authorization.usecase

import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier

class LogInUseCase(private val prefs: AuthorizationPreferncesProvier) {
    fun invoke(){
        prefs.saveAuthorization(true)
    }
}