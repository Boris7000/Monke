package com.miracle.monke.presentation.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailsScreenViewModel(
    val id:Int
):ViewModel() {

    var exitState by mutableStateOf<ExitState>(ExitState.DEFAULT)
        private set

    fun onEvent(event: UIEvent) {
        when (event) {
            UIEvent.ConfirmExit -> {
                exitState = ExitState.Confirmed
            }
            UIEvent.DismissExit -> {
                exitState = ExitState.Idle
            }
            UIEvent.Exit -> {
                exitState = ExitState.WaitingForConfirmation
            }
        }
    }

    sealed class UIEvent {
        object Exit: UIEvent()
        object ConfirmExit: UIEvent()
        object DismissExit: UIEvent()
    }

    sealed class ExitState {
        object Idle: ExitState()
        object WaitingForConfirmation: ExitState()
        object Confirmed: ExitState()

        companion object {
            internal val DEFAULT = Idle
        }
    }

    class Factory(
        val id:Int
    ) {
        fun  create(): DetailsScreenViewModel {
            return DetailsScreenViewModel(id)
        }
    }
}