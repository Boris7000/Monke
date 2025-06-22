package com.miracle.monke.monke

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

abstract class Node: ViewModelStoreOwner {

    val id:Long = ++createdCount
    var state:State = State.CREATED

    companion object {
        var createdCount:Long = 0
        fun simpleNode() = object:Node(){}
    }

    open fun toJSON():String {
        return "{\"type\": \"Node\", \"id\": \"$id\", \"state\": \"$state\"}"
    }

    override fun toString():String {
        //return "Node(id=$id, state=$state)"
        return toJSON()
    }

    enum class State {
        CREATED,
        SHOWING,
        PAUSED,
        REMOVED
    }

    final fun show(){
        if (state==State.CREATED||state==State.PAUSED) {
            state = State.SHOWING
            onShow()
        }
    }

    final fun pause(){
        if(state==State.SHOWING){
            state = State.PAUSED
            onPause()
        }
    }

    final fun remove(){
        if(state!=State.REMOVED) {
            state = State.REMOVED
            cachedViewModelStore.clear()
            onRemove()
        }
    }


    open fun onShow(){}

    open fun onPause(){}

    open fun onRemove(){}

    private val cachedViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    public override val viewModelStore: ViewModelStore
        get() {
            check(state != State.CREATED) {
                "You cannot access the ContainerNode's ViewModels until it is added to " +
                        "the node tree."
            }
            check(state != State.REMOVED) {
                "You cannot access the ContainerNode's ViewModels after the " +
                        "ContainerNode is removed."
            }
            return cachedViewModelStore
        }
}

