package m.luigi.eliteboy.util

import android.animation.Animator
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import m.luigi.eliteboy.R
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


fun Fragment.snackBarMessage(message: () -> String) {
    Snackbar.make(this.view!!, message(), Snackbar.LENGTH_LONG).show()
}

fun Fragment.snackBarMessageIf(bool: Boolean, message: () -> String) {
    if (bool) {
        Snackbar.make(this.view!!, message(), Snackbar.LENGTH_LONG).show()
    }
}

fun <T : Any> T.info(message: () -> String) {
    Log.i(this.javaClass.canonicalName, message())
}


fun <T : Any> T.error(message: () -> String) {
    Log.e(this.javaClass.canonicalName, message())
}

fun View.expand() {
    this.measure(
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val targetHeight = this.measuredHeight
    this.visibility = View.VISIBLE
    this.layoutParams.height = 0

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            this@expand.layoutParams.height = if (interpolatedTime == 1f) {
                WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                (targetHeight * interpolatedTime).toInt()
            }
            this@expand.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = 800.toLong()
    this.startAnimation(a)
}

fun View.collapse() {
    val initialHeight = this.measuredHeight

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                this@collapse.visibility = View.GONE
            } else {
                this@collapse.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = 800.toLong()
    this.startAnimation(a)
}

fun View.setAnimateOnClickListener(
    viewToAnimate: View,
    viewToRotate: View,
    bool: () -> Boolean,
    block: () -> Unit
) {
    setOnClickListener {
        if (bool()) {
            viewToAnimate.collapse()
            viewToRotate.animate().rotation(90f).apply { duration = 800 }.start()
        } else {
            viewToAnimate.expand()
            viewToRotate.animate().rotation(270f).apply { duration = 800 }.start()
        }
        block()
    }
}


suspend fun <T> onDefault(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default) {
        block()
    }
}

suspend fun <T> onIO(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}

suspend fun <T> onMain(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

fun Long?.isNullOrZero(): Boolean {
    return this == 0L || this == null
}

suspend fun runWhile(block: suspend CoroutineScope.(stop: () -> Unit) -> Unit) {
    var isRunning = true
    while (isRunning) {
        onDefault {
            block { isRunning = false }
        }
    }
}

/* Function to add a simple onPageListener to a ViewPager
 * that will run the function at every onPageChange.
 * nPage accepts an Int, the position of the page
 */

fun ViewPager.setOnPageListenerWhere(function: (page: Int) -> Unit) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            function(position)
        }
    })
}

fun Fragment.makeAlertDialog(
    items: Array<String>,
    title: String,
    textViewToUpdate: TextView
): AlertDialog {
    return AlertDialog.Builder(this.requireContext()).apply {
        setTitle(title)
        var updateF: () -> Unit = { textViewToUpdate.text = items[0] }
        setSingleChoiceItems(items, 0) { _, which ->
            updateF = { textViewToUpdate.text = items[which] }
        }
        setPositiveButton("OK") { _, _ ->
            updateF()
        }

        setNegativeButton("None") { _, _ ->
            textViewToUpdate.text = ""
        }
    }.create()
}

fun Fragment.makeMultiChoiceAlertDialog(
    items: Array<String>,
    title: String,
    textViewToUpdate: TextView
): AlertDialog {
    return AlertDialog.Builder(this.requireContext()).apply {
        setTitle(title)
        val chosenShips = arrayListOf<String>()
        setMultiChoiceItems(items, BooleanArray(items.size) { false }) { _, which, isChecked ->
            if (isChecked) {
                chosenShips.add(items[which])
            } else {
                chosenShips.remove(items[which])
            }
        }
        setPositiveButton("OK") { _, _ ->
            textViewToUpdate.text = chosenShips.joinToString(separator = ", ")
        }

        setNegativeButton("None") { _, _ ->
            textViewToUpdate.text = ""

        }
    }.create()
}

suspend fun isOnline(): Boolean {
    return onIO {
        try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}

suspend fun <T> Fragment.runWhenOnline(block: suspend () -> T): T? {
    return if (isOnline()) {
        block()
    } else {
        findNavController().navigate(R.id.noConnectivityFragment)
        null
    }
}

suspend fun Activity.runWhenOnline(block: suspend () -> Unit) {
    if (isOnline()) {
        block()
    } else {
        findNavController(R.id.navHost).navigate(R.id.noConnectivityFragment)
    }
}

fun View.hideWithAnimation() {
    animate()
        .scaleY(layoutParams.height * 1.1f)
        .scaleX(layoutParams.width * 1.1f)
        .setDuration(500)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animate()
                    .scaleY(0f)
                    .scaleX(0f)
                    .setDuration(500)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }

                    })
                    .start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        .start()
}

fun View.hideKeyboard(){
    val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(windowToken, 0)
}