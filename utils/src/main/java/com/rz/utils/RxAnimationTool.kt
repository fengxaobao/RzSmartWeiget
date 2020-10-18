package com.rz.utils

import android.animation.*
import android.view.View
import android.view.animation.*
import com.rz.utils.interfaces.OnDoIntListener


/**
 * @author Vondear
 * @date 2017/3/15
 */

object RxAnimationTool {

    fun start(animator: Animator?) {
        if (animator != null && !animator.isStarted) {
            animator.start()
        }
    }

    fun stop(animator: Animator?) {
        if (animator != null && !animator.isRunning) {
            animator.end()
        }
    }

    fun isRunning(animator: ValueAnimator?): Boolean {
        return animator != null && animator.isRunning
    }

    fun isStarted(animator: ValueAnimator?): Boolean {
        return animator != null && animator.isStarted
    }

    /**
     * 颜色渐变动画
     *
     * @param beforeColor 变化之前的颜色
     * @param afterColor  变化之后的颜色
     * @param listener    变化事件
     */
    fun animationColorGradient(beforeColor: Int, afterColor: Int, listener: OnDoIntListener) {
        val valueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), beforeColor, afterColor).setDuration(3000)
        valueAnimator.addUpdateListener { animation ->
            //                textView.setTextColor((Integer) animation.getAnimatedValue());
            listener.doSomething(animation.animatedValue as Int)
        }
        valueAnimator.start()
    }

    /**
     * 卡片翻转动画
     *
     * @param beforeView
     * @param afterView
     */
    fun cardFilpAnimation(beforeView: View, afterView: View) {
        val accelerator = AccelerateInterpolator()
        val decelerator = DecelerateInterpolator()
        var invisToVis: ObjectAnimator? = null
        var visToInvis: ObjectAnimator? = null
        if (beforeView.visibility == View.GONE) {
            // 局部layout可达到字体翻转 背景不翻转
            invisToVis = ObjectAnimator.ofFloat(
                beforeView,
                "rotationY", -90f, 0f
            )
            visToInvis = ObjectAnimator.ofFloat(
                afterView,
                "rotationY", 0f, 90f
            )
        } else if (afterView.visibility == View.GONE) {
            invisToVis = ObjectAnimator.ofFloat(
                afterView,
                "rotationY", -90f, 0f
            )
            visToInvis = ObjectAnimator.ofFloat(
                beforeView,
                "rotationY", 0f, 90f
            )
        }

        visToInvis!!.duration = 250// 翻转速度
        visToInvis.interpolator = accelerator// 在动画开始的地方速率改变比较慢，然后开始加速
        invisToVis!!.duration = 250
        invisToVis.interpolator = decelerator
        val finalInvisToVis = invisToVis
        val finalVisToInvis = visToInvis
        visToInvis.addListener(object : Animator.AnimatorListener {

            override fun onAnimationEnd(arg0: Animator) {
                if (beforeView.visibility == View.GONE) {
                    afterView.visibility = View.GONE
                    finalInvisToVis.start()
                    beforeView.visibility = View.VISIBLE
                } else {
                    afterView.visibility = View.GONE
                    finalVisToInvis.start()
                    beforeView.visibility = View.VISIBLE
                }
            }

            override fun onAnimationCancel(arg0: Animator) {

            }

            override fun onAnimationRepeat(arg0: Animator) {

            }

            override fun onAnimationStart(arg0: Animator) {

            }
        })
        visToInvis.start()
    }

    /**
     * 缩小动画
     *
     * @param view
     */
    fun zoomIn(view: View, scale: Float, dist: Float) {
        view.pivotY = view.height.toFloat()
        view.pivotX = (view.width / 2).toFloat()
        val mAnimatorSet = AnimatorSet()
        val mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale)
        val mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale)
        val mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist)

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX)
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY)
        mAnimatorSet.duration = 300
        mAnimatorSet.start()
    }

    /**
     * 放大动画
     *
     * @param view
     */
    fun zoomOut(view: View, scale: Float) {
        view.pivotY = view.height.toFloat()
        view.pivotX = (view.width / 2).toFloat()
        val mAnimatorSet = AnimatorSet()

        val mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f)
        val mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f)
        val mAnimatorTranslateY =
            ObjectAnimator.ofFloat(view, "translationY", view.translationY, 1.0f)

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX)
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY)
        mAnimatorSet.duration = 300
        mAnimatorSet.start()
    }

    fun ScaleUpDowm(view: View) {
        val animation = ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f)
        animation.repeatCount = -1
        animation.repeatMode = Animation.RESTART
        animation.interpolator = LinearInterpolator()
        animation.duration = 1200
        view.startAnimation(animation)
    }

    fun animateHeight(start: Int, end: Int, view: View) {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int//根据时间因子的变化系数进行设置高度
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams//设置高度
        }
        valueAnimator.start()
    }

    fun popup(view: View, duration: Long): ObjectAnimator {
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val popup = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        )
        popup.duration = duration
        popup.interpolator = OvershootInterpolator()

        return popup
    }

    fun popout(
        view: View,
        duration: Long,
        animatorListenerAdapter: AnimatorListenerAdapter?
    ): ObjectAnimator {
        val popout = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
            PropertyValuesHolder.ofFloat("scaleX", 1f, 0f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 0f)
        )
        popout.duration = duration
        popout.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
                animatorListenerAdapter?.onAnimationEnd(animation)
            }
        })
        popout.interpolator = AnticipateOvershootInterpolator()

        return popout
    }
}
