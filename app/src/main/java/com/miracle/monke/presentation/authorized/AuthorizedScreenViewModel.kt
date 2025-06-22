package com.miracle.monke.presentation.authorized

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miracle.monke.domain.authorization.usecase.LogOutUseCase
import com.miracle.monke.presentation.home.HomeScreenNode
import com.miracle.monke.presentation.settings.SettingsScreenNode
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthorizedScreenViewModel(
    private val logOutUseCase: LogOutUseCase,
    private val authorizedScreenNode: AuthorizedScreenNode
):ViewModel() {

    var sreenState by mutableStateOf<ScreenState>(ScreenState.None)
        private set

    init {
        fetchScreen()
    }

    private fun fetchScreen(){
        viewModelScope.launch {
            authorizedScreenNode.getCurrentNodeFlow().collect { currentNode ->
                sreenState = when(currentNode){
                    is HomeScreenNode ->{
                        ScreenState.Home(currentNode)
                    }

                    is SettingsScreenNode ->{
                        ScreenState.Settings(currentNode)
                    }
                    else -> {
                        ScreenState.None
                    }
                }
            }
        }
    }

    fun onEvent(event:UIEvent){
        when(event){
            UIEvent.HomeSelect -> {
                authorizedScreenNode.select(AuthorizedScreenNode.Screens.HOME, singleTop = true)
            }
            UIEvent.SettingsSelect -> {
                authorizedScreenNode.select(AuthorizedScreenNode.Screens.SETTINGS, singleTop = true)
            }
            UIEvent.LogOut -> {
                logOutUseCase.invoke()
            }
        }
    }

    sealed class UIEvent {
        object HomeSelect:UIEvent()
        object SettingsSelect:UIEvent()
        object LogOut:UIEvent()
    }

    sealed class ScreenState {
        object None:ScreenState()
        data class Home(val node: HomeScreenNode):ScreenState()
        data class Settings(val node: SettingsScreenNode):ScreenState()
    }

    class Factory @Inject constructor(
        private val logOutUseCase: LogOutUseCase
    ) {
        fun  create(
            authorizedScreenNode: AuthorizedScreenNode
        ): AuthorizedScreenViewModel {
            return AuthorizedScreenViewModel(
                logOutUseCase = logOutUseCase,
                authorizedScreenNode = authorizedScreenNode
            )
        }
    }
}