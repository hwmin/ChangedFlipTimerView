package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_countdown_clock_digit.view.*
import kotlinx.android.synthetic.main.view_simple_clock.view.*
import java.util.concurrent.TimeUnit

/**
 * Author : hwm
 * Date : 2023/3/22 14:14
 * Description : 改成了只有分钟和秒
 */
class CountDownClock : LinearLayout {
    private var countDownTimer: CountDownTimer? = null
    private var countdownListener: CountdownCallBack? = null
    private var countdownTickInterval = 1000

    private var resetSymbol: String = "8"

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.view_simple_clock, this)

        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.CountDownClock, defStyleAttr, 0)
            val resetSymbol = typedArray?.getString(R.styleable.CountDownClock_resetSymbol)
            if (resetSymbol != null) {
                setResetSymbol(resetSymbol)
            }

            val digitTopDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitTopDrawable)
            setDigitTopDrawable(digitTopDrawable)
            val digitBottomDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitBottomDrawable)
            setDigitBottomDrawable(digitBottomDrawable)
            val digitHorizontalDividerDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitHorizontalDividerDrawable)
            setHorizontalDividerDrawable(digitHorizontalDividerDrawable)
            val digitDividerColor = typedArray?.getColor(R.styleable.CountDownClock_digitDividerColor, 0)
            setDigitDividerColor(digitDividerColor ?: 0)
            val digitSplitterColor = typedArray?.getColor(R.styleable.CountDownClock_digitSplitterColor, 0)
            setDigitSplitterColor(digitSplitterColor ?: 0)

            val digitTextColor = typedArray?.getColor(R.styleable.CountDownClock_digitTextColor, 0)
            setDigitTextColor(digitTextColor ?: 0)

            val digitTextSize = typedArray?.getDimension(R.styleable.CountDownClock_digitTextSize, 0f)
            setDigitTextSize(digitTextSize ?: 0f)
            setSplitterDigitTextSize(digitTextSize ?: 0f)

            val digitPadding = typedArray?.getDimension(R.styleable.CountDownClock_digitPadding, 0f)
            setDigitPadding(digitPadding?.toInt() ?: 0)

            val splitterPadding = typedArray?.getDimension(R.styleable.CountDownClock_splitterPadding, 0f)
            setSplitterPadding(splitterPadding?.toInt() ?: 0)

            val halfDigitHeight = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_halfDigitHeight, 0)
            val digitWidth = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_digitWidth, 0)
            setHalfDigitHeightAndDigitWidth(halfDigitHeight ?: 0, digitWidth ?: 0)

            val animationDuration = typedArray?.getInt(R.styleable.CountDownClock_animationDuration, 0)
            setAnimationDuration(animationDuration ?: 600)

            val countdownTickInterval = typedArray?.getInt(R.styleable.CountDownClock_countdownTickInterval, 1000)
            this.countdownTickInterval = countdownTickInterval ?: 1000

            invalidate()
            typedArray?.recycle()
        }
    }

    ////////////////
    // Public methods
    ////////////////

    private var milliLeft: Long = 0

    fun startCountDown(timeToNextEvent: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeToNextEvent, countdownTickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                milliLeft = millisUntilFinished
                countdownListener?.countdownTick(millisUntilFinished)
                setCountDownTime(millisUntilFinished)
            }

            override fun onFinish() {
                countdownListener?.countdownFinished()
            }
        }
        countDownTimer?.start()
    }

    fun resetCountdownTimer() {
        countDownTimer?.cancel()
        firstDigitSecond.setNewText(resetSymbol)
        secondDigitSecond.setNewText(resetSymbol)
    }

    ////////////////
    // Private methods
    ////////////////

    private fun setCountDownTime(timeToStartMillis: Long) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToStartMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToStartMillis) % 60

        var minutesString = minutes.toString()
        var secondsString = seconds.toString()

        if(secondsString.length == 1){
            secondsString = "0$secondsString"
        }
        secondDigitSecond.animateTextChange(secondsString)

        if(minutesString.length == 1){
            minutesString = "0$minutesString"
        }
        firstDigitSecond.animateTextChange(minutesString)
    }

    private fun setResetSymbol(resetSymbol: String?) {
        resetSymbol?.let {
            if (it.isNotEmpty()) {
                this.resetSymbol = resetSymbol
            } else {
                this.resetSymbol = ""
            }
        } ?: kotlin.run {
            this.resetSymbol = ""
        }
    }

    private fun setDigitTopDrawable(digitTopDrawable: Drawable?) {
        if (digitTopDrawable != null) {
            firstDigitSecond.frontUpper.background = digitTopDrawable
            firstDigitSecond.backUpper.background = digitTopDrawable
            secondDigitSecond.frontUpper.background = digitTopDrawable
            secondDigitSecond.backUpper.background = digitTopDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setHorizontalDividerDrawable(drawable : Drawable?){
        ivDigitHorizontalDivider.setImageDrawable(drawable)
    }

    private fun setDigitBottomDrawable(digitBottomDrawable: Drawable?) {
        if (digitBottomDrawable != null) {
            firstDigitSecond.frontLower.background = digitBottomDrawable
            firstDigitSecond.backLower.background = digitBottomDrawable
            secondDigitSecond.frontLower.background = digitBottomDrawable
            secondDigitSecond.backLower.background = digitBottomDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitDividerColor(digitDividerColor: Int) {
        var dividerColor = digitDividerColor
        if (dividerColor == 0) {
            dividerColor = ContextCompat.getColor(context, R.color.transparent)
        }

        firstDigitSecond.digitDivider.setBackgroundColor(dividerColor)
        secondDigitSecond.digitDivider.setBackgroundColor(dividerColor)
    }

    private fun setDigitSplitterColor(digitsSplitterColor: Int) {
        if (digitsSplitterColor != 0) {
            //  digitsSplitter.setTextColor(digitsSplitterColor)
        } else {
            // digitsSplitter.setTextColor(ContextCompat.getColor(context, R.color.transparent))
        }
    }

    private fun setSplitterDigitTextSize(digitsTextSize: Float) {
        //digitsSplitter.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setDigitPadding(digitPadding: Int) {
        firstDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        secondDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
    }

    private fun setSplitterPadding(splitterPadding: Int) {
        //digitsSplitter.setPadding(splitterPadding, 0, splitterPadding, 0)
    }

    private fun setDigitTextColor(digitsTextColor: Int) {
        var textColor = digitsTextColor
        if (textColor == 0) {
            textColor = ContextCompat.getColor(context, R.color.transparent)
        }
        firstDigitSecond.frontUpperText.setTextColor(textColor)
        firstDigitSecond.backUpperText.setTextColor(textColor)
        secondDigitSecond.frontUpperText.setTextColor(textColor)
        secondDigitSecond.backUpperText.setTextColor(textColor)

        firstDigitSecond.frontLowerText.setTextColor(textColor)
        firstDigitSecond.backLowerText.setTextColor(textColor)
        secondDigitSecond.frontLowerText.setTextColor(textColor)
        secondDigitSecond.backLowerText.setTextColor(textColor)
    }

    private fun setDigitTextSize(digitsTextSize: Float) {
        firstDigitSecond.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)

        firstDigitSecond.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setHalfDigitHeightAndDigitWidth(halfDigitHeight: Int, digitWidth: Int) {
        setHeightAndWidthToView(firstDigitSecond.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.backUpper, halfDigitHeight, digitWidth)

        // Lower
        setHeightAndWidthToView(firstDigitSecond.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.backLower, halfDigitHeight, digitWidth)

        // Dividers
        firstDigitSecond.digitDivider.layoutParams.width = digitWidth
        secondDigitSecond.digitDivider.layoutParams.width = digitWidth
    }

    private fun setHeightAndWidthToView(view: View, halfDigitHeight: Int, digitWidth: Int) {
        val firstDigitMinuteFrontUpperLayoutParams = view.layoutParams
        firstDigitMinuteFrontUpperLayoutParams.height = halfDigitHeight
        firstDigitMinuteFrontUpperLayoutParams.width = digitWidth
        view.layoutParams = firstDigitMinuteFrontUpperLayoutParams
    }

    private fun setAnimationDuration(animationDuration: Int) {
        firstDigitSecond.setAnimationDuration(animationDuration.toLong())
        secondDigitSecond.setAnimationDuration(animationDuration.toLong())
    }

    private fun setTransparentBackgroundColor() {
        val transparent = ContextCompat.getColor(context, R.color.transparent)
        firstDigitSecond.frontLower.setBackgroundColor(transparent)
        firstDigitSecond.backLower.setBackgroundColor(transparent)
        secondDigitSecond.frontLower.setBackgroundColor(transparent)
        secondDigitSecond.backLower.setBackgroundColor(transparent)
    }

    ////////////////
    // Listeners
    ////////////////

     fun setCountdownListener(countdownListener: CountdownCallBack) {
        this.countdownListener = countdownListener
    }

    interface CountdownCallBack {
        fun countdownTick(millisUntilFinished: Long)
        fun countdownFinished()
    }

    fun pauseCountDownTimer() {
        countDownTimer?.cancel()
    }

     fun resumeCountDownTimer() {
        startCountDown(milliLeft)
    }

    fun setCustomTypeface(typeface : Typeface){
        firstDigitSecond.setTypeFace(typeface)
        firstDigitSecond.setTypeFace(typeface)
        secondDigitSecond.setTypeFace(typeface)
        secondDigitSecond.setTypeFace(typeface)
    }
}
