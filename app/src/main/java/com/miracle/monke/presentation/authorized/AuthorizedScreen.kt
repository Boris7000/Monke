package com.miracle.monke.presentation.authorized

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miracle.monke.presentation.common.ScreenRoot
import com.miracle.monke.presentation.home.HomeScreen
import com.miracle.monke.presentation.home.HomeScreenViewModel
import com.miracle.monke.presentation.settings.SettingsScreen
import com.miracle.monke.presentation.settings.SettingsScreenViewModel

@Composable
fun AuthorizedScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthorizedScreenViewModel
){

    val sreenState = viewModel.sreenState

    ScreenRoot(
        modifier = modifier,
        content = {
            val screenModifier = Modifier.fillMaxSize()
            when(sreenState){
                AuthorizedScreenViewModel.ScreenState.None -> Unit
                is AuthorizedScreenViewModel.ScreenState.Home -> {
                    val homeScreenViewModel =
                        viewModel<HomeScreenViewModel>(
                            viewModelStoreOwner = sreenState.node
                        ) {
                            HomeScreenViewModel(sreenState.node)
                        }

                    HomeScreen(
                        modifier = screenModifier,
                        viewModel = homeScreenViewModel,
                        onLogOut = {viewModel.onEvent(AuthorizedScreenViewModel.UIEvent.LogOut)}
                    )
                }
                is AuthorizedScreenViewModel.ScreenState.Settings -> {
                    val settingsScreenViewModel =
                        viewModel<SettingsScreenViewModel>(
                            viewModelStoreOwner = sreenState.node
                        ) {
                            SettingsScreenViewModel()
                        }

                    SettingsScreen(
                        modifier = screenModifier,
                        viewModel = settingsScreenViewModel
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = sreenState is AuthorizedScreenViewModel.ScreenState.Home,
                    onClick = { viewModel.onEvent(AuthorizedScreenViewModel.UIEvent.HomeSelect) },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "home") }
                )
                NavigationBarItem(
                    selected = sreenState is AuthorizedScreenViewModel.ScreenState.Settings,
                    onClick = { viewModel.onEvent(AuthorizedScreenViewModel.UIEvent.SettingsSelect) },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "settings") }
                )
            }
        }
    )
}