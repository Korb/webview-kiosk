package uk.nktnet.webviewkiosk.ui.components.setting.fielditems.webbrowsing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import uk.nktnet.webviewkiosk.config.UserSettings
import uk.nktnet.webviewkiosk.config.UserSettingsKeys
import uk.nktnet.webviewkiosk.ui.components.setting.fields.BooleanSettingFieldItem

@Composable
fun AllowDefaultLongPressSetting() {
    val context = LocalContext.current
    val userSettings = remember { UserSettings(context) }

    BooleanSettingFieldItem(
        label = "Allow Default Long Press",
        infoText = """
            When enabled, long-pressing areas in the WebView will trigger the native
            WebView behaviour, e.g. text selection.

            Specifically for links, even if this is set to false, it can be overridden
            by the "Allow Link Long Press Context Menu" setting.
        """.trimIndent(),
        initialValue = userSettings.allowDefaultLongPress,
        restricted = userSettings.isRestricted(UserSettingsKeys.WebBrowsing.ALLOW_DEFAULT_LONG_PRESS),
        onSave = { userSettings.allowDefaultLongPress = it }
    )
}
