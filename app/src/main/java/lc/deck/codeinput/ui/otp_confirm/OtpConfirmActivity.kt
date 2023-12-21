package lc.deck.codeinput.ui.otp_confirm

import android.animation.ValueAnimator
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import lc.deck.codeinput.R
import lc.deck.codeinput.broadcast_receiver.SmsCodeReceiver
import lc.deck.codeinput.databinding.ActivityMainBinding
import lc.deck.codeinput.ui._global.base.BaseActivity
import lc.deck.codeinput.ui._global.utils.hideKeyboard
import lc.deck.codeinput.ui._global.utils.setupClickListener
import lc.deck.codeinput.ui._global.utils.visible


/**
 * Экран ввода кода подтверждения
 */
@AndroidEntryPoint
class OtpConfirmActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var smsCodeReceiver: SmsCodeReceiver
    private lateinit var intentFilter: IntentFilter

    private val viewModel: OtpConfirmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.apply {
            otpArea.text = viewModel.getTypedOtp()
            otpArea.setOnCodeChangedListener {
                viewModel.setTypedOtp(otpArea.text)
                if (otpArea.text.first.length == otpArea.maxLength) {
                    hideKeyboard()
                    viewModel.sendOtpCode(otpArea.text.first.toString())
                } /*else if (otpArea.text.first.length == otpArea.maxLength - 1) {
                    viewModel.setSmsFieldsViewNormal()
                }*/
            }
            tvResend.setupClickListener {
                viewModel.requestSmsCode()
            }
        }
        initSmsListener()
        initSmsBroadcastReceiver()
    }

    override fun onStart() {
        super.onStart()
        viewModel.apply {
            loading.subscribe {
                renderLoading(it)
            }.disposeOnStop()

            successVerification.subscribe {
                viewModel.cancelCountDown()
                Toast.makeText(
                    this@OtpConfirmActivity,
                    "Переход на другой экран",
                    Toast.LENGTH_LONG
                ).show()
            }.disposeOnStop()

            codeRequest.subscribe {
                viewModel.startTimer()
            }.disposeOnStop()

            timer.subscribe { (isCountDownRunning, timeValue) ->
                if (isCountDownRunning) {
                    binding.apply {
                        binding.linearTimer.visible(true)
                        binding.tvResend.visible(false)
                        binding.tvTimer.text = getString(R.string.send_code_again_in)
                        binding.tvRemainingTime.text = timeValue
                    }
                } else {
                    binding.linearTimer.visible(false)
                    binding.tvResend.visible(true)
                }
            }.disposeOnStop()

            errorMessage.subscribe { errorText ->
                Toast.makeText(
                    this@OtpConfirmActivity,
                    errorText.asString(this@OtpConfirmActivity),
                    Toast.LENGTH_SHORT
                ).show()
            }.disposeOnStop()

            otpFieldsError.subscribe { (message, isVisible) ->
                binding.tvErrorMessage.visible(isVisible)
                binding.tvErrorMessage.text = message.asString(this@OtpConfirmActivity)
                binding.otpArea.text = Pair(viewModel.getTypedOtp().first, isVisible)
            }.disposeOnStop()

        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsCodeReceiver)
    }

    /**
     * Инициализация BroadcastReceiver для прослушивания входящих смс и получения кода
     */
    private fun initSmsBroadcastReceiver() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsCodeReceiver = SmsCodeReceiver()
        smsCodeReceiver.setCodeListener(object : SmsCodeReceiver.CodeReceivedListener {
            override fun setReceivedCode(code: String) {
                binding.otpArea.text = Pair(code, false)
            }
        })
    }

    /**
     * Начало прослушивания получения sms
     * Прослушивает sms содержащее уникальную строку,
     * идентифицирующую ваше приложение в течение 5 минут
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

    /**
     * Показ лоадера
     */
    private fun renderLoading(show: Boolean?) {
        binding.apply {
            if (show == true) {
                loaderContainer.visible(true)
                progressBar.repeatCount = ValueAnimator.INFINITE
                progressBar.playAnimation()
            } else {
                loaderContainer.visible(false)
                progressBar.pauseAnimation()
            }
        }
    }

}