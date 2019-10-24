package m.luigi.eliteboy


import android.annotation.TargetApi
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.*

class ProfileFragment : Fragment() {

    private lateinit var imageLoader: ImageLoader
    private var initJob: Job = Job()

    private var isRanksOpened = false
    private var isReputationOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = GlobalScope.launch {
            imageLoader = (activity as MainActivity).imageLoaderDeferred.await()
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GlobalScope.launch {
            initJob.join()
            (activity as MainActivity).waitForInitApi!!.await()
            val profile = if (EDCompanionApi.currentState == EDCompanionApi.State.AUTHORIZED) {
                onMain { profileSpinKit.visibility = View.VISIBLE }
                EDCompanionApi.getProfile()

            } else {
                Snackbar.make(
                    this@ProfileFragment.activity!!
                        .findViewById(android.R.id.content),
                    "Login to EDApi or EDSM",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Login") {
                    startActivity(
                        Intent(
                            this@ProfileFragment.activity,
                            SettingsActivity::class.java
                        )
                    )
                }.show()
                null
            }

            onMain {
                (activity as MainActivity).mainToolbar.title = "Profile"
                profileSpinKit.visibility = View.GONE

                rankLayout.setAnimateOnClickListener(
                    rankBars,
                    rankView,
                    { isRanksOpened }) { isRanksOpened = !isRanksOpened }

                profile?.let {
                    with(it) {
                        cmdrName.text = commander!!.name
                        credits.text = String.format(
                            resources.getString(R.string.credits),
                            NumberFormat.getIntegerInstance().format(commander!!.credits!!)
                        )
                        this@ProfileFragment.lastSystem.text = this.lastSystem!!.name
                        lastStation.text =
                            if (lastStarport!!.name != "") lastStarport!!.name else "-"
                        combatRank.text = commander!!.rank!!.combat!!.name
                        val combatRank = commander!!.rank!!.combat!!.ordinal
                        imageLoader.displayImage(combatRankImages[combatRank], combatImg)

                        tradeRank.text = commander!!.rank!!.trade!!.name
                        val tradeRank = commander!!.rank!!.trade!!.ordinal
                        imageLoader.displayImage(tradingRankImages[tradeRank], tradeImg)

                        explorRank.text = commander!!.rank!!.explore!!.name
                        val expRank = commander!!.rank!!.explore!!.ordinal
                        imageLoader.displayImage(explorationRankImages[expRank], expImg)

                        cqcRank.text = commander!!.rank!!.cqc!!.name
                        val cqcRank = commander!!.rank!!.cqc!!.ordinal
                        imageLoader.displayImage(cqcRankImages[cqcRank], cqcImg)

                        impNavyRank.text = commander!!.rank!!.empire!!.name
                        imageLoader.displayImage(empireIcon, impNavyImg)

                        fedNavyRank.text = commander!!.rank!!.federation!!.name
                        imageLoader.displayImage(federationIcon, fedNavyImg)

                        lastSystemLayout.setOnClickListener {
                            Navigation.findNavController(it).navigate(
                                R.id.action_profileFragment_to_systemFragment,
                                bundleOf(Pair("system", this.lastSystem!!.name))
                            )
                        }

                        if (lastStarport!!.name != "") {
                            val station: Station? = onIO {
                                EDSMApi.getStations(lastSystem!!.name!!).stations!!
                                    .firstOrNull { it.name == lastStarport!!.name }

                            }

                            station?.let {
                                lastStationLayout.setOnClickListener {
                                    Navigation.findNavController(it).navigate(
                                        R.id.action_profileFragment_to_stationFragment,
                                        bundleOf(Pair("station", station))
                                    )
                                }
                            }
                        }


                        creditsView.setOnClickListener {
                            Navigation.findNavController(it).navigate(
                                R.id.action_profileFragment_to_creditsFragment,
                                bundleOf(Pair("creds", commander!!.credits!!))
                            )
                        }
                    }
                }
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}
