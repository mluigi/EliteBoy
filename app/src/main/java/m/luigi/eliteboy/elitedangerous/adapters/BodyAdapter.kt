package m.luigi.eliteboy.elitedangerous.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.system_information_item.view.*
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.edsm.data.Body

class BodyAdapter(private val body: Body, val context: Context) :
    RecyclerView.Adapter<BodyAdapter.ViewHolder>() {
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
        return body.asMap().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        body.asMap().entries.elementAt(position).toPair().let { (k, v) ->
            holder.infoProperty.text = k
            holder.infoValue.text = v
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val infoProperty = view.infoProperty
        val infoValue = view.infoValue

    }
}