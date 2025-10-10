package uk.nktnet.webviewkiosk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class PermissionState(
    val granted: Boolean,
    val shouldShowRationale: Boolean
)

@Composable
fun rememberPermissionState(permission: String): Pair<PermissionState, () -> Unit> {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var granted by remember { mutableStateOf(false) }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
                shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                } ?: false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val requestPermission: () -> Unit = {
        if (!granted && !shouldShowRationale) {
            launcher.launch(permission)
        } else {
            openAppSettings(context)
        }
    }

    return PermissionState(granted, shouldShowRationale) to requestPermission
}
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
