package m.luigi.eliteboy.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.pages.StationPageFragment
import m.luigi.eliteboy.views.DynamicHeightViewPager

class StationPageAdapter(private val stations: ArrayList<Station>, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    private var currentPosition = -1
    override fun getCount(): Int {
        return stations.size
    }

    override fun getItem(position: Int): Fragment {
        return StationPageFragment(stations[position])
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        if (container !is DynamicHeightViewPager) {
            return  // Do nothing if it's not a compatible ViewPager
        }

        if (position != currentPosition) { // If the position has changed, tell WrappingViewPager
            val fragment = `object` as Fragment
            if (fragment.view != null) {
                currentPosition = position
                container.onPageChanged(fragment.view!!)
            }
        }
    }
}