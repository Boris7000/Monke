package com.miracle.monke.di.unauthorized

import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import com.miracle.monke.presentation.login.LoginScreenViewModel
import com.miracle.urbanmedictest.di.common.ComponentDependencies
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Unauthorized()

@Unauthorized
@Component(
    modules = [UnauthorizedModule::class],
    dependencies = [UnauthorizedComponent.Dependencies::class]
)
interface UnauthorizedComponent {

    fun provideLoginScreenViewModellFactory(): LoginScreenViewModel.Factory

    interface Dependencies: ComponentDependencies {
        fun authorizationPreferncesProvier(): AuthorizationPreferncesProvier
    }
}