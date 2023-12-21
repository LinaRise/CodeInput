package lc.deck.codeinput.ui._global.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import lc.deck.codeinput.R
import lc.deck.codeinput.databinding.CustomInputFieldLayoutBinding
import lc.deck.codeinput.ui._global.utils.showKeyboard

/**
 * View ввод otp
 */
class CodeEditText constructor(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_CODE_LENGTH = 4
        private const val DEFAULT_CODE_MASK_CHAR = '•'
        private const val DEFAULT_CODE_PLACEHOLDER = ' '
        private const val DEFAULT_SCROLL_DURATION_IN_MILLIS = 250
    }

    private var binding: CustomInputFieldLayoutBinding =
        CustomInputFieldLayoutBinding.inflate(LayoutInflater.from(context))

    var codeMaskChar: Char = DEFAULT_CODE_MASK_CHAR
        set(value) {
            field = value
            editable = editable
        }

    var codePlaceholder: Char = DEFAULT_CODE_PLACEHOLDER
        set(value) {
            field = value
            editable = editable
        }

    var inputType: Int
        get() = binding.etCode.inputType
        set(value) {
            binding.etCode.inputType = value
        }

    var maskTheCode: Boolean = false
        set(value) {
            field = value
            editable = editable
        }

    var maxLength: Int = DEFAULT_CODE_LENGTH
        set(value) {
            field = value
            if (initEnded) onAttachedToWindow()
        }

    var scrollDurationInMillis: Int = DEFAULT_SCROLL_DURATION_IN_MILLIS

    private var onCodeChangedListener: ((Pair<String, Boolean>) -> Unit)? = null
    private var initEnded =
        false // if true allows the view to be updated after setting an attribute programmatically
    private var rememberToRenderCode = false
    private var xAnimator: ObjectAnimator? = null
    private var yAnimator: ObjectAnimator? = null
    private var editable: Editable = "".toEditable()
        set(value) {
            field = value
            if (initEnded) renderCode()
            else rememberToRenderCode = true
        }

    fun setOnCodeChangedListener(listener: ((Pair<String, Boolean>) -> Unit)?) {
        this.onCodeChangedListener = listener
    }

    var text: CharSequence
        get() = this.editable
        set(value) {
            val cropped = if (value.length > maxLength) value.subSequence(0, maxLength)
            else value
            this.editable = cropped.toEditable()
            binding.etCode.setText(cropped)
        }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        removeAllViews()
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText, 0, 0)
        try {
            // codeMaskChar
            attributes.getString(R.styleable.CodeEditText_et_codeMaskChar)?.also {
                codeMaskChar = it[0]
            }

            // codePlaceholder
            attributes.getString(R.styleable.CodeEditText_et_codePlaceholder)?.also {
                codePlaceholder = it[0]
            }

            // inputType
            if (attributes.hasValue(R.styleable.CodeEditText_android_inputType))
                binding.etCode.inputType =
                    attributes.getInt(R.styleable.CodeEditText_android_inputType, 0)

            // maskTheCode
            if (attributes.hasValue(R.styleable.CodeEditText_et_maskTheCode)) maskTheCode =
                attributes.getBoolean(R.styleable.CodeEditText_et_maskTheCode, false)

            // maxLength
            maxLength = attributes.getInt(R.styleable.CodeEditText_android_maxLength, maxLength)

            // scrollDurationInMillis
            scrollDurationInMillis = attributes.getInt(
                R.styleable.CodeEditText_et_scrollDurationInMillis,
                scrollDurationInMillis
            )

            // text
            attributes.getString(R.styleable.CodeEditText_android_text)?.also { value ->
                val cropped = if (value.length > maxLength) value.subSequence(0, maxLength)
                else value
                this.editable = cropped.toEditable()
            }

        } finally {
            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initEnded = true

        if (!isInEditMode) binding.apply {
            llCode.removeAllViews()
            for (i in 0 until maxLength) {
                View.inflate(
                    context,
                    R.layout.item_input_code,
                    findViewById(R.id.llCode)
                )
            }

            if (editable.isNotEmpty()) etCode.text = editable
            etCode.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            etCode.removeTextChangedListener(textChangedListener)
            etCode.addTextChangedListener(textChangedListener)

            llCode.setOnClickListener {
                etCode.apply {
                    showKeyboard()
                    focusOnLastLetter()
                }
            }
        }

        if (rememberToRenderCode) {
            rememberToRenderCode = false
            post { renderCode() }
        }
    }

    private val textChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            editable = s
        }
    }

    private fun renderCode() {
        binding.apply {
            for (i in 0 until llCode.childCount) {
                val itemContainer = llCode.getChildAt(i)

                itemContainer.findViewById<TextView>(R.id.tvCode).text =
                    if (editable.length > i)
                        (if (maskTheCode) codeMaskChar else editable[i]).toString()
                    else codePlaceholder.toString()

                if (i == editable.length - 1 && !itemContainer.isFullyVisibleInside(
                        hScrollView
                    )
                )
                    hScrollView.focusOnView(itemContainer)
            }
        }
        notifyCodeChanged()
    }

    private fun notifyCodeChanged(): Boolean = (editable.length == maxLength).apply {
        onCodeChangedListener?.invoke(Pair(editable.toString(), this))
    }

    private fun View.isFullyVisibleInside(parentView: View): Boolean {
        val scrollBounds = Rect()
        parentView.getDrawingRect(scrollBounds)
        val left = this.x
        val right = left + this.width
        val top = this.y
        val bottom = top + this.height
        return scrollBounds.left < left &&
                scrollBounds.right > right &&
                scrollBounds.top < top &&
                scrollBounds.bottom > bottom
    }

    private fun View.focusOnView(childView: View) = post {
        var top = childView.top
        var left = childView.left
        var parent = (childView.parent as View)
        while (parent != this) {
            top += parent.top
            left += parent.left
            parent = parent.parent as View
        }

        val scrollX = left - this.width / 2 + childView.width / 2
        xAnimator?.cancel()
        xAnimator = ObjectAnimator.ofInt(this, "scrollX", scrollX).apply {
            interpolator = DecelerateInterpolator()
            duration = scrollDurationInMillis.toLong()
            start()
        }

        val scrollY = top - this.height / 2 + childView.height / 2
        yAnimator?.cancel()
        yAnimator = ObjectAnimator.ofInt(this, "scrollY", scrollY).apply {
            interpolator = DecelerateInterpolator()
            duration = scrollDurationInMillis.toLong()
            start()
        }
    }

    private fun EditText.focusOnLastLetter() = setSelection(text.length)

    private fun CharSequence.toEditable(): Editable =
        Editable.Factory.getInstance().newEditable(this)

    fun setErrorMode(isErrorMode: Boolean = false) {
        binding.apply {
            for (i in 0 until llCode.childCount) {
                val itemContainer = llCode.getChildAt(i)

                if (isErrorMode) {
                    itemContainer.findViewById<TextView>(R.id.tvCode).setTextColor(
                        ContextCompat.getColor(this@CodeEditText.context, R.color.red)
                    )

                    itemContainer.findViewById<View>(R.id.underline).setBackgroundColor(
                        ContextCompat.getColor(this@CodeEditText.context, R.color.red)
                    )
                } else {
                    itemContainer.findViewById<TextView>(R.id.tvCode).setTextColor(
                        ContextCompat.getColor(this@CodeEditText.context, R.color.black)
                    )

                    itemContainer.findViewById<View>(R.id.underline).setBackgroundColor(
                        ContextCompat.getColor(this@CodeEditText.context, R.color.black)
                    )
                }
            }
        }
    }

}