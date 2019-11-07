package m.luigi.eliteboy.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_faction_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import m.luigi.eliteboy.R
import m.luigi.eliteboy.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Faction

class FactionPageFragment(var faction: Faction) : Fragment(), CoroutineScope by CoroutineScope(
    Dispatchers.Main
) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faction_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            factionList.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            factionList.adapter = InformationAdapter(faction.asMap(), context!!)
        }

    }
}