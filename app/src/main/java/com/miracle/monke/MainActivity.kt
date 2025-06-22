package com.miracle.monke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.miracle.monke.presentation.root.RootScreen
import com.miracle.monke.presentation.root.RootScreenViewModel
import com.miracle.monke.ui.theme.MonkeDemoTheme


class MainActivity : ComponentActivity() {

    val appComponent = App.provideAppComponent()

    private val rootScreenViewModel: RootScreenViewModel by viewModels {
        appComponent.provideMainActivityViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        //enableEdgeToEdge()
        setContent {
            MonkeDemoTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    RootScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        viewModel = rootScreenViewModel
                    )
                }
            }
        }
    }
}