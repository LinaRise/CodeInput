package lc.deck.codeinput.ui._global.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.dispose()
    }

    protected fun Disposable.connect() {
        compositeDisposable.add(this)
    }
}