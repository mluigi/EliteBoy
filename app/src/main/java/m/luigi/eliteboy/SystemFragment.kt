package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_system.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.adapters.BodyPageAdapter
import m.luigi.eliteboy.elitedangerous.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.adapters.StationPageAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.setAnimateOnClickListener
import m.luigi.eliteboy.util.snackBarMessage

class SystemFragment : Fragment() {

    var name: String? = null
    var system: System? = null
    var isInfoOpened = false
    lateinit var getSystemJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSystemJob = GlobalScope.launch {
            onMain { systemSpinKit.visibility = View.VISIBLE }
            arguments?.let {
                name = it.getString("system")
                onMain {
                    (activity as MainActivity).mainToolbar.title = name
                    systemLayout.visibility = View.GONE
                }
                name?.let { system ->
                    this@SystemFragment.system = EDSMApi.getSystemComplete(system)
                }
            }
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

        GlobalScope.launch {
            getSystemJob.join()

            onMain {
                systemSpinKit.visibility = View.GONE
                systemLayout.visibility = View.VISIBLE
                system?.let {

                    infoLayout.setAnimateOnClickListener(
                        infoList,
                        infoImg,
                        { isInfoOpened }) { isInfoOpened = !isInfoOpened }
                    systemName.text = it.name!!
                    infoList.layoutManager =
                        LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    infoList.adapter = InformationAdapter(it.information!!, view.context)

                    stationsPager.adapter = StationPageAdapter(
                        system!!.stations!!,
                        this@SystemFragment.requireFragmentManager()
                    )
                    stationsDots.attachToViewPager(stationsPager)

                    bodiesViewPager.adapter = BodyPageAdapter(
                        system!!.bodies!!,
                        this@SystemFragment.requireFragmentManager()
                    )
                    bodiesDots.attachToViewPager(bodiesViewPager)

                }
            } ?: snackBarMessage { "Couldn't find System $name" }
        }
    }
}
