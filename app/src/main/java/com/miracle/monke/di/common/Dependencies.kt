package com.miracle.urbanmedictest.di.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.MapKey
import kotlin.reflect.KClass

interface ComponentDependencies

typealias ComponentDependenciesStore = Map<Class<out ComponentDependencies>, @JvmSuppressWildcards ComponentDependencies>

interface ComponentDependenciesStoreProvider {
    val componentDependenciesStore: ComponentDependenciesStore
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ComponentDependenciesKey(val value: KClass<out ComponentDependencies>)

@Composable
inline fun <reified T : ComponentDependencies> findComponentDependencies(): T {
    val localContext = LocalContext.current
    if (localContext is ComponentDependenciesStoreProvider){
        return localContext.componentDependenciesStore[T::class.java] as T
    } else {
        val applicationContext = localContext.applicationContext
        if (applicationContext is ComponentDependenciesStoreProvider){
            return applicationContext.componentDependenciesStore[T::class.java] as T
        }
    }
    throw IllegalStateException("No ComponentDependenciesStoreProvider in context dectected")
}