package com.miracle.monke.presentation.details

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.miracle.monke.presentation.authorized.AuthorizedScreenViewModel
import com.miracle.monke.presentation.common.AlertDialog
import com.miracle.monke.presentation.common.ScreenRoot
import com.miracle.monke.presentation.root.RootScreenViewModel

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailsScreenViewModel,
    onBackClicked: ()->Unit = {}
){

    val exitState = viewModel.exitState

    LaunchedEffect(exitState) {
        if (exitState == DetailsScreenViewModel.ExitState.Confirmed){
            onBackClicked()
        }
    }

    val onBack:()->Unit = {
        viewModel.onEvent(DetailsScreenViewModel.UIEvent.Exit)
    }

    BackHandler(enabled = true, onBack = onBack)

    Box(modifier = modifier) {

        if (exitState == DetailsScreenViewModel.ExitState.WaitingForConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(DetailsScreenViewModel.UIEvent.DismissExit) },
                onConfirmation = { viewModel.onEvent(DetailsScreenViewModel.UIEvent.ConfirmExit) },
                dialogTitle = "Are you sure you want to sign out?",
                dialogText = "When you exit, the information you entered will not be saved",
                confirmButtomText = "Exit",
                dismissButtomText = "Cancel"
            )
        }

        ScreenRoot(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Details"
                    )
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = onBack
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "exit")
                    }
                }
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Details ${viewModel.id}")
                }
            }
        )
    }
}

