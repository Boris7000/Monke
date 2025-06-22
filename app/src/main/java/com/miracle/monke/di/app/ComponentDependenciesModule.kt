package com.miracle.monke.di.app

import com.miracle.monke.di.authorized.AuthorizedComponent
import com.miracle.monke.di.unauthorized.UnauthorizedComponent
import com.miracle.urbanmedictest.di.common.ComponentDependencies
import com.miracle.urbanmedictest.di.common.ComponentDependenciesKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ComponentDependenciesModule {

    @Binds
    @IntoMap
    @ComponentDependenciesKey(AuthorizedComponent.Dependencies::class)
    abstract fun provideAuthorizedComponentDependencies(appComponent: AppComponent): ComponentDependencies

    @Binds
    @IntoMap
    @ComponentDependenciesKey(UnauthorizedComponent.Dependencies::class)
    abstract fun provideUnauthorizedComponentDependencies(appComponent: AppComponent): ComponentDependencies

}