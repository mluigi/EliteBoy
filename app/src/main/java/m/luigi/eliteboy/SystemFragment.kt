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
import m.luigi.eliteboy.adapters.BodyPageAdapter
import m.luigi.eliteboy.adapters.InformationAdapter
import m.luigi.eliteboy.adapters.StationPageAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.setAnimateOnClickListener
import m.luigi.eliteboy.util.snackBarMessage

class SystemFragment : Fragment() {

    var name: String? = null
    private var system: System? = null
    private var isInfoOpened = false
    private lateinit var getSystemJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSystemJob = GlobalScope.launch {
            arguments?.let {
                name = it.getString("system", "")
                onMain {
                    (activity as MainActivity).mainToolbar.title = name
                    systemLayout.visibility = View.GONE
                    systemSpinKit.visibility = View.VISIBLE
                }
                name?.let {
                    this@SystemFragment.system = onIO { EDSMApi.getSystemComplete(name!!) }
                }


            }
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch{
            onMain{
                (activity as MainActivity).mainToolbar.title = name
                system?.let {
                    setSystemLayout()
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
                    setSystemLayout()
                } ?: snackBarMessage { "Couldn't find System $name" }
            }
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

        stationsPager.adapter = StationPageAdapter(
            system!!.stations!!.apply { sortBy { it.distanceToArrival } } ,
            childFragmentManager
        )
        stationsDots.attachToViewPager(stationsPager)

        bodiesViewPager.adapter = BodyPageAdapter(
            system!!.bodies!!.apply { sortBy { it.distanceToArrival } },
            this@SystemFragment.requireFragmentManager()
        )
        bodiesDots.attachToViewPager(bodiesViewPager)


    }
}
