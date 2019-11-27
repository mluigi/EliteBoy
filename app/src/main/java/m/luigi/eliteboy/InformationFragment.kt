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
import m.luigi.eliteboy.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.snackBarMessage

class InformationFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private var info: Map<String, String>? = null
    private lateinit var initJob: Job
    private var initLayoutJob: Job = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            arguments?.let {
                info = it.getParcelable<Station>("station")!!.asMap()
            } ?: snackBarMessage { "No info found" }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayoutJob = launch {
            initJob.join()

            infoList?.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            infoList?.adapter = InformationAdapter(info!!, view.context)
        }
    }
}