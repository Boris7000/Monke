package com.miracle.monke.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenRoot(
    modifier: Modifier = Modifier,
    topBar: (@Composable BoxScope.() -> Unit)?=null,
    content: @Composable BoxScope.() -> Unit,
    bottomBar: (@Composable BoxScope.() -> Unit)?=null
) {

    Column(
        modifier = modifier
            .safeDrawingPadding()
    ) {
        if (topBar!=null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    //.background(Color.Red)
                    .statusBarsPadding()
            ) {
                topBar()
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            //.background(Color.Green)
        ){
            content()
        }

        if (bottomBar!=null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    //.background(Color.Cyan)
                    .navigationBarsPadding()
            ) {
                bottomBar()
            }
        }
    }
}