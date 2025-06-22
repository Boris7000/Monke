package com.miracle.monke.monke

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner

/**
 * A Composable that allows you to handle the back press event and return a boolean
 * indicating whether to consume the back press event or not.
 *
 * @param onBackPressed A lambda that returns a boolean indicating whether to consume the back press event.
 */
@Composable
fun BackPressedHandler(onBackPressed: () -> Boolean) {
    // Get the back dispatcher and lifecycle owner
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher

    // Remember the current onBackPressed state
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)

    // Create a back callback
    val backCallback = rememberUpdatedState(
        object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                if (!currentOnBackPressed()) {
                    isEnabled = false
                    backDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    // Set up the DisposableEffect for adding/removing the callback
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback.value)

        // Clean up when leaving the composition
        onDispose {
            backCallback.value.remove()
        }
    }
}