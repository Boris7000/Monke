package com.miracle.monke.di.authorized

import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import com.miracle.monke.presentation.authorized.AuthorizedScreenViewModel
import com.miracle.urbanmedictest.di.common.ComponentDependencies
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorized()

@Authorized
@Component(
    modules = [
        AuthorizedModule::class
    ],
    dependencies = [AuthorizedComponent.Dependencies::class]
)
interface AuthorizedComponent {

    fun provideAuthorizedScreenViewModelFactory(): AuthorizedScreenViewModel.Factory

    interface Dependencies: ComponentDependencies {
        fun authorizationPreferncesProvier(): AuthorizationPreferncesProvier
    }
}