package m.luigi.eliteboy.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Body
import m.luigi.eliteboy.pages.BodyPageFragment

class BodyPageAdapter(private val bodies: ArrayList<Body>, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return bodies.size
    }

    override fun getItem(position: Int): Fragment {
        return BodyPageFragment(bodies[position])
    }

}