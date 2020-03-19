package m.luigi.eliteboy.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            with(holder.demandStock){
                val demand = SpannableString("Demand")
                val stock = SpannableString("Stock")
                demand.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    demand.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                stock.setSpan(ForegroundColorSpan(Color.GREEN),
                    0,
                    stock.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                text= stock
                append("/")
                append(demand)
            }
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
                    val stock = SpannableString(nFormat.format(stock))
                    stock.setSpan(ForegroundColorSpan(Color.GREEN),
                        0,
                        stock.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.demandStock.text = stock
                } else {
                    val sell = SpannableString(nFormat.format(sellPrice))
                    sell.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        sell.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    val demand = SpannableString(nFormat.format(demand))
                    demand.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        demand.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.buySell.text = sell
                    holder.demandStock.text = demand
                }


                holder.buySell.typeface = null
                holder.name.typeface = null
                holder.demandStock.typeface = null
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.nameView
        val buySell: TextView = view.buySellView
        val demandStock: TextView = view.demandStockView

    }

}
