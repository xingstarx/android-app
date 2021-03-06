package one.mixin.android.ui.url

import android.content.UriMatcher
import android.net.Uri
import android.os.Bundle
import one.mixin.android.R
import one.mixin.android.extension.isUUID
import one.mixin.android.ui.common.BaseActivity
import one.mixin.android.ui.conversation.link.LinkBottomSheetDialogFragment
import one.mixin.android.util.Session
import org.jetbrains.anko.toast

class UrlInterpreterActivity : BaseActivity() {
    companion object {
        private val CODE = 100
        private val PAY = 101
        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI("codes", "*", CODE)
            addURI("pay", null, PAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.data
        if (data == null) {
            finish()
            return
        }
        if (Session.getAccount() == null) {
            toast(R.string.not_logged_in)
            finish()
            return
        }
        interpretIntent(data)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun interpretIntent(uri: Uri) {
        when (sURIMatcher.match(uri)) {
            CODE -> {
                val pathSegments = uri.pathSegments
                if (pathSegments.size > 0) {
                    val bottomSheet = LinkBottomSheetDialogFragment.newInstance(uri.toString())
                    bottomSheet.show(supportFragmentManager, LinkBottomSheetDialogFragment.TAG)
                }
            }
            PAY -> {
                val bottomSheet = LinkBottomSheetDialogFragment.newInstance(uri.toString())
                bottomSheet.show(supportFragmentManager, LinkBottomSheetDialogFragment.TAG)
            }
            else -> {
            }
        }
    }
}

fun isMixinUrl(url: String): Boolean {
    if (url.startsWith("https://mixin.one/pay", true) ||
        url.startsWith("mixin://pay", true)) {
        return true
    } else if (url.startsWith("https://mixin.one/codes/", true)) {
        val segments = Uri.parse(url).pathSegments
        if (segments.size >= 2) {
            val data = segments[1]
            if (data.isUUID()) {
                return true
            }
        }
        return false
    } else if (url.startsWith("mixin://codes/", true)) {
        val segments = Uri.parse(url).pathSegments
        if (segments.size >= 1) {
            val data = segments[0]
            if (data.isUUID()) {
                return true
            }
        }
        return false
    } else {
        return false
    }
}