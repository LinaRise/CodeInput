package lc.deck.codeinput.data.entity.confirmation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Otp(
    @SerializedName("code")
    val code: String
) : Parcelable