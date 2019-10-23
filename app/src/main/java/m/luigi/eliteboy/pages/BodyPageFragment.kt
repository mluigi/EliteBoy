package m.luigi.eliteboy.pages


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bodies_page.*
import m.luigi.eliteboy.R
import m.luigi.eliteboy.elitedangerous.adapters.InformationAdapter
import m.luigi.eliteboy.elitedangerous.edsm.data.Body

class BodyPageFragment(private val body: Body) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bodies_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bodyList.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        bodyList.adapter = InformationAdapter(body.asMap(), context!!)

    }

}
