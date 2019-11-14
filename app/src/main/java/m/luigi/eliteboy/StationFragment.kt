package m.luigi.eliteboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_station.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.CommodityAdapter
import m.luigi.eliteboy.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.CoriolisDataHelper
import m.luigi.eliteboy.util.hideWithAnimation
import m.luigi.eliteboy.util.runWhenOnline
import m.luigi.eliteboy.util.setAnimateOnClickListener


class StationFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    lateinit var station: Station
    private lateinit var getStationJob: Job

    private var isInfoOpened = true
    private var isMarketOpened = false
    var isShipyardOpened = false
    var isOutfittingOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getStationJob = launch {
            arguments?.let {
                station = it.getParcelable("station")!!

                //stationLayout.visibility = View.GONE
                stationSpinKit.visibility = View.VISIBLE
                (activity as MainActivity).mainToolbar.title = station.name

                if (station.haveMarket) {
                    runWhenOnline {
                        EDSMApi.getMarket(station)
                    }
                }

                if (station.haveShipyard) {
                    runWhenOnline {
                        EDSMApi.getShipyard(station)
                    }
                }

                if (station.haveMarket) {
                    runWhenOnline {
                        EDSMApi.getOutfitting(station)
                    }
                }
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

        launch {
            getStationJob.join()

            infoList?.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            infoList?.adapter = InformationAdapter(station.asMap(), view.context)

            infoLayout?.setAnimateOnClickListener(
                infoList,
                infoImg,
                { isInfoOpened }) { isInfoOpened = !isInfoOpened }

            if (station.haveMarket && station.commodities != null) {
                marketLayout?.setAnimateOnClickListener(
                    marketList,
                    marketImg,
                    { isMarketOpened }) { isMarketOpened = !isMarketOpened }

                marketList?.layoutManager =
                    LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                marketList?.adapter = CommodityAdapter(station.commodities!!, view.context)
            } else {
                marketCardView?.visibility = View.GONE
            }

            if (station.haveShipyard && station.ships != null) {
                shipsLayout?.setAnimateOnClickListener(
                    shipList,
                    shipImg,
                    { isShipyardOpened }) { isShipyardOpened = !isShipyardOpened }
                shipList?.layoutManager =
                    LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                shipList?.adapter = InformationAdapter(
                    CoriolisDataHelper.getShipPriceMapFiltered(station.ships!!),
                    view.context
                )
            } else {
                shipsCardView?.visibility = View.GONE
            }

            if (station.haveOutfitting && station.outfitting != null) {
                outfittingLayout?.setAnimateOnClickListener(
                    outfittingList,
                    outfittingImg,
                    { isOutfittingOpened }) { isOutfittingOpened = !isOutfittingOpened }

                outfittingList?.layoutManager =
                    LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                val mapSIDtoName = mutableMapOf<String, String>()
                station.outfitting!!.forEach {
                    mapSIDtoName[it.sId!!] = it.name!!
                }
                val modulePriceMap = CoriolisDataHelper.getModulePriceMapFiltered(mapSIDtoName)
                outfittingList?.adapter = InformationAdapter(
                    modulePriceMap,
                    view.context
                )
            } else {
                outfittingCardView?.visibility = View.GONE
            }

            //stationLayout?.visibility = View.VISIBLE
            stationSpinKit?.hideWithAnimation()
        }
    }
}
