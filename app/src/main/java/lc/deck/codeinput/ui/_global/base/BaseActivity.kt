package lc.deck.codeinput.ui._global.base

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseActivity : AppCompatActivity() {
    private val disposeOnStopDisposables = CompositeDisposable()

    protected fun Disposable.disposeOnStop() {
        disposeOnStopDisposables.add(this)
    }

    override fun onStop() {
        super.onStop()
        disposeOnStopDisposables.clear()
    }
}