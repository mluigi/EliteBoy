package m.luigi.eliteboy.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Body
import m.luigi.eliteboy.pages.BodyPageFragment
import m.luigi.eliteboy.views.DynamicHeightViewPager

class BodyPageAdapter(private val bodies: ArrayList<Body>, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var currentPosition = -1
    override fun getCount(): Int {
        return bodies.size
    }

    override fun getItem(position: Int): Fragment {
        return BodyPageFragment(bodies[position])
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        if (container !is DynamicHeightViewPager) {
            return
        }

        if (position != currentPosition) {
            val fragment = `object` as Fragment
            if (fragment.view != null) {
                currentPosition = position
                container.onPageChanged(fragment.view!!)
            }
        }
    }
}