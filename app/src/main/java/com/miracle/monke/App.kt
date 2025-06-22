package com.miracle.monke

import android.app.Application
import com.miracle.monke.di.app.AppComponent
import com.miracle.monke.di.app.AppComponentProvider
import com.miracle.monke.di.app.DaggerAppComponent
import com.miracle.urbanmedictest.di.common.ComponentDependenciesStore
import com.miracle.urbanmedictest.di.common.ComponentDependenciesStoreProvider
import javax.inject.Inject

class App: Application(),
    ComponentDependenciesStoreProvider {

    @Inject
    override lateinit var componentDependenciesStore: ComponentDependenciesStore

    companion object: AppComponentProvider {
        lateinit var appComponent: AppComponent
        override fun provideAppComponent(): AppComponent {
            return appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent =  DaggerAppComponent
            .builder()
            .application(this)
            .build().also {
                it.inject(this)
            }
    }

}