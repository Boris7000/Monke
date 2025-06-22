package com.miracle.monke.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miracle.monke.presentation.content.ContentScreen
import com.miracle.monke.presentation.content.ContentScreenViewModel
import com.miracle.monke.presentation.details.DetailsScreen
import com.miracle.monke.presentation.details.DetailsScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    onLogOut:()->Unit = {},
){
    val sreenState = viewModel.sreenState
    when(sreenState){
        HomeScreenViewModel.ScreenState.None -> Unit
        is HomeScreenViewModel.ScreenState.Content -> {
            val contentScreenViewModel =
                viewModel<ContentScreenViewModel>(
                    viewModelStoreOwner = sreenState.node
                ) {
                    ContentScreenViewModel()
                }

            ContentScreen(
                modifier = modifier,
                viewModel = contentScreenViewModel,
                onOpenDetails = { id->
                    viewModel.onEvent(HomeScreenViewModel.UIEvent.OpenDetails(id))
                },
                onLogOut = onLogOut
            )
        }
        is HomeScreenViewModel.ScreenState.Details -> {
            val detailsScreenViewModel =
                viewModel<DetailsScreenViewModel>(
                    viewModelStoreOwner = sreenState.node
                ) {
                    DetailsScreenViewModel(sreenState.node.detailsId)
                }

            DetailsScreen(
                modifier = modifier,
                viewModel = detailsScreenViewModel,
                onBackClicked = {
                    viewModel.onEvent(HomeScreenViewModel.UIEvent.Back)
                }
            )
        }
    }
}