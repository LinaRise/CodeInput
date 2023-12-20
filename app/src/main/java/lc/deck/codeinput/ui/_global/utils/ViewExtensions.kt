package lc.deck.codeinput.ui._global.utils

import android.view.View

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setupClickListener(clickListener: () -> Unit) {
    this.setOnClickListener {
        this.isEnabled = false
        clickListener.invoke()
    }
}
