package com.miracle.monke.monke

import android.util.Log
import com.miracle.monke.monke.DequeContainerNode.AddCommand
import com.miracle.monke.monke.DequeContainerNode.BackCommand
import com.miracle.monke.monke.DequeContainerNode.Command
import com.miracle.monke.monke.DequeContainerNode.HookContext
import com.miracle.monke.monke.DequeContainerNode.ReplaceCurrentCommand
import com.miracle.monke.monke.DequeContainerNode.Transaction
import com.miracle.monke.monke.Node.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

open class DequeContainerNode():ContainerNode() {

    private val nodeHolders:ArrayDeque<NodeHolder> = ArrayDeque()
    private fun <T>ArrayDeque<T>.replaceLastWith(value:T){
        this[lastIndex] = value
    }

    private val currentNodeHolder:NodeHolder get() = nodeHolders.last()
    private fun currentNodeHolderOrNull():NodeHolder? = nodeHolders.lastOrNull()
    override val currentNode:Node get() = currentNodeHolder.node
    override fun currentNodeOrNull():Node? = currentNodeHolderOrNull()?.node

    constructor(iterator:Iterator<Node>):this(){
        for(node in iterator){
            nodeHolders.addLast(NodeHolder(node))
        }
        if(state==State.SHOWING) currentNode.show()
    }

    constructor(nodes:Iterable<Node>):this(nodes.iterator())

    constructor(vararg nodes:Node):this(nodes.iterator())

    private fun addInternal(node: Node, backWithNext:Boolean = false){
        currentNodeOrNull()?.pause()
        nodeHolders.addLast(NodeHolder(node, backWithNext = backWithNext))
    }

    fun add(node: Node, backWithNext:Boolean = false) {
        addInternal(node, backWithNext)
        if(state==State.SHOWING) node.show()
        notifyOnCurrentNodeChangeListeners()
    }

    private fun replaceCurrentWithInternal(node: Node, backWithNext:Boolean = false){
        if(nodeHolders.isNotEmpty()){
            currentNode.remove()
            nodeHolders.replaceLastWith(NodeHolder(node, backWithNext = backWithNext))
        } else {
            nodeHolders.addLast(NodeHolder(node, backWithNext = backWithNext))
        }
    }

    fun replaceCurrentWith(node: Node, backWithNext:Boolean = false) {
        replaceCurrentWithInternal(node, backWithNext)
        if(state==State.SHOWING) node.show()
        notifyOnCurrentNodeChangeListeners()
    }

    private fun backInternal(ignoreNestedContainers:Boolean):Boolean {
        var hasChanges = false

        while (nodeHolders.isNotEmpty()) {
            val removable = currentNode

            if (!ignoreNestedContainers && removable is ContainerNode && removable.back()) {
                hasChanges = true
                break
            }

            if (nodeHolders.size > 1) {
                removable.remove()
                nodeHolders.removeLast()
                hasChanges = true

                if (!currentNodeHolder.backWithNext) break
            } else {
                break
            }
        }

        return hasChanges
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
        for(nh in nodeHolders){
            nh.node.remove()
        }
    }

    inner class NodeHolder(
        val node:Node,
        val backWithNext:Boolean = false
    ){
        override fun toString():String{
            return "NodeHolder(node=$node, backWithNext=$backWithNext)"
        }
    }

    companion object {
        fun build(init:Transaction.()->Unit):DequeContainerNode = DequeContainerNode().apply {
            transaction(init).commit()
        }
    }

    //транзакции тут нужны чтобы не устанавливать состояние SHOWING для нодов, которые сразу же должны быть скрыты, например при многократном back или при back вместе replaceCurrent
    //тут выполняются операции с учетом того какой нод окажется последним после выполнения всех операций чтобы установить именно ему состояние SHOWING
    private fun transact(commands:MutableList<Command>,
                         doBeforeEveryCommand:(HookContext.()->Unit)?,
                         doAfterEveryCommand:(HookContext.()->Unit)?,
                         doFinally:(HookContext.()->Unit)?) {

        val hookContext by lazy {
            HookContext()
        }

        if (commands.isNotEmpty()) {

            var hasChanges = false

            for (command in commands) {
                doBeforeEveryCommand?.invoke(HookContext())
                when (command) {

                    is AddCommand -> {
                        val oldNode = currentNodeOrNull()
                        command.doBefore?.invoke(hookContext, command.node, oldNode)

                        addInternal(command.node, command.backWithNext)
                        hasChanges = true

                        command.doAfter?.invoke(hookContext, command.node, oldNode)
                    }

                    is ReplaceCurrentCommand -> {
                        val oldNode = currentNodeOrNull()
                        command.doBefore?.invoke(hookContext, command.node, oldNode)

                        replaceCurrentWithInternal(command.node, command.backWithNext)
                        hasChanges = true

                        command.doAfter?.invoke(hookContext, command.node, oldNode)
                    }

                    is BackCommand -> {
                        if (nodeHolders.size > 1) {
                            val oldNode = currentNode
                            command.doBefore?.invoke(hookContext, oldNode)

                            if (backInternal(command.ignoreNestedContainers)){
                                hasChanges = true
                            }

                            command.doAfter?.invoke(hookContext, currentNode, oldNode)
                        }
                    }
                }
                doAfterEveryCommand?.invoke(hookContext)
            }
            if (hasChanges){
                if (state == State.SHOWING) currentNodeOrNull()?.show()
                notifyOnCurrentNodeChangeListeners()
            }
            doFinally?.invoke(hookContext)
        }
    }


    fun transaction(init:Transaction.()->Unit):Transaction = Transaction().apply { init() }

    @DslMarker
    annotation class TransactionDSLMarker

    @TransactionDSLMarker
    inner class Transaction {

        private val commands:MutableList<Command> = mutableListOf()

        private var doBeforeEveryCommand:(HookContext.()->Unit)? = null
        private var doAfterEveryCommand:(HookContext.()->Unit)? = null
        private var doFinally:(HookContext.()->Unit)? = null

        fun commit(){
            transact(commands, doBeforeEveryCommand, doAfterEveryCommand, doFinally)
        }

        fun doBeforeEveryCommand(execute:HookContext.()->Unit) = apply {
            doBeforeEveryCommand = execute
        }

        fun doAfterEveryCommand(execute:HookContext.()->Unit) = apply {
            doAfterEveryCommand = execute
        }

        fun doFinally(execute:HookContext.()->Unit) = apply {
            doFinally = execute
        }

        fun add(node:Node, init:(AddCommand.()->Unit)?=null) = AddCommand(node).also {
            init?.invoke(it)
            commands.add(it)
        }

        fun replaceCurrentWith(node:Node, init:(ReplaceCurrentCommand.()->Unit)?=null) = ReplaceCurrentCommand(node).also {
            init?.invoke(it)
            commands.add(it)
        }

        fun back(init:(BackCommand.()->Unit)?=null) = BackCommand().also {
            init?.invoke(it)
            commands.add(it)
        }

    }

    class AddCommand (val node:Node):Command(){
        var backWithNext:Boolean = false

        var doBefore:(HookContext.(newNode:Node, currentNode:Node?)->Unit)? = null
        var doAfter:(HookContext.(currentNode:Node, previousNode:Node?)->Unit)? = null

        fun doBefore(execute:HookContext.(newNode:Node, previousNode:Node?)->Unit) {
            doBefore = execute
        }

        fun doAfter(execute:HookContext.(newNode:Node, previousNode:Node?)->Unit) {
            doAfter = execute
        }
    }

    class ReplaceCurrentCommand (val node:Node):Command(){
        var backWithNext:Boolean = false

        var doBefore:(HookContext.(newNode:Node, currentNode:Node?)->Unit)? = null
        var doAfter:(HookContext.(currentNode:Node, previousNode:Node?)->Unit)? = null

        fun doBefore(execute:HookContext.(newNode:Node, previousNode:Node?)->Unit) {
            doBefore = execute
        }

        fun doAfter(execute:HookContext.(newNode:Node, previousNode:Node?)->Unit) {
            doAfter = execute
        }
    }

    class BackCommand:Command(){
        var ignoreNestedContainers:Boolean = false

        var doBefore:(HookContext.(currentNode:Node)->Unit)? = null
        var doAfter:(HookContext.(currentNode:Node, previousNode:Node)->Unit)? = null

        fun doBefore(execute:HookContext.(currentNode:Node)->Unit) {
            doBefore = execute
        }

        fun doAfter(execute:HookContext.(newNode:Node, previousNode:Node)->Unit) {
            doAfter = execute
        }
    }

    @TransactionDSLMarker
    open class Command

    @TransactionDSLMarker
    class HookContext

    override fun toJSON():String {
        return "{\"type\": \"DequeContainerNode\", \"id\": \"$id\", \"state\": \"$state\", \"nodes\": ${nodeHolders.map { it.node.toJSON() }}}"
    }

    override fun toString():String {
        //return "LinearContainerNode(id=$id, nodes=$nodeHolders)"
        //return "LinearContainerNode(id=$id, nodes=${nodeHolders.map { it.node }})"
        return toJSON()
    }
}

