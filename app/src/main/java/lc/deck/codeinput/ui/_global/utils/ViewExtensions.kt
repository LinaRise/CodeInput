package lc.deck.codeinput.ui._global.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setupClickListener(clickListener: () -> Unit) {
    this.setOnClickListener {
        this.isEnabled = false
        clickListener.invoke()
    }
}

fun View.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
        it.showSoftInput(this, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}
