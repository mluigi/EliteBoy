package m.luigi.eliteboy.elitedangerous.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.commodities_item.view.*
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.companionapi.data.Commodity
import java.text.NumberFormat


class CommodityAdapter(private val commodities: ArrayList<Commodity>, val context: Context) :
    RecyclerView.Adapter<CommodityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.commodities_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return commodities.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.name.text = "Name"

            with(holder.buySell) {
                val buy = SpannableString("Buy")
                val sell = SpannableString("Sell")
                buy.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    0,
                    buy.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                sell.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    sell.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                text = buy
                append("/")
                append(sell)
            }
            holder.demandStock.text = "Demand/Stock"
            holder.buySell.setTypeface(holder.buySell.typeface, Typeface.BOLD)
            holder.name.setTypeface(holder.name.typeface, Typeface.BOLD)
            holder.demandStock.setTypeface(holder.demandStock.typeface, Typeface.BOLD)
        } else {
            with(commodities[position - 1]) {
                holder.name.text = name

                val nFormat = NumberFormat.getIntegerInstance()

                if (buyPrice!! > sellPrice!!) {
                    val buy = SpannableString(nFormat.format(buyPrice))
                    buy.setSpan(
                        ForegroundColorSpan(Color.GREEN),
                        0,
                        buy.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.buySell.text = buy
                    holder.demandStock.text = nFormat.format(stock)
                } else {
                    val sell = SpannableString(nFormat.format(sellPrice))
                    sell.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        sell.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.buySell.text = sell
                    holder.demandStock.text = nFormat.format(demand)
                }


                holder.buySell.typeface = null
                holder.name.typeface = null
                holder.demandStock.typeface = null
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.nameView
        val buySell = view.buySellView
        val demandStock = view.demandStockView

    }

}
