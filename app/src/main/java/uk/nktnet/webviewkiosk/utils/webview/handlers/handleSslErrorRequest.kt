package uk.nktnet.webviewkiosk.utils.webview.handlers

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.SslError
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.webkit.SslErrorHandler
import androidx.appcompat.app.AlertDialog
import android.view.ViewGroup.LayoutParams
import android.webkit.WebView

@SuppressLint("SetTextI18n")
fun handleSslErrorRequest(
    context: Context,
    webView: WebView?,
    handler: SslErrorHandler?,
    error: SslError?,
) {
    val layout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(60, 60, 60, 0)
    }

    val titleView = TextView(context).apply {
        text = "SSL Certificate Error"
        textSize = 22f
        setPadding(0, 0, 0, 30)
    }
    layout.addView(titleView)

    val errorDescription = when (error?.primaryError) {
        SslError.SSL_EXPIRED -> "The certificate has expired."
        SslError.SSL_IDMISMATCH -> "The certificate Hostname mismatch."
        SslError.SSL_UNTRUSTED -> "The certificate authority is not trusted."
        SslError.SSL_NOTYETVALID -> "The certificate is not yet valid."
        else -> "Unknown SSL error."
    }

    val messageView = TextView(context).apply {
        text = """
            $errorDescription
            
            URL:
                ${error?.url}
        """.trimIndent()
        setPadding(0, 0, 0, 30)
    }
    layout.addView(messageView)

    val buttonsLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.END
        layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    val advancedButton = Button(context).apply { text = "Advanced" }
    buttonsLayout.addView(advancedButton)
    layout.addView(buttonsLayout)

    val dialog = AlertDialog.Builder(context)
        .setView(layout)
        .setNegativeButton("Cancel") { _, _ ->
            handler?.cancel()
            webView?.reload()
        }
        .setOnCancelListener {
            handler?.cancel()
            webView?.reload()
        }
        .show()

    var advancedVisible = false
    val confirmTextView = TextView(context).apply {
        text = "WARNING: Proceeding may compromise your security!"
        setPadding(0, 20, 0, 20)
    }
    val proceedButton = Button(context).apply { text = "Proceed" }
    proceedButton.setOnClickListener {
        handler?.proceed()
        dialog.dismiss()
    }

    advancedButton.setOnClickListener {
        advancedVisible = !advancedVisible
        if (advancedVisible) {
            layout.addView(confirmTextView)
            layout.addView(proceedButton)
        } else {
            layout.removeView(confirmTextView)
            layout.removeView(proceedButton)
        }
    }
}
