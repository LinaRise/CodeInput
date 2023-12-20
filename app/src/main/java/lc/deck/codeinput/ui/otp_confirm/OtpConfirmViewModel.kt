package lc.deck.codeinput.ui.otp_confirm

import android.os.CountDownTimer
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import lc.deck.codeinput.R
import lc.deck.codeinput.repository.ConfirmRepository
import lc.deck.codeinput.ui._global.base.BaseViewModel
import lc.deck.codeinput.ui._global.entity.UiText
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OtpConfirmViewModel @Inject constructor(
    private val repository: ConfirmRepository
) : BaseViewModel() {

    private val _codeRequest = PublishRelay.create<Boolean>()
    val codeRequest: Observable<Boolean> = _codeRequest.hide()

    private val _timer = PublishRelay.create<Pair<Boolean, String>>()
    val timer: Observable<Pair<Boolean, String>> = _timer.hide()

    private val _loading = BehaviorRelay.create<Boolean>()
    val loading: Observable<Boolean> = _loading.hide()

    private val _errorMessage = PublishRelay.create<UiText>()
    val errorMessage: Observable<UiText> = _errorMessage.hide()

    private val _otpFieldsError = BehaviorRelay.create<Pair<UiText, Boolean>>()
    val otpFieldsError: Observable<Pair<UiText, Boolean>> = _otpFieldsError.hide()

    var isCountDownRunning = false

    private var countDownTimer: CountDownTimer? = null

    private var otpValue = ""

    init {
        // за отсутствием запроса для первичной отправки кода,
        // воспользуемся запросом для повторной отправкаи
        requestSmsCode()
    }

    /**
     * Запрос на смс код
     */
    fun requestSmsCode() {
        repository.requestSmsCode()
            .doOnSubscribe { _loading.accept(true) }
            .doFinally { _loading.accept(false) }
            .subscribe(
                {
                    _codeRequest.accept(true)
                },
                { e ->
                    _codeRequest.accept(true)
                    _errorMessage.accept(UiText.DynamicString(e.message ?: ""))
                }
            ).connect()
    }

    /**
     * Отправка введенного кода
     */
    fun sendOtpCode(otp: String) {
        repository.sendOtpCode(otp)
            .doOnSubscribe { _loading.accept(true) }
            .doFinally { _loading.accept(false) }
            .subscribe(
                {
                    _codeRequest.accept(true)
                },
                { e ->
                    //преполагается, что ошибка неверного кода
                    // будет показываться на опредленный код ошибки,
                    // а на остальные показывать стандартным обработчиком _errorMessage
                    // - например показ toast  _errorMessage.accept(UiText.DynamicString(e.message ?: ""))
                    // тут мы этим пренебрегаем так как методы абстркатные и
                    // будем на все ошибки реагировать как на неверный код
                    _otpFieldsError.accept(
                        Pair(
                            UiText.StringResource(R.string.wrong_code_check_input_data),
                            true
                        )
                    )
                }
            ).connect()
    }

    /**
     * Скрытие информации об ошибке
     */
    fun setSmsFieldsViewNormal() {
        _otpFieldsError.accept(
            Pair(
                UiText.DynamicString(""),
                false
            )
        )
    }

    private fun setCountDownTimer() {
        countDownTimer =
            object : CountDownTimer(
                RESEND_CODE_DELAY_IN_MILLIS,
                ONE_SECOND_IN_MILLISECONDS
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    _timer.accept(
                        Pair(
                            true, String.format(
                                "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(
                                            TimeUnit.MILLISECONDS.toMinutes(
                                                millisUntilFinished
                                            )
                                        )
                            )
                        )
                    )
                }

                override fun onFinish() {
                    timerStop()
                }

                private fun timerStop() {
                    _timer.accept(
                        Pair(
                            false, ""
                        )
                    )
                    isCountDownRunning = false
                }
            }
    }

    fun startTimer() {
        if (countDownTimer == null) {
            setCountDownTimer()
            countDownTimer?.start()
        } else countDownTimer?.start()
        isCountDownRunning = true
    }

    fun setTypedOtp(otp: String) {
        otpValue = otp
    }

    fun getTypedOtp() = otpValue

    companion object {
        private const val RESEND_CODE_DELAY_IN_MILLIS = 60_000L
        private const val ONE_SECOND_IN_MILLISECONDS = 1_000L
    }

}