package m.luigi.eliteboy


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.companionapi.data.Profile
import m.luigi.eliteboy.elitedangerous.edsm.data.Station
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.setAnimateOnClickListener

class ProfileFragment : Fragment() {

    private lateinit var imageLoader: ImageLoader
    private var initJob: Job = Job()

    private var isRanksOpened = false
    private var profile: Profile? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initJob = GlobalScope.launch {
            (activity as MainActivity).initjob.join()
            imageLoader = (activity as MainActivity).imageLoader
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GlobalScope.launch {
            initJob.join()
            updateProfile()

            onMain {
                (activity as MainActivity).mainToolbar.title = "Profile"
                rankLayout.setAnimateOnClickListener(
                    rankBars,
                    rankView,
                    { isRanksOpened }) { isRanksOpened = !isRanksOpened }

                swipeRefresh.setColorSchemeResources(
                    R.color.md_amber_800
                )

                swipeRefresh.setOnRefreshListener {
                    GlobalScope.launch {
                        swipeRefresh.isRefreshing = false
                        updateProfile(true)
                        setProfileLayout()
                    }
                }
            }

            setProfileLayout()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    private suspend fun updateProfile(forced: Boolean = false) {
        onIO {
            profile = if (EDCompanionApi.currentState == EDCompanionApi.State.AUTHORIZED) {
                onMain { profileSpinKit.visibility = View.VISIBLE }
                EDCompanionApi.getProfile(forced)
            } else {
                Snackbar.make(
                    this@ProfileFragment.activity!!
                        .findViewById(android.R.id.content),
                    "Login to EDApi or EDSM",
                    Snackbar.LENGTH_LONG
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
        }
    }

    private suspend fun setProfileLayout() {
        onMain {
            profile?.let {
                with(it) {
                    cmdrName.text = commander!!.name
                    credits.text = kotlin.String.format(
                        resources.getString(m.luigi.eliteboy.R.string.credits),
                        android.icu.text.NumberFormat.getIntegerInstance().format(commander!!.credits!!)
                    )
                    this@ProfileFragment.lastSystem.text = this.lastSystem!!.name
                    lastStation.text =
                        if (lastStarport!!.name != "") lastStarport!!.name else "-"
                    combatRank.text = commander!!.rank!!.combat!!.name
                    val combatRank = commander!!.rank!!.combat!!.ordinal
                    imageLoader.displayImage(
                        m.luigi.eliteboy.util.combatRankImages[combatRank],
                        combatImg
                    )

                    tradeRank.text = commander!!.rank!!.trade!!.name
                    val tradeRank = commander!!.rank!!.trade!!.ordinal
                    imageLoader.displayImage(
                        m.luigi.eliteboy.util.tradingRankImages[tradeRank],
                        tradeImg
                    )

                    explorRank.text = commander!!.rank!!.explore!!.name
                    val expRank = commander!!.rank!!.explore!!.ordinal
                    imageLoader.displayImage(
                        m.luigi.eliteboy.util.explorationRankImages[expRank],
                        expImg
                    )

                    cqcRank.text = commander!!.rank!!.cqc!!.name
                    val cqcRank = commander!!.rank!!.cqc!!.ordinal
                    imageLoader.displayImage(m.luigi.eliteboy.util.cqcRankImages[cqcRank], cqcImg)

                    impNavyRank.text = commander!!.rank!!.empire!!.name
                    imageLoader.displayImage(m.luigi.eliteboy.util.empireIcon, impNavyImg)

                    fedNavyRank.text = commander!!.rank!!.federation!!.name
                    imageLoader.displayImage(m.luigi.eliteboy.util.federationIcon, fedNavyImg)

                    lastSystemLayout.setOnClickListener {
                        androidx.navigation.Navigation.findNavController(it).navigate(
                            m.luigi.eliteboy.R.id.action_profileFragment_to_systemFragment,
                            androidx.core.os.bundleOf(
                                kotlin.Pair(
                                    "system",
                                    this.lastSystem!!.name
                                )
                            )
                        )
                    }

                    if (lastStarport!!.name != "") {
                        val station: Station? = m.luigi.eliteboy.util.onIO {
                            m.luigi.eliteboy.elitedangerous.edsm.EDSMApi.getStations(lastSystem!!.name!!).stations!!
                                .firstOrNull { it.name == lastStarport!!.name }

                        }

                        station?.let {
                            lastStationLayout.setOnClickListener {
                                androidx.navigation.Navigation.findNavController(it).navigate(
                                    m.luigi.eliteboy.R.id.action_profileFragment_to_stationFragment,
                                    androidx.core.os.bundleOf(kotlin.Pair("station", station))
                                )
                            }
                        }
                    }

                    creditsView.setOnClickListener {
                        androidx.navigation.Navigation.findNavController(it).navigate(
                            m.luigi.eliteboy.R.id.action_profileFragment_to_creditsFragment,
                            androidx.core.os.bundleOf(
                                kotlin.Pair(
                                    "creds",
                                    commander!!.credits!!
                                )
                            )
                        )
                    }
                }
            }
            profileSpinKit.visibility = View.GONE
        }
    }
}
