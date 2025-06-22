package com.miracle.monke.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.miracle.monke.presentation.content.ContentScreenNode
import com.miracle.monke.presentation.details.DetailsScreenNode
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val homeScreenNode: HomeScreenNode
):ViewModel() {

    var sreenState by mutableStateOf<ScreenState>(ScreenState.None)
        private set

    init {
        fetchScreen()
    }

    fun fetchScreen(){
        viewModelScope.launch {
            homeScreenNode.getCurrentNodeFlow().collect { currentNode ->
                sreenState = when(currentNode){
                    is ContentScreenNode ->{
                        ScreenState.Content(currentNode)
                    }

                    is DetailsScreenNode ->{
                        ScreenState.Details(currentNode)
                    }
                    else -> ScreenState.None
                }
            }
        }
    }

    fun onEvent(event: UIEvent){
        when(event){
            is UIEvent.OpenDetails -> {
                homeScreenNode.add(DetailsScreenNode(event.id))
            }

            UIEvent.Back -> {
                homeScreenNode.back()
            }
        }
    }

    sealed class UIEvent {
        data class OpenDetails(val id:Int):UIEvent()
        object Back:UIEvent()
    }

    sealed class ScreenState {
        object None:ScreenState()
        data class Content(val node: ContentScreenNode):ScreenState()
        data class Details(val node: DetailsScreenNode):ScreenState()
    }

    class Factory(
        val homeScreenNode: HomeScreenNode
    ) {
        fun  create(): HomeScreenViewModel {
            return HomeScreenViewModel(homeScreenNode)
        }
    }

}