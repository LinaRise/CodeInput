package lc.deck.codeinput.data.server

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET

interface Api {

    companion object {
        const val API_PATH = "v1"
    }

    @GET("$API_PATH/reg/otp")
    fun getSmsCode(): Completable
}