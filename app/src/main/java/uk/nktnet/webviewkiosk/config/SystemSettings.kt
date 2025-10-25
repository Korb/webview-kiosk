package uk.nktnet.webviewkiosk.config

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*
import uk.nktnet.webviewkiosk.utils.booleanPref
import uk.nktnet.webviewkiosk.utils.floatPref
import uk.nktnet.webviewkiosk.utils.intPref
import uk.nktnet.webviewkiosk.utils.stringPrefOptional

@Serializable
data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

class SystemSettings(val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    var menuOffsetX by floatPref(prefs = prefs, key = MENU_OFFSET_X, default = -1f)
    var menuOffsetY by floatPref(prefs = prefs, key = MENU_OFFSET_Y, default = -1f)

    var historyStack: List<HistoryEntry>
        get() {
            val raw = prefs.getString(HISTORY_STACK, null) ?: return emptyList()
            return try {
                json.decodeFromString(raw)
            } catch (_: Exception) {
                emptyList()
            }
        }
        set(value) {
            val serialized = json.encodeToString(value)
            prefs.edit { putString(HISTORY_STACK, serialized) }
        }

    var historyIndex by intPref(prefs = prefs, key = HISTORY_INDEX, default = -1)

    val currentUrl
        get() = historyStack.getOrNull(historyIndex)?.url ?: ""

    var isKioskControlPanelSticky by booleanPref(prefs = prefs, key = IS_KIOSK_CONTROL_PANEL_STICKY, default = false)

    val appInstanceId: String
        get() {
            var id = prefs.getString(APP_INSTANCE_ID, null)
            if (id.isNullOrEmpty()) {
                id = UUID.randomUUID().toString()
                prefs.edit { putString(APP_INSTANCE_ID, id) }
            }
            return id
        }

    var sitePermissionsMap: Map<String, Set<String>>
        get() {
            val raw = prefs.getString(SITE_PERMISSIONS, null) ?: return emptyMap()
            return try {
                json.decodeFromString(raw)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyMap()
            }
        }
        set(value) {
            val serialized = json.encodeToString(value)
            prefs.edit { putString(SITE_PERMISSIONS, serialized) }
        }

    var intentUrl by stringPrefOptional(prefs = prefs, key = INTENT_URL)
    var isFreshLaunch by booleanPref(prefs = prefs, key = IS_FRESH_LAUNCH, default = true)
    var urlBeforeNavigation by stringPrefOptional(prefs = prefs, key = URL_BEFORE_NAVIGATION)
    var urlPendingNavigation by stringPrefOptional(prefs = prefs, key = URL_PENDING_NAVIGATION)

    companion object {
        private const val PREFS_NAME = "system_settings"
        private const val MENU_OFFSET_X = "menu_offset_x"
        private const val MENU_OFFSET_Y = "menu_offset_y"
        private const val HISTORY_STACK = "history_stack"
        private const val HISTORY_INDEX = "history_index"
        private const val IS_KIOSK_CONTROL_PANEL_STICKY = "is_kiosk_control_panel_sticky"
        private const val APP_INSTANCE_ID = "app_instance_id"
        private const val SITE_PERMISSIONS = "site_permissions"
        private const val IS_FRESH_LAUNCH = "is_fresh_launch"
        private const val INTENT_URL = "intent_url"
        private const val URL_BEFORE_NAVIGATION = "url_before_navigation"
        private const val URL_PENDING_NAVIGATION = "url_pending_navigation"
    }

    fun clearHistory() {
        historyStack = emptyList()
        historyIndex = -1
    }

    fun getSitePermissions(origin: String): Set<String> {
        return sitePermissionsMap[origin] ?: emptySet()
    }

    fun setSitePermissions(origin: String, resources: Set<String>) {
        val current = sitePermissionsMap.toMutableMap()
        if (resources.isEmpty()) {
            current.remove(origin)
        } else {
            current[origin] = resources
        }
        sitePermissionsMap = current
    }

    fun saveSitePermissions(origin: String, resource: String) {
        val current = sitePermissionsMap.toMutableMap()
        val set = current[origin]?.toMutableSet() ?: mutableSetOf()
        set.add(resource)
        current[origin] = set
        sitePermissionsMap = current
    }
}
