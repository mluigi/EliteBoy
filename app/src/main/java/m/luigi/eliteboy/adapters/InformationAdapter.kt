package m.luigi.eliteboy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.system_information_item.view.*
import m.luigi.eliteboy.R
import m.luigi.eliteboy.views.AutoScrollingTextView

class InformationAdapter(private val informationMap: Map<String,String>, val context: Context) :
    RecyclerView.Adapter<InformationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.system_information_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return informationMap.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        informationMap.entries.elementAt(position).toPair().let { (k, v) ->
            holder.infoProperty.text = k
            holder.infoValue.text = v
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val infoProperty: AutoScrollingTextView = view.infoProperty
        val infoValue: TextView = view.infoValue

    }
}