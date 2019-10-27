package m.luigi.eliteboy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_nearest.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.PropertyAdapter
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi

class NearestFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var currentSystem = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        launch {
            currentSystem = EDCompanionApi.getLastPosition()
            refSystem.setText(currentSystem)
            (activity as MainActivity).mainToolbar.title = "Nearest"
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch {
            searchTypes.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            searchTypes.adapter =
                PropertyAdapter(
                    EDSMApi.SearchType.values().map { it.type } as ArrayList<String>,
                    this@NearestFragment,
                    view.context
                )

        }
    }

}
