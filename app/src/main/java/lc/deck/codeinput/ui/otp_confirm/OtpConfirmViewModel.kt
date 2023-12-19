package lc.deck.codeinput.ui.otp_confirm

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import lc.deck.codeinput.repository.ConfirmRepository
import lc.deck.codeinput.ui._global.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class OtpConfirmViewModel @Inject constructor(
    private val repository: ConfirmRepository
) : BaseViewModel() {

    private val _codeRequest = PublishRelay.create<Boolean>()
    val codeRequest: Observable<Boolean> = _codeRequest.hide()

    private val _loading = BehaviorRelay.create<Boolean>()
    val loading: Observable<Boolean> = _loading.hide()

    private val _errorMessage = PublishRelay.create<String>()
    val errorMessage: Observable<String> = _errorMessage.hide()

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
                { e -> _errorMessage.accept(e.message ?: "") }
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
                { e -> _errorMessage.accept(e.message ?: "") }
            ).connect()
    }
}