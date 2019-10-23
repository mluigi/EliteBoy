package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_station.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain


class StationFragment : Fragment() {
    lateinit var station: Station
    lateinit var getStationJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getStationJob = GlobalScope.launch {
            arguments?.let {
                station = it.getParcelable("station") as Station
                onMain {
                    stationLayout.visibility = View.GONE
                    stationSpinKit.visibility = View.VISIBLE
                    (activity as MainActivity).mainToolbar.title = station.name
                }
                onIO { EDSMApi.getCompleteStation(station) }
            }
        }

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

        GlobalScope.launch {
            getStationJob.join()

            onMain {
                stationLayout.visibility = View.VISIBLE
                stationSpinKit.visibility = View.GONE
                infoList.layoutManager =
                    LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                infoList.adapter = InformationAdapter(station.asMap(), view.context)
            }
        }
    }

}
