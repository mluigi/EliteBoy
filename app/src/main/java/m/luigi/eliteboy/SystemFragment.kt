package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_system.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.BodyPageAdapter
import m.luigi.eliteboy.adapters.FactionPageAdapter
import m.luigi.eliteboy.adapters.InformationAdapter
import m.luigi.eliteboy.adapters.StationPageAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.*

class SystemFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var name: String? = null
    private var system: System? = null
    private var isInfoOpened = false
    private lateinit var getSystemJob: Job
    private lateinit var initLayoutJob: Job
    private var lastBodyPage = 0
    private var lastStationPage = 0
    private var lastFactionPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSystemJob = launch {
            arguments?.let {
                name = it.getString("system", "")
                //systemLayout.visibility = View.GONE
                systemSpinKit.visibility = View.VISIBLE
                name?.let {
                    runWhenOnline {
                        this@SystemFragment.system = EDSMApi.getSystemComplete(name!!)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            (activity as MainActivity).mainToolbar.title = name
            system?.let {
                setSystemLayout()
            }
            initLayoutJob.join()
            bodiesViewPager.currentItem = lastBodyPage
            stationsPager.currentItem = lastStationPage
            factionsViewPager.currentItem = lastFactionPage
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayoutJob = launch {
            getSystemJob.join()
            systemSpinKit.hideWithAnimation()
            //systemLayout.visibility = View.VISIBLE
            system?.let {
                setSystemLayout()
            } ?: snackBarMessage { "Couldn't find System $name" }
        }
    }

    private fun setSystemLayout() {
        infoLayout.setAnimateOnClickListener(
            infoList,
            infoImg,
            { isInfoOpened }) { isInfoOpened = !isInfoOpened }
        systemName.text = system!!.name!!
        infoList.layoutManager =
            LinearLayoutManager(view!!.context, LinearLayoutManager.VERTICAL, false)
        infoList.adapter = InformationAdapter(system!!.information!!.asMap(), view!!.context)

        if (!system!!.stations.isNullOrEmpty()) {
            stationsPager.adapter = StationPageAdapter(
                system!!.stations!!.apply { sortBy { it.distanceToArrival } },
                childFragmentManager
            )
            stationsPager.setOnPageListenerWhere { lastStationPage = it }
            stationsDots.attachToViewPager(stationsPager)
        } else {
            stationsCard.visibility = View.GONE
        }

        bodiesViewPager.adapter = BodyPageAdapter(
            system!!.bodies!!.apply { sortBy { it.distanceToArrival } },
            this@SystemFragment.requireFragmentManager()
        )
        bodiesViewPager.setOnPageListenerWhere { lastBodyPage = it }
        bodiesDots.attachToViewPager(bodiesViewPager)

        if (!system!!.factions.isNullOrEmpty()) {
            factionsViewPager.adapter = FactionPageAdapter(
                system!!.factions!!.apply { sortByDescending { it.influence } },
                this@SystemFragment.requireFragmentManager()
            )
            factionsViewPager.setOnPageListenerWhere { lastFactionPage = it }
            factionsDots.attachToViewPager(factionsViewPager)
        } else {
            factionsCard.visibility = View.GONE
        }
    }
}
