package com.miracle.monke.di.unauthorized

import com.miracle.monke.di.authorized.Authorized
import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import com.miracle.monke.domain.authorization.usecase.LogInUseCase
import dagger.Module
import dagger.Provides

@Module
class UnauthorizedModule {

    @Unauthorized
    @Provides
    fun provideLogInUseCase(
        authorizationPreferncesProvier: AuthorizationPreferncesProvier
    ): LogInUseCase {
        return LogInUseCase(authorizationPreferncesProvier)
    }

}

