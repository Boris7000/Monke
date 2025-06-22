package com.miracle.monke.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.miracle.monke.domain.authorization.usecase.LogInUseCase
import javax.inject.Inject

class LoginScreenViewModel(
    private val logInUseCase: LogInUseCase
):ViewModel() {

    fun onEvent(event:UIEvent){
        when(event){
            UIEvent.LogIn -> {
                logInUseCase.invoke()
            }
        }
    }

    sealed class UIEvent {
        object LogIn: UIEvent()
    }

    class Factory @Inject constructor(
        private val logInUseCase: LogInUseCase,
    ) {
        fun  create():LoginScreenViewModel{
            return LoginScreenViewModel(
                logInUseCase = logInUseCase
            )
        }
    }
}