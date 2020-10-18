package com.jetpack.base.mvvm.ui.widget

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

/**
 * 可以自由移动缩放的图片控件
 * Created by capton on 2017/4/18.
 */


class ScaleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle), ViewTreeObserver.OnGlobalLayoutListener,
    ScaleGestureDetector.OnScaleGestureListener,
    View.OnTouchListener {

    /** 表示是否只有一次加载  */
    private var isOnce = false

    /** 初始时的缩放值  */
    private var mInitScale: Float = 0.toFloat()

    /** 双击时 的缩放值  */
    private var mClickScale: Float = 0.toFloat()

    /** 最大的缩放值  */
    private var mMaxScale: Float = 0.toFloat()

    /** 图片缩放矩阵  */
    private val mMatrix: Matrix

    /** 图片缩放手势  */
    private val mScaleGesture: ScaleGestureDetector

    // ----------------------------自由移动--------------------------------
    /** 可移动最短距离限制，大于这个值时就可移动  */
    private val mTouchSlop: Int

    /** 是否可以拖动  */
    private var isCanDrag: Boolean = false

    // ----------------------------双击放大--------------------------------
    private val mGesture: GestureDetector

    // 是否自动缩放
    private var isAutoScale: Boolean = false

    /**
     * 获得缩放值
     *
     * @return
     */
    /**
     * xscale xskew xtrans yskew yscale ytrans 0 0 0
     */
    val scale: Float
        get() {
            val values = FloatArray(9)
            mMatrix.getValues(values)
            return values[Matrix.MSCALE_X]
        }

    /**
     * 获得图片缩放后的矩阵
     *
     * @return
     */
    // 初始化矩阵
    // 移动s
    val matrixRectF: RectF
        get() {
            val matrix = mMatrix
            val rectF = RectF()
            val drawable = getDrawable()
            if (drawable != null) {
                rectF.set(
                    0f,
                    0f,
                    drawable!!.getIntrinsicWidth().toFloat(),
                    drawable!!.getIntrinsicHeight().toFloat()
                )
                matrix.mapRect(rectF)
            }
            return rectF
        }

    private var mLastX: Float = 0.toFloat()
    private var mLastY: Float = 0.toFloat()

    /** 上次手指的数量  */
    private var mLastPointerCount: Int = 0

    /** 判断是否检测了x,y轴  */
    private var isCheckX: Boolean = false
    private var isCheckY: Boolean = false

    init {
        // 必须设置才能触发
        this.setOnTouchListener(this)

        mMatrix = Matrix()
        // 设置缩放模式
        super.setScaleType(ImageView.ScaleType.MATRIX)

        mScaleGesture = ScaleGestureDetector(context, this)
        mGesture = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {

                // 如果正在缩放时，不能放大
                if (isAutoScale) {
                    return true
                }

                val px = e.getX()
                val py = e.getY()
                // 只有小于最大缩放比例才能放大
                val scale = scale
                if (scale < mClickScale) {
                    // mMatrix.postScale(mClickScale/scale, mClickScale/scale,
                    // px, py);
                    postDelayed(ScaleRunnale(px, py, mClickScale), 16)
                    isAutoScale = true
                } else {
                    // mMatrix.postScale(mInitScale/scale, mInitScale/scale, px,
                    // py);
                    postDelayed(ScaleRunnale(px, py, mInitScale), 16)
                    isAutoScale = true
                }
                // setImageMatrix(mMatrix);
                return true
            }
        })

        /**
         * 是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件，如viewpager
         * 就是用这个距离来判断用户是否翻页。
         */
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()
    }

    private inner class ScaleRunnale(
        private val x: Float,
        private val y: Float,
        private val mTargetScale: Float
    ) : Runnable {
        private var mTempScale: Float = 0.toFloat()

        // 放大值
        private val BIGGER = 1.08f

        // 缩小值
        private val SMALLER = 0.96f

        init {

            if (scale < mTargetScale) {
                mTempScale = BIGGER
            } else if (scale > mTargetScale) {
                mTempScale = SMALLER
            }
        }

        override fun run() {
            // 先进行缩放
            mMatrix.postScale(mTempScale, mTempScale, x, y)
            checkSideAndCenterWhenScale()
            setImageMatrix(mMatrix)

            val currentScale = scale

            // 如果想放大，并且当前的缩放值小于目标值
            if (mTempScale > 1.0f && currentScale < mTargetScale || mTempScale < 1.0f && currentScale > mTargetScale) {
                // 递归执行run方法
                postDelayed(this, 16)
            } else {
                val scale = mTargetScale / currentScale
                mMatrix.postScale(scale, scale, x, y)
                checkSideAndCenterWhenScale()
                setImageMatrix(mMatrix)

                isAutoScale = false
            }
        }


    }

    override fun onGlobalLayout() {
        // 如果还没有加载图片
        if (!isOnce) {

            // 获得控件的宽高
            val width = getWidth()
            val height = getHeight()

            val drawable = getDrawable() ?: return
// 获得图片的宽高
            val bitmapWidth = drawable!!.getIntrinsicWidth()
            val bitmapHeight = drawable!!.getIntrinsicHeight()

            // 设定比例值
            var scale = 0.0f

            // 如果图片的宽度>控件的宽度，缩小
            if (bitmapWidth > width && bitmapHeight < height) {
                scale = width * 1.0f / bitmapWidth
            }
            // 如果图片的高度>控件的高度，缩小
            if (bitmapHeight > height && bitmapWidth < width) {
                scale = height * 1.0f / bitmapHeight
            }
            // 如果图片的宽高度>控件的宽高度，缩小 或者 如果图片的宽高度<控件的宽高度，放大
            if (bitmapWidth > width && bitmapHeight > height || bitmapWidth < width && bitmapHeight < height) {
                val f1 = width * 1.0f / bitmapWidth
                val f2 = height * 1.0f / bitmapHeight
                scale = Math.min(f1, f2)
            }

            // 初始化缩放值
            mInitScale = scale
            mClickScale = mInitScale * 2
            mMaxScale = mInitScale * 8

            // 得到移动的距离
            val dx = width / 2 - bitmapWidth / 2
            val dy = height / 2 - bitmapHeight / 2

            // 平移
            mMatrix.postTranslate(dx.toFloat(), dy.toFloat())

            // 在控件的中心缩放
            mMatrix.postScale(scale, scale, (width / 2).toFloat(), (height / 2).toFloat())

            // 设置矩阵
            setImageMatrix(mMatrix)

            // 关于matrix，就是个3*3的矩阵
            /**
             * xscale xskew xtrans yskew yscale ytrans 0 0 0
             */

            isOnce = true
        }
    }

    /**
     * 注册全局事件
     */
    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getViewTreeObserver().addOnGlobalLayoutListener(this)
    }

    /**
     * 移除全局事件
     */
    protected override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        getViewTreeObserver().removeGlobalOnLayoutListener(this)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // 如果没有图片，返回
        if (getDrawable() == null) {
            return true
        }
        // 缩放因子，>0表示正在放大，<0表示正在缩小
        var intentScale = detector.getScaleFactor()
        val scale = scale

        // 进行缩放范围的控制
        // 判断，如果<最大缩放值，表示可以放大，如果》最小缩放，说明可以缩小
        if (scale < mMaxScale && intentScale > 1.0f || scale > mInitScale && intentScale < 1.0f) {

            // scale 变小时， intentScale变小
            if (scale * intentScale < mInitScale) {
                // intentScale * scale = mInitScale ;
                intentScale = mInitScale / scale
            }

            // scale 变大时， intentScale变大
            if (scale * intentScale > mMaxScale) {
                // intentScale * scale = mMaxScale ;
                intentScale = mMaxScale / scale
            }

            // 以控件为中心缩放
            // mMatrix.postScale(intentScale, intentScale, getWidth()/2,
            // getHeight()/2);
            // 以手势为中心缩放
            mMatrix.postScale(intentScale, intentScale, detector.getFocusX(), detector.getFocusY())

            // 检测边界与中心点
            checkSideAndCenterWhenScale()

            setImageMatrix(mMatrix)
        }

        return true
    }

    private fun checkSideAndCenterWhenScale() {
        val rectF = matrixRectF
        var deltaX = 0f
        var deltaY = 0f
        val width = getWidth()
        val height = getHeight()

        // 情况1， 如果图片的宽度大于控件的宽度
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left// 如果图片没有左边对齐，就往左边移动
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right// 如果图片没有右边对齐，就往右边移动
            }
        }
        // 情况2， 如果图片的宽度大于控件的宽度
        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top//
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom// 往底部移动
            }
        }

        // 情况3,如图图片在控件内，则让其居中
        if (rectF.width() < width) {
            // deltaX = width/2-rectF.left - rectF.width()/2;
            // 或
            deltaX = width / 2f - rectF.right + rectF.width() / 2f
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2f
        }

        mMatrix.postTranslate(deltaX, deltaY)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        // TODO Auto-generated method stub
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        // TODO Auto-generated method stub

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        // 把事件传递给双击手势
        if (mGesture.onTouchEvent(event)) {
            return true
        }
        // 把事件传递给缩放手势
        mScaleGesture.onTouchEvent(event)

        var x = event.getX()
        var y = event.getY()

        val pointerCount = event.getPointerCount()
        for (i in 0 until pointerCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        x /= pointerCount.toFloat()
        y /= pointerCount.toFloat()

        // 说明手指改变
        if (mLastPointerCount != pointerCount) {
            isCanDrag = false
            mLastX = x
            mLastY = y
        }
        mLastPointerCount = pointerCount

        val rectF = matrixRectF

        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> if (rectF.width() > getWidth()) {
                getParent().requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                if (rectF.width() > getWidth()) {
                    getParent().requestDisallowInterceptTouchEvent(true)
                }

                var dx = x - mLastX
                var dy = y - mLastY

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy)
                }
                /**
                 * 如果能移动
                 */
                if (isCanDrag) {
                    //RectF rectF = getMatrixRectF();
                    if (getDrawable() == null) {
                        return true
                    }

                    isCheckY = true
                    isCheckX = isCheckY

                    // 如果图片在控件内，不允许移动
                    if (rectF.width() < getWidth()) {
                        isCheckX = false
                        dx = 0f
                    }
                    if (rectF.height() < getHeight()) {
                        isCheckY = false
                        dy = 0f
                    }

                    mMatrix.postTranslate(dx, dy)

                    // 移动事检测边界
                    checkSideAndCenterWhenTransate()

                    setImageMatrix(mMatrix)
                }

                mLastX = x
                mLastY = y
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // 清楚手指
                mLastPointerCount = 0
        }

        return true
    }

    private fun checkSideAndCenterWhenTransate() {
        val rectF = matrixRectF
        var deltaX = 0f
        var deltaY = 0f
        val width = getWidth()
        val height = getHeight()

        if (rectF.top > 0 && isCheckY) {
            deltaY = -rectF.top// 往上边移动
        }
        if (rectF.bottom < height && isCheckY) {
            deltaY = height - rectF.bottom// 往底部移动
        }

        if (rectF.left > 0 && isCheckX) {
            deltaX = -rectF.left// 往左边移动
        }
        if (rectF.right < width && isCheckX) {
            deltaX = width - rectF.right// 往右边移动
        }
        // 移动
        mMatrix.postTranslate(deltaX, deltaY)
    }

    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        // 求得两点的距离
        return Math.sqrt((dx * dx + dy * dy).toDouble()) > mTouchSlop
    }
}