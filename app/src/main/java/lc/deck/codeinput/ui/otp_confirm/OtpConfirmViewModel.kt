package lc.deck.codeinput.ui.otp_confirm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import lc.deck.codeinput.R
import lc.deck.codeinput.repository.ConfirmRepository
import lc.deck.codeinput.ui._global.base.BaseViewModel
import lc.deck.codeinput.ui._global.entity.UiText
import javax.inject.Inject

@HiltViewModel
class OtpConfirmViewModel @Inject constructor(
    private val repository: ConfirmRepository
) : BaseViewModel() {

    private val _codeRequest = PublishRelay.create<Boolean>()
    val codeRequest: Observable<Boolean> = _codeRequest.hide()

    private val _loading = BehaviorRelay.create<Boolean>()
    val loading: Observable<Boolean> = _loading.hide()

    private val _errorMessage = PublishRelay.create<UiText>()
    val errorMessage: Observable<UiText> = _errorMessage.hide()

    private val _otpFieldsError = PublishRelay.create<Pair<UiText, Boolean>>()
    val otpFieldsError: Observable<Pair<UiText, Boolean>> = _otpFieldsError.hide()

    var isCountDownRunning = false

    init {
        // за отсутствием запроса для первичной отправки кода,
        // воспользуемся запросом для повторной отправкаи
        requestSmsCode()
    }

    /**
     * Запрос на смс код
     */
    private fun requestSmsCode() {
        repository.requestSmsCode()
            .doOnSubscribe { _loading.accept(true) }
            .doFinally { _loading.accept(false) }
            .subscribe(
                {
                    _codeRequest.accept(true)
                },
                { e ->
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
        _otpFieldsError.accept( Pair(
            UiText.DynamicString(""),
            false
        ))
    }
}