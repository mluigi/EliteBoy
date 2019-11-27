package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_station.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.edsm.data.Station


class StationFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    lateinit var station: Station
    private lateinit var initJob: Job
    private var initLayoutJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            arguments?.let {
                station = it.getParcelable("station")!!

                (activity as MainActivity).mainToolbar.title = station.name
            }
        }
    }

    override fun onPause() {
        super.onPause()
        initJob.cancel()
        initLayoutJob.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayoutJob = launch {
            val adapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(view.context)
                    .add(
                        "Info", InformationFragment::class.java,
                        bundleOf("station" to station)
                    )
                    .add(
                        "Market", CommoditiesFragment::class.java,
                        bundleOf("station" to station)
                    )
                    .add(
                        "Shipyard", ShipsFragment::class.java,
                        bundleOf("station" to station, "origin" to "stationFragment")
                    )
                    .add(
                        "Outfitting", OutfittingFragment::class.java,
                        bundleOf("station" to station)
                    )
                    .create()
            )
            viewpager.adapter = adapter
            viewpagertab.setViewPager(viewpager)
        }
    }
}
