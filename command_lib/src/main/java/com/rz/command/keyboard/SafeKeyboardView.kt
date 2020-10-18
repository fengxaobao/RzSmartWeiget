package com.rz.command.keyboard

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import com.rz.command.R
import java.lang.reflect.Field

/**
 * Created by Administrator on 2018/3/7 0007.
 */
class SafeKeyboardView : KeyboardView {
    private var mContext: Context
    private var isCap = false
    private var isCapLock = false
    var isVibrateEnable = true
        private set
    private var delDrawable: Drawable? = null
    private var lowDrawable: Drawable? = null
    private var upDrawable: Drawable? = null
    private var upDrawableLock: Drawable? = null
    var lastKeyboard: Keyboard? = null
        private set

    // 键盘的一些自定义属性
    var isRandomDigit = false // 数字随机 = false

    //    public boolean isOnlyIdCard() {
    //        return onlyIdCard;
    //    }
    //    private boolean onlyIdCard;     // 仅显示 身份证 键盘
    //    private final static boolean ONLY_ID_CARD = false;
    var isRememberLastType = false // 仅显示 身份证 键盘 = false

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
        mContext = context
        initAttrs(context, attrs, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
        mContext = context
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        if (attrs != null) {
            val array =
                context.obtainStyledAttributes(attrs, R.styleable.SafeKeyboardView, defStyleAttr, 0)
            isRandomDigit = array.getBoolean(
                R.styleable.SafeKeyboardView_random_digit,
                DIGIT_RANDOM
            )
            // onlyIdCard = array.getBoolean(R.styleable.SafeKeyboardView_only_id_card, ONLY_ID_CARD);
            isRememberLastType = array.getBoolean(
                R.styleable.SafeKeyboardView_remember_last_type,
                REM_LAST_TYPE
            )
            isVibrateEnable = array.getBoolean(
                R.styleable.SafeKeyboardView_enable_vibrate,
                DEFAULT_ENABLE_VIBRATE
            )
            array.recycle()
        }
    }

    private fun init(context: Context) {
        isCap = false
        isCapLock = false
        // 默认三种图标
        delDrawable = context.resources.getDrawable(R.drawable.icon_del)
        lowDrawable = context.resources.getDrawable(R.drawable.icon_capital_default)
        upDrawable = context.resources.getDrawable(R.drawable.icon_capital_selected)
        upDrawableLock =
            context.resources.getDrawable(R.drawable.icon_capital_selected_lock)
        lastKeyboard = null
    }

    fun enableVibrate(enableVibrate: Boolean) {
        isVibrateEnable = enableVibrate
    }

    override fun setKeyboard(keyboard: Keyboard) {
        super.setKeyboard(keyboard)
        lastKeyboard = keyboard
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            val keys = keyboard.keys
            for (key in keys) {
                if (key.codes[0] == -5 || key.codes[0] == -2 || key.codes[0] == 100860 || key.codes[0] == -1
                    || key.codes[0] == 43 || key.codes[0] == 46 || key.codes[0] == 100863
                ) {
                    drawSpecialKey(canvas, key)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawSpecialKey(canvas: Canvas, key: Keyboard.Key) {
        val color = Color.WHITE
        if (key.codes[0] == -5) {
            drawKeyBackground(R.drawable.keyboard_change, canvas, key)
            drawTextAndIcon(canvas, key, delDrawable, color)
        } else if (key.codes[0] == -2 || key.codes[0] == 100860 || key.codes[0] == 100861 || key.codes[0] == 100863) {
            drawKeyBackground(R.drawable.keyboard_change, canvas, key)
            drawTextAndIcon(canvas, key, null, color)
        } else if (key.codes[0] == -1) {
            if (isCapLock) {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key)
                drawTextAndIcon(canvas, key, upDrawableLock, color)
            } else if (isCap) {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key)
                drawTextAndIcon(canvas, key, upDrawable, color)
            } else {
                drawKeyBackground(R.drawable.keyboard_change, canvas, key)
                drawTextAndIcon(canvas, key, lowDrawable, color)
            }
        }
    }

    private fun drawKeyBackground(
        id: Int,
        canvas: Canvas,
        key: Keyboard.Key
    ) {
        val drawable = mContext.resources.getDrawable(id)
        val state = key.currentDrawableState
        if (key.codes[0] != 0) {
            drawable.state = state
        }
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height)
        drawable.draw(canvas)
    }

    private fun drawTextAndIcon(
        canvas: Canvas,
        key: Keyboard.Key,
        drawable: Drawable?,
        color: Int
    ) {
        try {
            val bounds = Rect()
            val paint = Paint()
            paint.textAlign = Paint.Align.CENTER
            paint.isAntiAlias = true
            paint.color = color
            if (key.label != null) {
                val label = key.label.toString()
                val field: Field
                if (label.length > 1 && key.codes.size < 2) {
                    var labelTextSize = 0
                    try {
                        field =
                            KeyboardView::class.java.getDeclaredField(context.getString(R.string.mLabelTextSize))
                        field.isAccessible = true
                        labelTextSize = field[this] as Int
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                    paint.textSize = labelTextSize.toFloat()
                    paint.typeface = Typeface.DEFAULT_BOLD
                } else {
                    var keyTextSize = 0
                    try {
                        field =
                            KeyboardView::class.java.getDeclaredField(context.getString(R.string.mLabelTextSize))
                        field.isAccessible = true
                        keyTextSize = field[this] as Int
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                    paint.textSize = keyTextSize + 10.toFloat()
                    paint.typeface = Typeface.DEFAULT
                }
                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length, bounds)
                canvas.drawText(
                    key.label.toString(), key.x + 1.0f * key.width / 2,
                    key.y + 1.0f * key.height / 2 + 1.0f * bounds.height() / 2, paint
                )
            }
            if (drawable == null) return
            // 约定: 最终图标的宽度和高度都需要在按键的宽度和高度的二分之一以内
            // 如果: 图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 那就不需要变换, 否则就需要等比例缩小
            val iconSizeWidth: Int
            val iconSizeHeight: Int
            key.icon = drawable
            val iconH =
                px2dip(mContext, key.icon.intrinsicHeight.toFloat())
            val iconW =
                px2dip(mContext, key.icon.intrinsicWidth.toFloat())
            if (key.width >= ICON2KEY * iconW && key.height >= ICON2KEY * iconH) {
                //图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 不需要缩放, 因为图片已经够小或者按键够大
                setIconSize(canvas, key, iconW, iconH)
            } else {
                //图标的实际宽度和高度至少有一个不在按键的宽度或高度的二分之一以内, 需要等比例缩放, 因为此时图标的宽或者高已经超过按键的二分之一
                //需要把超过的那个值设置为按键的二分之一, 另一个等比例缩放
                //不管图标大小是多少, 都以宽度width为标准, 把图标的宽度缩放到和按键一样大, 并同比例缩放高度
                val multi = 1.0 * iconW / key.width
                val tempIconH = (iconH / multi).toInt()
                if (tempIconH <= key.height) {
                    //宽度相等时, 图标的高度小于等于按键的高度, 按照现在的宽度和高度设置图标的最终宽度和高度
                    iconSizeHeight = tempIconH / ICON2KEY
                    iconSizeWidth = key.width / ICON2KEY
                } else {
                    //宽度相等时, 图标的高度大于按键的高度, 这时按键放不下图标, 需要重新按照高度缩放
                    val mul = 1.0 * iconH / key.height
                    val tempIconW = (iconW / mul).toInt()
                    iconSizeHeight = key.height / ICON2KEY
                    iconSizeWidth = tempIconW / ICON2KEY
                }
                setIconSize(canvas, key, iconSizeWidth, iconSizeHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIconSize(
        canvas: Canvas,
        key: Keyboard.Key,
        iconSizeWidth: Int,
        iconSizeHeight: Int
    ) {
        val left = key.x + (key.width - iconSizeWidth) / 2
        val top = key.y + (key.height - iconSizeHeight) / 2
        val right = key.x + (key.width + iconSizeWidth) / 2
        val bottom = key.y + (key.height + iconSizeHeight) / 2
        key.icon.setBounds(left, top, right, bottom)
        key.icon.draw(canvas)
        key.icon = null
    }

    fun setCap(cap: Boolean) {
        isCap = cap
    }

    fun setCapLock(isCapLock: Boolean) {
        this.isCapLock = isCapLock
    }

    fun setDelDrawable(delDrawable: Drawable?) {
        this.delDrawable = delDrawable
    }

    fun setLowDrawable(lowDrawable: Drawable?) {
        this.lowDrawable = lowDrawable
    }

    fun setUpDrawable(upDrawable: Drawable?) {
        this.upDrawable = upDrawable
    }

    fun setUpDrawableLock(upDrawableLock: Drawable?) {
        this.upDrawableLock = upDrawableLock
    }

    companion object {
        private const val TAG = "SafeKeyboardView"

        /**
         * 按键的宽高至少是图标宽高的倍数
         */
        private const val ICON2KEY = 2
        private const val DIGIT_RANDOM = false
        private const val REM_LAST_TYPE = true
        private const val DEFAULT_ENABLE_VIBRATE = false
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }
    }
}