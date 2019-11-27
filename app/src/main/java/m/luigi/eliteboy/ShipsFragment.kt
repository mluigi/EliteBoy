package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ships.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.ShipAdapter
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.onDefault
import m.luigi.eliteboy.util.runWhenOnline
import m.luigi.eliteboy.util.snackBarMessageIf

class ShipsFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var initJob: Job
    private lateinit var initLayoutJob: Job
    private var ships: ArrayList<Ship>? = null
    private var origin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = launch {
            arguments?.let {
                val ship: Ship? = it.getParcelable("ship")
                var ships = it.getParcelableArrayList<Ship>("ships")
                origin = it.getString("origin")
                val station = it.getParcelable<Station>("station")

                snackBarMessageIf(ships == null && origin == null) {
                    findNavController().navigateUp()
                    "Couldn't find any ship"
                }
                station?.let { st ->
                    if (st.haveShipyard) {
                        runWhenOnline {
                            EDSMApi.getShipyard(st)
                        }
                        ships = st.ships
                    }
                }

                onDefault {
                    ship?.let {
                        ships?.removeIf { it.id!! == ship.id }
                        ships?.add(0, ship)
                    }
                }

                this@ShipsFragment.ships = ships
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ships, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (!origin.isNullOrBlank() && origin != "stationFragment") {
            (activity as MainActivity).mainToolbar.title = "Ships"
        }
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
            shipList?.layoutManager = GridLayoutManager(view.context, 2)
            shipList?.adapter =
                ShipAdapter(
                    if (ships.isNullOrEmpty()) {
                        if (!origin.isNullOrBlank()) {
                            noShipFoundText.visibility = View.VISIBLE
                            noShipFoundText.text = "This station doesn't sell ships."
                        }
                        ArrayList()
                    } else {
                        ships!!
                    }, view.context, origin ?: "shipsFragment"
                )

        }

    }
}
