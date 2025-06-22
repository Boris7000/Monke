package com.miracle.monke.monke

import android.util.Log
import com.miracle.monke.monke.MapContainerNode.NodeHolder
import com.miracle.monke.monke.MapContainerNode.OnSelectedKeyChangeListener
import com.miracle.monke.monke.Node.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

open class MapContainerNode<T>:ContainerNode {

    private val nodeFabricsMap:Map<T,NodeFabric>
    private val history:ArrayDeque<T> = ArrayDeque()
    private val nodeHoldersMap:MutableMap<T,NodeHolder> = mutableMapOf()

    private val currentNodeHolder:NodeHolder get() = nodeHoldersMap.getValue(history.last())
    private fun currentNodeHolderOrNull():NodeHolder? = nodeHoldersMap.get(history.lastOrNull())
    override val currentNode:Node get() = currentNodeHolder.node
    override fun currentNodeOrNull():Node? = currentNodeHolderOrNull()?.node

    private val onSelectedKeyChangeListeners: MutableList<OnSelectedKeyChangeListener<T>> = mutableListOf()
    fun registerOnSelectedKeyChangeListener(listener: OnSelectedKeyChangeListener<T>) {
        if (!onSelectedKeyChangeListeners.contains(listener)) {
            onSelectedKeyChangeListeners.add(listener)
        }
    }
    fun unregisterOnSelectedKeyChangeListener(listener: OnSelectedKeyChangeListener<T>) {
        onSelectedKeyChangeListeners.remove(listener)
    }
    suspend fun getSelectedKeyFlow(): Flow<T?> = callbackFlow {
        val listener = OnSelectedKeyChangeListener<T> { key->
            trySend(key)
        }
        send(history.lastOrNull())
        registerOnSelectedKeyChangeListener(listener)
        awaitClose {
            unregisterOnSelectedKeyChangeListener(listener)
        }
    }
    protected fun notifyOnSelectedKeyChangeListeners(){
        val key = history.lastOrNull()
        for (callback in onSelectedKeyChangeListeners) {
            callback.onSelectedKeyChanged(key)
        }
    }

    private fun getOrCreateNodeHolder(key:T):NodeHolder?{
        var nodeHolder:NodeHolder? = nodeHoldersMap[key]
        if (nodeHolder==null){
            val fabric = nodeFabricsMap[key]
            if (fabric!=null){
                nodeHolder = NodeHolder(fabric.create())
                nodeHoldersMap[key] = nodeHolder
            }
        }
        return nodeHolder
    }

    constructor(){
        this.nodeFabricsMap = emptyMap()
    }

    constructor(selectedByDefault:T? = null, nodeFabricsMap:Map<T,NodeFabric>){
        this.nodeFabricsMap = nodeFabricsMap
        if (selectedByDefault!=null){
            select(selectedByDefault)
        }
    }

    constructor(selectedByDefault:T, vararg pairs:Pair<T,NodeFabric>):this(selectedByDefault,
        pairs.associate { (key, nodeFabric) -> key to nodeFabric }
    )

    private fun selectInternal(key:T, singleTop:Boolean = false, removeOther:Boolean = false):Boolean {
        val nodeHolder:NodeHolder? = getOrCreateNodeHolder(key)
        if(nodeHolder!=null){
            if (removeOther){
                if(history.isNotEmpty()) {
                    history.filter { it != key }.distinct().forEach { removingKey ->
                        val removing = nodeHoldersMap.remove(removingKey)
                        if (removing != null) {
                            removing.node.remove()
                        }
                    }
                    history.clear()
                }
                history.addLast(key)
                return true
            } else {
                if(history.isNotEmpty()){
                    val lastKey = history.last()
                    if(lastKey!=key) {
                        currentNode.pause()
                    }
                    if (singleTop && history.count { it==key }>0){
                        history.removeIf { it==key }
                        history.addLast(key)
                        return true
                    } else {
                        if(lastKey!=key){
                            history.addLast(key)
                            return true
                        }
                    }
                } else {
                    history.addLast(key)
                    return true
                }
            }
        }
        return false
    }

    fun select(key:T, singleTop:Boolean = false, removeOther:Boolean = false):Boolean {
        val previousKey = history.lastOrNull()
        val result = selectInternal(key, singleTop, removeOther)
        if (result){
            if (state==State.SHOWING) currentNode.show()
            if(previousKey!=history.lastOrNull()){
                notifyOnCurrentNodeChangeListeners()
                notifyOnSelectedKeyChangeListeners()
            }
        }
        return result
    }

    fun select(key:T, singleTop:Boolean = false, removeOther:Boolean = false, fabric:()->Node):Boolean {
        if (!nodeHoldersMap.containsKey(key)){
            nodeHoldersMap.put(key,NodeHolder(fabric()))
        }
        return select(key, singleTop, removeOther)
    }

    private fun backInternal(ignoreNestedContainers:Boolean):Boolean {
        if (history.isEmpty()) return false

        val pausing = currentNode

        if (!ignoreNestedContainers && pausing is ContainerNode) {
            if (pausing.back()) return true
        }

        if (history.size > 1) {
            val currentKey = history.last()
            if (history.count {it==currentKey}>1){
                pausing.pause()
            } else {
                pausing.remove()
                nodeHoldersMap.remove(currentKey)
            }
            history.removeLast()
            return true
        }

        return false
    }

    override fun back(ignoreNestedContainers:Boolean):Boolean {
        val result = backInternal(ignoreNestedContainers)
        if (result){
            if (state==State.SHOWING) currentNode.show()
            notifyOnCurrentNodeChangeListeners()
        }
        return result
    }

    override fun onShow(){
        super.onShow()
        currentNodeOrNull()?.show()
    }

    override fun onPause(){
        super.onPause()
        currentNodeOrNull()?.pause()
    }

    override fun onRemove(){
        super.onRemove()
        for(nh in nodeHoldersMap){
            nh.value.node.remove()
        }
    }

    override fun toJSON():String {
        return "{\"type\": \"MapContainerNode\", \"id\": \"$id\", \"state\": \"$state\", \"history\": ${history.map { "\"$it\"" }}, \"nodes\": ${nodeHoldersMap.map { "{\"key\": ${"\"${it.key}\""}, \"node\": ${it.value.node.toJSON()}}" }}}"
    }

    override fun toString():String {
        //return "MapContainerNode(id=$id, state=$state, history=$history, nodes=${nodeHoldersMap.mapValues { it.value.node }})"
        return toJSON()
    }

    inner class NodeHolder(
        val node:Node
    ){
        override fun toString():String{
            return "NodeHolder(node=$node)"
        }
    }

    fun interface NodeFabric {
        fun create():Node
    }

    fun interface OnSelectedKeyChangeListener<T> {
        fun onSelectedKeyChanged(key:T?)
    }
}


