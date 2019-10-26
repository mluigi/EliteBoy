package m.luigi.eliteboy.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.pages.StationPageFragment

class StationPageAdapter(private val stations: ArrayList<Station>, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return stations.size
    }

    override fun getItem(position: Int): Fragment {
        return StationPageFragment(stations[position])
    }

}