package com.miracle.monke.presentation.root

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.miracle.monke.domain.authorization.usecase.GetAuthorizationFlowUseCase
import com.miracle.monke.presentation.authorized.AuthorizedScreenNode
import com.miracle.monke.presentation.authorized.AuthorizedScreenViewModel.UIEvent
import com.miracle.monke.presentation.login.LoginScreenNode
import kotlinx.coroutines.launch
import javax.inject.Inject

object Logger{

    private var counter:Int = 0

    fun log(string: String){
        Log.d("Test", "step №${++counter}: "+string)
    }

}

class RootScreenViewModel(
    private val getAuthorizationFlowUseCase: GetAuthorizationFlowUseCase
):ViewModel() {

    private val node = RootScreenNode().also { it.show() }

    var sreenState by mutableStateOf<ScreenState>(ScreenState.None)
        private set

    init {
        fetchScreen()
        fetchAuthorization()
    }

    private fun fetchScreen(){
        viewModelScope.launch {
            node.getCurrentNodeFlow().collect { currentNode->
                sreenState = when(currentNode){
                    is AuthorizedScreenNode ->{
                        ScreenState.Home(currentNode)
                    }

                    is LoginScreenNode ->{
                        ScreenState.Login(currentNode)
                    }
                    else -> ScreenState.None
                }
            }
        }
    }

    fun fetchAuthorization(){
        viewModelScope.launch {
            getAuthorizationFlowUseCase.invoke().collect { authorization->
                if (authorization) {
                    node.select(RootScreenNode.Screens.AUTHORIZED, removeOther = true)
                } else {
                    node.select(RootScreenNode.Screens.LOG_IN, removeOther = true)
                }
            }
        }
    }

    fun onEvent(event:UIEvent):Boolean{
        when(event){
            UIEvent.LogIn -> {
                node.select(RootScreenNode.Screens.AUTHORIZED)
            }

            UIEvent.Back -> {
                return node.back()
            }

            UIEvent.Log -> {
                Logger.log(node.toString())
            }
        }
        return true
    }

    override fun onCleared() {
        node.remove()
    }

    sealed class UIEvent {
        object LogIn:UIEvent()
        object Back:UIEvent()
        object Log:UIEvent()
    }

    sealed class ScreenState {
        object None:ScreenState()
        data class Home(val node: AuthorizedScreenNode):ScreenState()
        data class Login(val node: LoginScreenNode):ScreenState()
    }

    class Factory @Inject constructor(
        private val getAuthorizationFlowUseCase: GetAuthorizationFlowUseCase
    ) : ViewModelProvider.Factory {

        fun  create():RootScreenViewModel{
            return RootScreenViewModel(getAuthorizationFlowUseCase)
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RootScreenViewModel::class.java)) {
                return create() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

}


