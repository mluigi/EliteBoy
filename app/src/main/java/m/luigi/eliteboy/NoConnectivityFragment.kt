package m.luigi.eliteboy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_no_connectivity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.util.isOnline

class NoConnectivityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_connectivity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryAgainButton.setOnClickListener {
            GlobalScope.launch {
                if (isOnline()) {
                    EDCompanionApi.refreshToken()
                    findNavController().navigateUp()
                }
            }
        }
    }
}
