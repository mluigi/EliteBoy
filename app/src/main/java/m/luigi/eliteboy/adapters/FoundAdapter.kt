package m.luigi.eliteboy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.data_item.view.*
import m.luigi.eliteboy.R
import m.luigi.eliteboy.SystemsFragment
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.System
import java.text.NumberFormat

class FoundAdapter(
    val systems: ArrayList<System>,
    val searchType: EDSMApi.SearchType,
    val fragment: SystemsFragment,
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val system = systems[position]
        holder.primaryData.text = system.name
        with(holder.secondaryData) {
            text = String.format("%s ly", NumberFormat.getNumberInstance().format(system.distance))
            visibility = View.VISIBLE
        }

        when (searchType.returnType) {
            0 -> {
            }
            1 -> {
                val map = mutableMapOf<String, String>()
                system.stations!!.sortedBy { it.distanceToArrival }.forEach {
                    map[it.name!!] = String.format(
                        "%s ls",
                        NumberFormat.getIntegerInstance().format(it.distanceToArrival)
                    )
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

        val clickListener = View.OnClickListener {
            fragment.findNavController().navigate(
                R.id.action_systemsFragment_to_systemFragment,
                bundleOf("system" to system.name!!)
            )
        }
        holder.dataLayout.setOnClickListener(clickListener)
        holder.infoList.setOnClickListener(clickListener)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val primaryData = view.primaryData
        val secondaryData = view.secondaryData
        val infoList = view.infoList
        val dataLayout = view.dataLayout
    }
}