package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_systems.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import m.luigi.eliteboy.adapters.FoundAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.snackBarMessage

@FlowPreview
class SystemsFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    val systems = ArrayList<System>()
    var searchJob: Job = Job()
    var initLayoutJob: Job = Job()
    var searchType: EDSMApi.SearchType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchJob = launch {
            var currentSystem: String? = null
            var searchName: String? = null
            arguments?.let {
                currentSystem = it.getString("currentSystem", "Sol")
                searchName = it.getString("searchType")
            }

            if (!(searchName.isNullOrBlank() || currentSystem.isNullOrBlank())) {
                searchType = EDSMApi.SearchType.getByType(searchName!!)
                (activity as MainActivity).mainToolbar.title =
                    "Nearest ${searchType?.type ?: "Systems"}"
                val flow = EDSMApi.search(searchType!!, currentSystem!!)
                initLayoutJob.join()
                withTimeoutOrNull(10000) {
                    var i = 0
                    flow.collect {
                        systems.add(it)

                        onMain {
                            if (systems.isNotEmpty()) {
                                systemsSpinKit.visibility = View.GONE
                            }
                            foundList.adapter!!.notifyItemInserted(i)
                            i++
                        }
                        delay(100)
                    }
                }

                if (systems.isEmpty()) {
                    snackBarMessage { "Couldn't find nearest ${searchType!!.type}" }
                    initLayoutJob.cancel(CancellationException("No system found"))
                    findNavController().navigateUp()
                }
            } else {
                snackBarMessage { "Shouldn't be here" }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_systems, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayoutJob = launch {
            systemsSpinKit.visibility = View.VISIBLE
            foundList.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            foundList.adapter =
                FoundAdapter(systems, searchType!!, this@SystemsFragment, view.context)
        }
    }
}