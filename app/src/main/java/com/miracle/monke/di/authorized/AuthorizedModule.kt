package com.miracle.monke.di.authorized

import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import com.miracle.monke.domain.authorization.usecase.LogOutUseCase
import dagger.Module
import dagger.Provides

@Module
class AuthorizedModule {

    @Authorized
    @Provides
    fun provideLogOutUseCase(
        authorizationPreferncesProvier: AuthorizationPreferncesProvier
    ): LogOutUseCase {
        return LogOutUseCase(authorizationPreferncesProvier)
    }

}

