package m.luigi.eliteboy.pages


import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_station_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.luigi.eliteboy.MainActivity
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.stationImage
import kotlin.math.roundToInt

class StationPageFragment(var station: Station) :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            onMain {
                nameView.text = station.name
                typeView.text = station.type
                distanceView.text = String.format(
                    "%s Ls",
                    NumberFormat.getIntegerInstance().format(station.distanceToArrival.roundToInt())
                )
                factionView.text = station.controllingFaction!!.name
                detailButton.setOnClickListener { findNavController().navigate(R.id.action_systemFragment_to_stationFragment,
                    bundleOf(
                        "station" to station
                    )) }
                (activity as MainActivity).imageLoader
                    .displayImage(stationImage(station.type!!), typeImg)
            }
        }
    }
}
