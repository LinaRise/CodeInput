package lc.deck.codeinput.data.server

import io.reactivex.Completable
import lc.deck.codeinput.data.entity.confirmation.Otp
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {

    companion object {
        const val API_PATH = "v1"
    }

    @POST("$API_PATH/reg/otp")
    fun sendOtpCode(@Body otp: Otp): Completable

    @GET("$API_PATH/reg/otpresend")
    fun requestSmsCode(): Completable
}