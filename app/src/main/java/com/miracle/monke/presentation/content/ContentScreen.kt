package com.miracle.monke.presentation.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miracle.monke.presentation.common.ScreenRoot

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    viewModel: ContentScreenViewModel,
    onOpenDetails:(id:Int)->Unit = {},
    onLogOut:()->Unit = {},
){
    ScreenRoot(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Content"
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onLogOut
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = "exit")
                }
            }
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { onOpenDetails(1) }) {
                    Text(text = "Details 1")
                }
                Button(onClick = { onOpenDetails(2) }) {
                    Text(text = "Details 2")
                }
                Button(onClick = { onOpenDetails(3) }) {
                    Text(text = "Details 3")
                }
            }
        }
    )
}