package uk.nktnet.webviewkiosk.ui.components.setting.fielditems.webbrowsing

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import uk.nktnet.webviewkiosk.config.Constants
import uk.nktnet.webviewkiosk.config.UserSettings
import uk.nktnet.webviewkiosk.config.UserSettingsKeys
import uk.nktnet.webviewkiosk.ui.components.setting.fields.TextSettingFieldItem
import uk.nktnet.webviewkiosk.utils.validateUrl

@Composable
fun SearchProviderUrlSetting() {
    val context = LocalContext.current
    val userSettings = remember { UserSettings(context) }
    val restricted = userSettings.isRestricted(UserSettingsKeys.WebBrowsing.SEARCH_PROVIDER_URL)

    val searchProviders = listOf(
        "Google" to "https://google.com/search?q=",
        "DuckDuckGo" to "https://duckduckgo.com/?q=",
        "Bing" to "https://bing.com/search?q=",
        "Yahoo" to "https://search.yahoo.com/search?p=",
        "Start page" to "https://startpage.com/do/search?q=",
        "Ecosia" to "https://ecosia.org/search?q=",
        "Unduck" to "https://unduck.link?q=",
        "Unduckified" to "https://s.dunkirk.sh?q=",
    )

    TextSettingFieldItem(
        label = "Search Provider URL",
        infoText = """
            The URL used for search queries in the address bar.

            This URL must include a query parameter, e.g.
              ${Constants.DEFAULT_SEARCH_PROVIDER_URL}
        """.trimIndent(),
        placeholder = Constants.DEFAULT_SEARCH_PROVIDER_URL,
        initialValue = userSettings.searchProviderUrl,
        restricted = restricted,
        isMultiline = false,
        validator = { validateUrl(it) },
        validationMessage = "Invalid search provider URL.",
        onSave = { userSettings.searchProviderUrl = it },
        extraContent = { setValue: (String) -> Unit ->
            if (restricted) {
                return@TextSettingFieldItem
            }

            var lastHighlight by remember {
                mutableStateOf(userSettings.searchProviderUrl)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp)
            ) {
                searchProviders.chunked(2).forEach { rowProviders ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        rowProviders.forEach { (name, url) ->
                            val isSelected = lastHighlight == url
                            Button(
                                onClick = {
                                    lastHighlight = url
                                    setValue(url)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        1.dp,
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant
                                        },
                                        RoundedCornerShape(4.dp)
                                    ),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        if (rowProviders.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    )
}
