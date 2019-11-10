package m.luigi.eliteboy.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.Transformation
import androidx.viewpager.widget.ViewPager

//From https://github.com/iffa/wrapping-viewpager
class DynamicHeightViewPager : ViewPager, Animation.AnimationListener {
    private var mCurrentView: View? = null
    private val mAnimation = PagerAnimation()
    private var mAnimStarted = false
    private var mAnimDuration: Long = 100

    constructor(context: Context) : super(context) {
        mAnimation.setAnimationListener(this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mAnimation.setAnimationListener(this)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!mAnimStarted && mCurrentView != null) {
            var height: Int
            mCurrentView!!.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            height = mCurrentView!!.measuredHeight

            if (height < minimumHeight) {
                height = minimumHeight
            }

            val newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            if (layoutParams.height != 0 && heightMeasureSpec != newHeight) {
                mAnimation.setDimensions(height, layoutParams.height)
                mAnimation.duration = mAnimDuration
                startAnimation(mAnimation)
                mAnimStarted = true
            } else {
                heightMeasureSpec = newHeight
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun onPageChanged(currentView: View) {
        mCurrentView = currentView
        requestLayout()
    }

    private inner class PagerAnimation : Animation() {
        private var targetHeight: Int = 0
        private var currentHeight: Int = 0
        private var heightChange: Int = 0


        internal fun setDimensions(targetHeight: Int, currentHeight: Int) {
            this.targetHeight = targetHeight
            this.currentHeight = currentHeight
            this.heightChange = targetHeight - currentHeight
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime >= 1) {
                layoutParams.height = targetHeight
            } else {
                val stepHeight = (heightChange * interpolatedTime).toInt()
                layoutParams.height = currentHeight + stepHeight
            }
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    fun setAnimationDuration(duration: Long) {
        mAnimDuration = duration
    }

    fun setAnimationInterpolator(interpolator: Interpolator) {
        mAnimation.interpolator = interpolator
    }

    override fun onAnimationStart(animation: Animation) {
        mAnimStarted = true
    }

    override fun onAnimationEnd(animation: Animation) {
        mAnimStarted = false
    }

    override fun onAnimationRepeat(animation: Animation) {}
}