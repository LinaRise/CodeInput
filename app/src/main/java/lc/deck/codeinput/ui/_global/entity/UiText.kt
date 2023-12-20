package lc.deck.codeinput.ui._global.entity

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class UiText: Parcelable {
    @Parcelize
    data class DynamicString(@SerializedName("value") val value: String): UiText(), Parcelable
    @Parcelize
    class StringResource(
        @SerializedName("resId")
        @StringRes val resId: Int,
        vararg val args: @RawValue Any
    ): UiText(), Parcelable

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}