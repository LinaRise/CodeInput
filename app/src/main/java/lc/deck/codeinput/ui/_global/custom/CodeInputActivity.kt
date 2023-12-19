package lc.deck.codeinput.ui._global.custom

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import lc.deck.codeinput.broadcast_receiver.SmsCodeReceiver
import lc.deck.codeinput.databinding.ActivityMainBinding

/**
 * Экран ввода кода подтверждения
 */
@AndroidEntryPoint
class CodeInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var smsCodeReceiver: SmsCodeReceiver
    private lateinit var intentFilter: IntentFilter

    val viewModel: CodeInputViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initSmsListener()
        initSmsBroadcastReceiver()
    }

    /**
     * Инициализация BroadcastReceiver для прослушивания входящих смс и получения кода
     */
    private fun initSmsBroadcastReceiver() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsCodeReceiver = SmsCodeReceiver()
        smsCodeReceiver.setCodeListener(object : SmsCodeReceiver.CodeReceivedListener {
            override fun setReceivedCode(code: String) {

            }
        })
    }

    /**
     * Начало прослушивания получения sms
     */
    private fun initSmsListener() {
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }

    override fun onResume() {
        super.onResume()
        registerSmsCodeReceiver()
    }

    /**
     * Регистррация BroadcastReceiver
     */
    private fun registerSmsCodeReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            registerReceiver(smsCodeReceiver, intentFilter, RECEIVER_EXPORTED)
        else registerReceiver(smsCodeReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsCodeReceiver)
    }

}