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
import kotlinx.coroutines.*
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.hideWithAnimation
import m.luigi.eliteboy.util.runWhenOnline


class StationFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    lateinit var station: Station
    private lateinit var initJob: Job
    private lateinit var getStationJob: Job
    private var initLayoutJob: Job = Job()

    private var isInfoOpened = true
    private var isMarketOpened = false
    private var isShipyardOpened = false
    private var isOutfittingOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            arguments?.let {
                station = it.getParcelable("station")!!

                //stationLayout.visibility = View.GONE
                stationSpinKit.visibility = View.VISIBLE
                (activity as MainActivity).mainToolbar.title = station.name
            }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onResume() {
        super.onResume()
        getStationJob = launch {
            initJob.join()
            if (station.haveMarket) {
                runWhenOnline {
                    EDSMApi.getMarket(station)
                }
            }

            if (station.haveShipyard) {
                runWhenOnline {
                    EDSMApi.getShipyard(station)
                }
            }

            if (station.haveMarket) {
                runWhenOnline {
                    EDSMApi.getOutfitting(station)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        initJob.cancel()
        getStationJob.cancel()
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
            getStationJob.join()
            val adapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(view.context)
                    .add(
                        "Info",
                        InformationFragment::class.java,
                        bundleOf("station" to station)
                    )
                    .add("Market", ShipsFragment::class.java)
                    .add(
                        "Shipyard",
                        ShipsFragment::class.java,
                        bundleOf("ships" to station.ships, "origin" to "stationFragment")
                    )
                    .add("Outfitting", ShipsFragment::class.java)
                    .create()
            )
            viewpager.adapter = adapter
            viewpagertab.setViewPager(viewpager)
            //stationLayout?.visibility = View.VISIBLE
            stationSpinKit?.hideWithAnimation()
        }
    }
}
