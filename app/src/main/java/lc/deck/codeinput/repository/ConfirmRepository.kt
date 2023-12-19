package lc.deck.codeinput.repository

import io.reactivex.Completable
import lc.deck.codeinput.data.entity.confirmation.Otp
import lc.deck.codeinput.data.server.Api
import lc.deck.codeinput.system.schedulers.SchedulersProvider
import javax.inject.Inject

class ConfirmRepository @Inject constructor(
    private val api: Api,
    private val schedulers: SchedulersProvider
) {

    /**
     * Запрос повторный для получения смс
     */
    fun requestSmsCode(): Completable = api
        .requestSmsCode()
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())

    /**
     * Отправка введеного кода
     */
    fun sendOtpCode(code: String): Completable = api
        .sendOtpCode(otp = Otp(code))
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
}
