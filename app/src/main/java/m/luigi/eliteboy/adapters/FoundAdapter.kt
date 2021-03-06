package m.luigi.eliteboy.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.data_item.view.*
import kotlinx.coroutines.FlowPreview
import m.luigi.eliteboy.FoundFragment
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import java.text.NumberFormat

@FlowPreview
class FoundAdapter(
    val systems: ArrayList<System>,
    private val searchType: EDSMApi.SearchType?=null,
    val fragment: FoundFragment,
    val context: Context
) :
    RecyclerView.Adapter<FoundAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.data_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return systems.size

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val system = systems[position]
        holder.primaryData.text = system.name
        with(holder.secondaryData) {
            text = String.format("%s ly", NumberFormat.getNumberInstance().format(system.distance))
            visibility = View.VISIBLE
        }

        searchType?.let {
            when (searchType.returnType) {
                0 -> {
                }
                1, 3 -> {
                    val map = mutableMapOf<String, String>()
                    system.stations!!.sortedBy { it.distanceToArrival }.forEach {
                        map[it.name!!] = String.format(
                            "%s ls",
                            NumberFormat.getIntegerInstance().format(it.distanceToArrival)
                        )
                        if (searchType.returnType == 3) {
                            if (it.otherServices!!.contains("Material Trader")){
                                map["Trader Type"] = it.traderType!!
                            }
                            if (it.otherServices!!.contains("Technology Broker")){
                                map["Broker Type"] = it.brokerType!!
                            }
                        }
                    }

                    with(holder.infoList) {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = InformationAdapter(map, context)
                        visibility = View.VISIBLE
                    }


                }
                2 -> {
                    val map = mutableMapOf<String, String>()
                    system.factions!!.forEach {
                        if (it.state!! == searchType.type) {
                            map[it.name!!] = it.state!!
                        }
                    }
                    with(holder.infoList) {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = InformationAdapter(map, context)
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        val clickListener = View.OnClickListener {
            fragment.findNavController().navigate(
                R.id.action_systemsFragment_to_systemFragment,
                bundleOf("system" to system.name!!)
            )

        }
        holder.dataLayout.setOnClickListener(clickListener)
        holder.infoList.setOnClickListener(clickListener)

        //Had do add this because just setting the onClickListener doesn't work
        holder.infoList.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val primaryData: TextView = view.primaryData
        val secondaryData: TextView = view.secondaryData
        val infoList: RecyclerView = view.infoList
        val dataLayout: ConstraintLayout = view.dataLayout
    }
}