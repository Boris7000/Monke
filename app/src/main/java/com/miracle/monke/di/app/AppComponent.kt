package com.miracle.monke.di.app

import android.app.Application
import com.miracle.monke.App
import com.miracle.monke.di.authorized.AuthorizedComponent
import com.miracle.monke.di.unauthorized.UnauthorizedComponent
import com.miracle.monke.presentation.root.RootScreenViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ComponentDependenciesModule::class
    ]
)
interface AppComponent:
    UnauthorizedComponent.Dependencies,
    AuthorizedComponent.Dependencies {

    fun inject(app: App)

    fun provideMainActivityViewModelFactory():RootScreenViewModel.Factory

    @Component.Builder
    interface AppComponentBuilder {
        @BindsInstance
        fun application(application: Application): AppComponentBuilder

        fun build(): AppComponent
    }

}

