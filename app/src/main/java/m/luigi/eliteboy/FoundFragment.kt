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
import kotlinx.coroutines.flow.flow
import m.luigi.eliteboy.adapters.FoundAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.snackBarMessage

@FlowPreview
class FoundFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    val systems = ArrayList<System>()
    var initJob: Job = Job()
    var initLayoutJob: Job = Job()
    private var searchType: EDSMApi.SearchType? = null
    private var currentSystem: String? = null
    private var searchData: Array<String>? = null
    private var isFromNearest = false

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            var searchName: String? = null
            arguments?.let {
                searchData = it.getStringArray("searchData")
                currentSystem = it.getString("currentSystem", "Sol")
                searchName = it.getString("searchType")
                searchType = try {
                    EDSMApi.SearchType.getByType(searchName!!)
                } catch (e: Exception) {
                    null
                }
            }

            if (searchData == null) {
                isFromNearest = true
            }

            val flow = if (isFromNearest) {
                if (!(searchName.isNullOrBlank() || currentSystem.isNullOrBlank())) {
                    (activity as MainActivity).mainToolbar.title =
                        "Nearest ${searchType?.type ?: "Systems"}"
                    EDSMApi.searchNearest(searchType!!, currentSystem!!)
                } else {
                    snackBarMessage { "Shouldn't be here" }
                    initLayoutJob.cancel(CancellationException("No system found"))
                    findNavController().navigateUp()
                    flow {}
                }
            } else {
                searchData?.let {
                    (activity as MainActivity).mainToolbar.title =
                        "Systems found"

                    EDSMApi.searchSystem(it)

                } ?: run {
                    snackBarMessage { "Couldn't find any system" }
                    initLayoutJob.cancel(CancellationException("No system found"))
                    findNavController().navigateUp()
                    flow<System>{}
                }

            }

            initLayoutJob.join()
            systemsSpinKit.visibility = View.VISIBLE
            withTimeoutOrNull(10000) {
                var i = 0
                flow.collect {
                    systems.add(it)

                    onMain {
                        if (systems.size == 1) {
                            val diff = systemsSpinKit.x * 2 - systemsSpinKit.x * 15 / 8
                            systemsSpinKit.animate()
                                .scaleX(0.5f)
                                .scaleY(0.5f)
                                .x(systemsSpinKit.x * 2 - diff)
                                .y(systemsSpinKit.y * 2 - diff)
                                .apply { duration = 800 }
                                .start()
                        }
                        foundList.adapter!!.notifyItemInserted(i)
                        i++
                    }
                    delay(100)
                }
            }

            if (systems.isEmpty()) {
                snackBarMessage { "Couldn't find any system" }
                initLayoutJob.cancel(CancellationException("No system found"))
                findNavController().navigateUp()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_systems, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayoutJob = launch {
            foundList.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            foundList.adapter =
                FoundAdapter(
                    systems,
                    if (isFromNearest) searchType!! else null,
                    this@FoundFragment,
                    view.context
                )
        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            initJob.join()
            systemsSpinKit?.let { it.visibility = View.GONE }

        }
    }

    override fun onPause() {
        super.onPause()
        initJob.cancel()
        initLayoutJob.cancel()
    }
}