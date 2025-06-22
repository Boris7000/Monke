package com.miracle.monke.di.app

import android.app.Application
import android.content.Context
import com.miracle.monke.data.preferences.SharedPrefs
import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import com.miracle.monke.domain.authorization.usecase.GetAuthorizationFlowUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(
        application: Application
    ): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideSharedPrefs(
        context: Context
    ): SharedPrefs {
        return SharedPrefs(context)
    }

    @Singleton
    @Provides
    fun provideAuthorizationPreferncesProvier(
        sharedPrefs: SharedPrefs
    ): AuthorizationPreferncesProvier {
        return sharedPrefs
    }


    ///////

    @Singleton
    @Provides
    fun provideGetAuthorizationFlowUseCase(
        authorizationPreferncesProvier: AuthorizationPreferncesProvier
    ): GetAuthorizationFlowUseCase {
        return GetAuthorizationFlowUseCase(authorizationPreferncesProvier)
    }

}


