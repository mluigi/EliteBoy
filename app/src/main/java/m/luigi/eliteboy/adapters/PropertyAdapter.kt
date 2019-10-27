package m.luigi.eliteboy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.data_item.view.*
import m.luigi.eliteboy.NearestFragment
import m.luigi.eliteboy.R

class PropertyAdapter(
    private val searchTypes: ArrayList<String>,
    val fragment: NearestFragment,
    val context: Context
) :
    RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {
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
        return searchTypes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val primaryData = searchTypes[position]

        holder.primaryData.text = primaryData

        holder.dataLayout.setOnClickListener {
            fragment.findNavController().navigate(
                R.id.action_nearestFragment_to_systemsFragment, bundleOf(
                    "searchType" to primaryData,
                    "currentSystem" to fragment.currentSystem
                )
            )
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val primaryData = view.primaryData
        val secondaryData = view.secondaryData
        val infoList = view.infoList
        val dataLayout = view.dataLayout
    }
}