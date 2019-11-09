package m.luigi.eliteboy.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import m.luigi.eliteboy.util.info


class GenericSuggestionsAdapter(
    context: Context,
    resId: Int,
    private var array: ArrayList<String>
) :
    ArrayAdapter<String>(context, resId, array) {
    override fun getCount(): Int {
        return array.size
    }

    private var filteredList = ArrayList<String>()
    private var originalValues = ArrayList<String>().apply { addAll(array) }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().also {

                    if (constraint.isNullOrBlank()) {
                        it.values = originalValues
                        it.count = originalValues.size
                    } else {
                        val r = originalValues.filter {
                            it.toLowerCase().contains(constraint.toString().toLowerCase())
                        } as ArrayList<String>
                        info { "${array.size} $constraint ${r.size}" }
                        it.values = r
                        it.count = r.size
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.let {
                    array.clear()
                    array.addAll(it.values as ArrayList<String>)
                    notifyDataSetChanged()
                } ?: kotlin.run { array = originalValues }
            }
        }
    }
}