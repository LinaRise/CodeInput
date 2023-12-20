package lc.deck.codeinput.ui.otp_confirm

import android.animation.ValueAnimator
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import lc.deck.codeinput.R
import lc.deck.codeinput.broadcast_receiver.SmsCodeReceiver
import lc.deck.codeinput.databinding.ActivityMainBinding
import lc.deck.codeinput.ui._global.base.BaseActivity
import lc.deck.codeinput.ui._global.utils.visible
import java.util.concurrent.TimeUnit

/**
 * Экран ввода кода подтверждения
 */
@AndroidEntryPoint
class OtpConfirmActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var smsCodeReceiver: SmsCodeReceiver
    private lateinit var intentFilter: IntentFilter
    private var countDownTimer: CountDownTimer? = null

    private val viewModel: OtpConfirmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.apply {
            otpArea.setOnCodeChangedListener {
                if (otpArea.text.length == otpArea.maxLength)
                    viewModel.sendOtpCode(otpArea.text.toString())
                else if (otpArea.text.length == otpArea.maxLength - 1)
                    viewModel.setSmsFieldsViewNormal()

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

            codeRequest.subscribe {
                binding.apply {
                    binding.tvRemainingTime.visible(true)
                    tvResend.text = getString(R.string.send_code_again_in)
                }
                setCountDownTimer(RESEND_CODE_DELAY_IN_MILLIS)
                countDownTimer?.start()
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
                binding.otpArea.setErrorMode(isVisible)
            }.disposeOnStop()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsCodeReceiver)
    }

    private fun setCountDownTimer(timeInSeconds: Int) {
        countDownTimer =
            object : CountDownTimer(
                timeInSeconds.times(ONE_SECOND_IN_MILLISECONDS),
                ONE_SECOND_IN_MILLISECONDS
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.apply {
                        tvRemainingTime.text = String.format(
                            "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                            millisUntilFinished
                                        )
                                    )
                        )
                    }
                }

                override fun onFinish() {
                    timerStop()
                }

                private fun timerStop() {
                    binding.tvRemainingTime.visible(false)
                    viewModel.isCountDownRunning = false
                }
            }
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

    companion object {
        private const val RESEND_CODE_DELAY_IN_MILLIS = 60_000
        private const val ONE_SECOND_IN_MILLISECONDS = 1_000L
    }

}