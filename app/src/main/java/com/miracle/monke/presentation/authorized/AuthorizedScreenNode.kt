package com.miracle.monke.presentation.authorized

import com.miracle.monke.monke.MapContainerNode
import com.miracle.monke.monke.Node
import com.miracle.monke.presentation.home.HomeScreenNode
import com.miracle.monke.presentation.settings.SettingsScreenNode

class AuthorizedScreenNode: MapContainerNode<AuthorizedScreenNode.Screens>(
    Screens.HOME,
    Screens.HOME to NodeFabric { HomeScreenNode() },
    Screens.SETTINGS to NodeFabric { SettingsScreenNode() }
){
    enum class Screens{
        HOME,
        SETTINGS
    }
}