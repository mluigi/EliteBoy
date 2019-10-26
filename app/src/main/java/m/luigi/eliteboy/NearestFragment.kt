package m.luigi.eliteboy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_nearest.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.util.onIO

class NearestFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var lastSystem = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        launch {
            updateLastPosition()
            refSystem.setText(lastSystem)
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearest, container, false)
    }


    private suspend fun updateLastPosition(forcedUpdate: Boolean = false) {
        lastSystem = if (EDCompanionApi.currentState == EDCompanionApi.State.AUTHORIZED) {
            onIO { EDCompanionApi.getProfile(forcedUpdate)!!.lastSystem!!.name!! }
        } else ""
    }
}
