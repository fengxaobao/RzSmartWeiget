package com.rz.command.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.rz.command.R
import java.util.*

/**
 * Created by zhou on 2020/5/31 23:56.
 */
class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 波浪圆圈颜色
     */
    private var mColor = resources.getColor(android.R.color.holo_blue_bright)

    /**
     * 第一个圆圈的半径(也就是圆形图片的半径)
     */
    private var mImageRadius = 50

    /**
     * 波浪圆之间间距
     */
    private var mWidth = 3

    /**
     * 最大宽度
     */
    private var mMaxRadius = 300

    /**
     * 是否扩散中
     */
    /**
     * 是否正在扩散中
     */
    var isWave = false
        private set

    // 透明度集合
    private val mAlphas: MutableList<Int> = ArrayList()

    // 扩散圆半径集合
    private val mRadius: MutableList<Int> = ArrayList()
    private var mPaint: Paint? = null

    //扩散的圆形是否是实心圆
    var isFill = true
    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mAlphas.add(255)
        mRadius.add(0)
    }

    /**
     * 获取View的宽高在构造方法中拿不到的，getWidth()，getHeight()都会为零
     *
     * @param hasWindowFocus
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        mMaxRadius = if (width > height) height / 2 else width / 2
        invalidate()
    }

    /**
     * 防止window是去焦点时，也就是应用在后台时，停止View的绘制
     */
    override fun invalidate() {
        if (hasWindowFocus()) {
            super.invalidate()
        }
    }

    public override fun onDraw(canvas: Canvas) {
        // 绘制扩散圆
        mPaint!!.color = mColor
        for (i in mAlphas.indices) {
            // 设置透明度
            var alpha = mAlphas[i]
            mPaint!!.alpha = alpha
            // 绘制波浪圆
            val radius = mRadius[i]
            if (isFill) {
                mPaint!!.style = Paint.Style.FILL
                canvas.drawCircle(
                    width / 2.toFloat(),
                    height / 2.toFloat(),
                    mImageRadius + radius.toFloat(),
                    mPaint!!
                )
                mPaint!!.style = Paint.Style.STROKE
                mPaint!!.strokeWidth = 5f
                canvas.drawCircle(
                    width / 2.toFloat(),
                    height / 2.toFloat(),
                    mImageRadius + radius.toFloat(),
                    mPaint!!
                )
            } else {
                mPaint!!.style = Paint.Style.STROKE
                mPaint!!.strokeWidth = 5f
                canvas.drawCircle(
                    width / 2.toFloat(),
                    height / 2.toFloat(),
                    mImageRadius + radius.toFloat(),
                    mPaint!!
                )
            }
            if (alpha > 0 && mImageRadius + radius < mMaxRadius) {
                alpha = (255.0f * (1.0f - (mImageRadius + radius) * 1.0f / mMaxRadius)).toInt()
                mAlphas[i] = alpha
                mRadius[i] = radius + 1
            } else if (alpha < 0 && mImageRadius + radius > mMaxRadius) {
                // 当最外面那个圆达到了View的宽度时，移除，保证内存的回收
                mRadius.removeAt(i)
                mAlphas.removeAt(i)
            }
        }
        // 判断当波浪圆扩散到指定宽度时添加新扩散圆
        if (mRadius[mRadius.size - 1] == mWidth) {
            addWave()
        }
//        if (isWave) {
        invalidate()
//        }
    }

    /**
     * 开始扩散
     */
    fun start() {
        isWave = true
        invalidate()
    }

    /**
     * 停止扩散
     */
    fun stop() {
        isWave = false
    }

    /**
     * 设置波浪圆颜色
     */
    fun setColor(colorId: Int) {
        mColor = colorId
    }

    /**
     * 设置波浪圆之间间距
     */
    fun setWidth(width: Int) {
        mWidth = width
    }

    /**
     * 设置中心圆半径
     */
    fun setMaxRadius(maxRadius: Int) {
        mMaxRadius = maxRadius
    }

    fun setImageRadius(imageRadius: Int) {
        mImageRadius = imageRadius
    }

    fun addWave() {
        mAlphas.add(255)
        mRadius.add(0)
    }

    init {
        init()
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, 0)
        mColor = a.getColor(R.styleable.WaveView_wave_color, mColor)
        mWidth = a.getInt(R.styleable.WaveView_wave_width, mWidth)
        mImageRadius = a.getInt(R.styleable.WaveView_wave_coreImageRadius, mImageRadius)
        a.recycle()
    }
}