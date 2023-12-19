package lc.deck.codeinput.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import lc.deck.codeinput.ui._global.utils.parcelable
import lc.deck.codeinput.ui._global.utils.serializable
import java.util.regex.Pattern


class SmsCodeReceiver : BroadcastReceiver() {
    interface CodeReceivedListener {
        fun setReceivedCode(code: String)
    }

    private var codeReceivedListener: CodeReceivedListener? = null

    fun setCodeListener(codeReceivedListener: CodeReceivedListener?) {
        this.codeReceivedListener = codeReceivedListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status: Status? = extras?.parcelable(SmsRetriever.EXTRA_STATUS)

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms: String? = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    sms?.let {
                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            codeReceivedListener?.setReceivedCode(otp)
                        }
                    }
                }

            }
        }
    }
}