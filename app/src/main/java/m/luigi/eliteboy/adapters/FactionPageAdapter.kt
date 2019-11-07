package m.luigi.eliteboy.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Faction
import m.luigi.eliteboy.pages.FactionPageFragment

class FactionPageAdapter(
    private val factions: ArrayList<Faction>,
    fragmentManager: FragmentManager
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var currentPosition = -1
    override fun getCount(): Int {
        return factions.size
    }

    override fun getItem(position: Int): Fragment {
        return FactionPageFragment(factions[position])
    }
}