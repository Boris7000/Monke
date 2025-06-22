package com.miracle.monke.presentation.root

import com.miracle.monke.monke.MapContainerNode
import com.miracle.monke.presentation.authorized.AuthorizedScreenNode
import com.miracle.monke.presentation.login.LoginScreenNode


class RootScreenNode: MapContainerNode<RootScreenNode.Screens>(
    RootScreenNode.Screens.LOG_IN,
    RootScreenNode.Screens.LOG_IN to NodeFabric { LoginScreenNode() },
    RootScreenNode.Screens.AUTHORIZED to NodeFabric { AuthorizedScreenNode()},
) {
    enum class Screens{
        LOG_IN,
        AUTHORIZED
    }
}