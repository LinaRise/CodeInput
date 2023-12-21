package lc.deck.codeinput.ui._global.entity

import android.content.Context
import androidx.annotation.StringRes
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class UiText {
    @Serializable
    data class DynamicString(@SerialName("value") val value: String) : UiText()

    @Serializable
    class StringResource(
        @SerialName("resId")
        @StringRes val resId: Int,
        vararg val args: @Contextual Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}