package com.miracle.monke.presentation.root

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.miracle.monke.di.authorized.AuthorizedComponent
import com.miracle.monke.di.authorized.DaggerAuthorizedComponent
import com.miracle.monke.di.unauthorized.DaggerUnauthorizedComponent
import com.miracle.monke.di.unauthorized.UnauthorizedComponent
import com.miracle.monke.monke.BackPressedHandler
import com.miracle.monke.presentation.authorized.AuthorizedScreen
import com.miracle.monke.presentation.authorized.AuthorizedScreenViewModel
import com.miracle.monke.presentation.login.LoginScreen
import com.miracle.monke.presentation.login.LoginScreenViewModel
import com.miracle.urbanmedictest.di.common.findComponentDependencies

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    viewModel:RootScreenViewModel
){

    BackPressedHandler { viewModel.onEvent(RootScreenViewModel.UIEvent.Back) }

    Box(
        modifier = modifier
    ){
        
        Button(onClick = { viewModel.onEvent(RootScreenViewModel.UIEvent.Log) }) {
            Text(text = "log")
        }
        
        val screenModifier = Modifier.fillMaxSize()
        val sreenState = viewModel.sreenState
        when(sreenState){
            RootScreenViewModel.ScreenState.None -> Unit
            is RootScreenViewModel.ScreenState.Home -> {

                val authorizedComponentDependencies = findComponentDependencies<AuthorizedComponent.Dependencies>()

                val authorizedScreenViewModel = viewModel<AuthorizedScreenViewModel>(
                    viewModelStoreOwner = sreenState.node
                ) {

                    val authorizedComponent = DaggerAuthorizedComponent
                        .builder()
                        .dependencies(authorizedComponentDependencies)
                        .build()

                    authorizedComponent.provideAuthorizedScreenViewModelFactory()
                        .create(sreenState.node)
                }

                AuthorizedScreen(
                    modifier = screenModifier,
                    viewModel = authorizedScreenViewModel
                )
            }
            is RootScreenViewModel.ScreenState.Login -> {

                val aunauthorizedComponentDependencies = findComponentDependencies<UnauthorizedComponent.Dependencies>()

                val loginScreenViewModel = viewModel<LoginScreenViewModel>(
                    viewModelStoreOwner = sreenState.node
                ) {

                    val unauthorizedComponent = DaggerUnauthorizedComponent
                        .builder()
                        .dependencies(aunauthorizedComponentDependencies)
                        .build()

                    unauthorizedComponent.provideLoginScreenViewModellFactory()
                        .create()
                }

                LoginScreen(
                    modifier = screenModifier,
                    viewModel = loginScreenViewModel
                )
            }
        }
    }
}