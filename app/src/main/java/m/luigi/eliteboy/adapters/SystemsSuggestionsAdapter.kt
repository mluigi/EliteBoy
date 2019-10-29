package m.luigi.eliteboy.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter


class SystemsSuggestionsAdapter(context: Context, resId: Int, val results: ArrayList<String>) :
    ArrayAdapter<String>(context, resId, results) {
    override fun getCount(): Int {
        return results.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                return FilterResults().also {
                    it.values = results
                    it.count = results.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.let {
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            }
        }
    }
}