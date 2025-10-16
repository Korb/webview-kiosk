package uk.nktnet.webviewkiosk.config
sealed class Screen(val route: String) {
    object AdminRestrictionsChanged : Screen("admin/restrictions_changed")
    object Loading : Screen("loading")

    object WebView : Screen("webview")
    object Settings : Screen("settings")

    object SettingsWebContent : Screen("settings/web-content")
    object SettingsWebContentFiles : Screen("settings/web-content/files")
    object SettingsWebBrowsing : Screen("settings/web-browsing")
    object SettingsWebEngine : Screen("settings/web-engine")
    object SettingsWebLifecycle : Screen("settings/web-lifecycle")

    object SettingsAppearance : Screen("settings/appearance")
    object SettingsDevice : Screen("settings/device")
    object SettingsJsScript : Screen("settings/js-script")

    object SettingsAbout : Screen("settings/about")
}
