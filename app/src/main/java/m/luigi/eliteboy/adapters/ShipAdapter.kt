package m.luigi.eliteboy.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ship_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.companionapi.data.Ship
import m.luigi.eliteboy.util.CoriolisDataHelper
import m.luigi.eliteboy.util.shipIdToImgId
import java.text.NumberFormat
import java.util.*

class ShipAdapter(
    private val ships: ArrayList<Ship>,
    val context: Context,
    val origin: String
) :
    RecyclerView.Adapter<ShipAdapter.ViewHolder>(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.ship_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return ships.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        launch {
            val ship = ships[position]
            with(holder) {
                when (origin) {
                    "shipsFragment" -> {
                        shipImg.setImageDrawable(
                            Drawable.createFromStream(
                                context.assets.open("${shipIdToImgId[ship.name!!.toLowerCase()]}.png"),
                                shipIdToImgId[ship.name!!.toLowerCase()]
                            )
                        )
                        shipNameTextView.text =
                            CoriolisDataHelper.shipIdtoShipNameMap()[ship.name!!.toLowerCase(
                                Locale.getDefault()
                            )]

                        shipNickTextView.text = if (ship.shipName == null) {
                            ""
                        } else {
                            "\"${ship.shipName}\""
                        }
                        systemTextTextView.text = ship.starsystem!!.name!!
                        stationTextView.text = ship.station!!.name!!
                        valueTextView.text = String.format(
                            context.resources.getString(R.string.credits),
                            NumberFormat.getIntegerInstance().format(ship.value!!.total)
                        )
                    }
                    "stationFragment" -> {
                        shipImg.setImageDrawable(
                            Drawable.createFromStream(
                                context.assets.open("${shipIdToImgId[CoriolisDataHelper.getEDIDToShipIDMap()[ship.id!!]]}.png"),
                                shipIdToImgId[ship.name!!.toLowerCase()]
                            )
                        )
                        shipNameTextView.text = CoriolisDataHelper.getEDIDToShipNameMap()[ship.id!!]
                        shipNickTextView.visibility = View.GONE
                        systemTextTextView.visibility = View.GONE
                        stationTextView.visibility = View.GONE
                        valueTextView.text = CoriolisDataHelper.getEDIDtoPriceMap()[ship.id!!]
                    }
                }
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shipImg: ImageView = view.shipImg
        val shipNameTextView: TextView = view.shipNameTextView
        val shipNickTextView: TextView = view.shipNickTextView
        val systemTextTextView: TextView = view.systemTextView
        val stationTextView: TextView = view.stationTextView
        val valueTextView: TextView = view.valueTextView
    }
}