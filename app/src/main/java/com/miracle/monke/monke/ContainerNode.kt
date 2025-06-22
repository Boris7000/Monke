package com.miracle.monke.monke

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class ContainerNode:Node() {

    abstract val currentNode:Node
    abstract fun currentNodeOrNull():Node?

    private val onCurrentNodeChangeListeners: MutableList<OnCurrentNodeChangeListener> = mutableListOf()
    final fun registerOnCurrentNodeChangeListener(listener: OnCurrentNodeChangeListener) {
        if (!onCurrentNodeChangeListeners.contains(listener)) {
            onCurrentNodeChangeListeners.add(listener)
        }
    }
    final fun unregisterOnCurrentNodeChangeListener(listener: OnCurrentNodeChangeListener) {
        onCurrentNodeChangeListeners.remove(listener)
    }
    suspend final fun getCurrentNodeFlow(): Flow<Node?> = callbackFlow {
        val listener = OnCurrentNodeChangeListener { node->
            trySend(node)
        }
        send(currentNodeOrNull())
        registerOnCurrentNodeChangeListener(listener)
        awaitClose {
            unregisterOnCurrentNodeChangeListener(listener)
        }
    }
    protected final fun notifyOnCurrentNodeChangeListeners(){
        val node = currentNodeOrNull()
        for (callback in onCurrentNodeChangeListeners) {
            callback.onCurrentNodeChanged(node)
        }
    }

    open fun back(ignoreNestedContainers:Boolean=false):Boolean = false


    override fun toString():String {
        return "ContainerNode(id=$id, state=$state)"
    }

    fun interface OnCurrentNodeChangeListener {
        fun onCurrentNodeChanged(node:Node?)
    }
}