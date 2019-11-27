package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.CommodityAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.runWhenOnline
import m.luigi.eliteboy.util.snackBarMessage

class CommoditiesFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private var station: Station? = null
    private lateinit var initJob: Job
    private var initLayoutJob: Job = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            arguments?.let {
                station = it.getParcelable("station")!!
                station?.let {
                    if (it.haveMarket) {
                        runWhenOnline {
                            EDSMApi.getMarket(it)
                        }
                    }
                }
            } ?: snackBarMessage { "No Station found" }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onPause() {
        super.onPause()
        initJob.cancel()
        initLayoutJob.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayoutJob = launch {
            initJob.join()
            station?.let {
                if (it.haveMarket && it.commodities != null) {
                    infoList?.layoutManager =
                        LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    infoList?.adapter = CommodityAdapter(it.commodities!!, view.context)
                } else {
                    noFoundText.text = "This station doesn't have a market."
                    noFoundText.visibility =View.VISIBLE
                }
            } ?: snackBarMessage { "Error passing station to market" }
        }
    }
}