package m.luigi.eliteboy


import android.annotation.TargetApi
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.companionapi.data.Profile
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.snackBarMessage

class ProfileFragment : Fragment() {
    lateinit var profileDeferred: Deferred<Profile?>
    private var profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            profileDeferred = async {
                (activity as MainActivity).waitForInitApi!!.await()
                onIO { EDCompanionApi.getProfile() }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GlobalScope.launch {
            profile = profileDeferred.await()

            onMain {
                profile?.let {
                    with(it) {
                        cmdrName.text = commander!!.name
                        credits.text = String.format(
                            resources.getString(R.string.credits),
                            NumberFormat.getIntegerInstance().format(commander!!.credits!!)
                        )
                        this@ProfileFragment.lastSystem.text = this.lastSystem!!.name
                        lastStation.text = lastStarport!!.name
                        combatRank.text = commander!!.rank!!.combat!!.name

                        val combatrank = commander!!.rank!!.combat!!.ordinal
                        combatRankProgressBar.progress = combatrank
                        combatRankPercentage.text =
                            String.format(resources.getString(R.string.rank), combatrank)

                        tradeRank.text = commander!!.rank!!.trade!!.name
                        val tradeRank = commander!!.rank!!.trade!!.ordinal
                        tradeRankProgressBar.progress = tradeRank
                        tradeRankPercentage.text =
                            String.format(resources.getString(R.string.rank), tradeRank)

                        explorRank.text = commander!!.rank!!.explore!!.name
                        val expRank = commander!!.rank!!.explore!!.ordinal
                        expRankBar.progress = expRank
                        explorRankPercentage.text =
                            String.format(resources.getString(R.string.rank), expRank)

                        cqcRank.text = commander!!.rank!!.cqc!!.name
                        val cqcrank = commander!!.rank!!.cqc!!.ordinal
                        cqcProgressBar.progress = cqcrank
                        cqcRankPercentage.text =
                            String.format(resources.getString(R.string.rank), cqcrank)

                        impNavyRank.text = commander!!.rank!!.empire!!.name
                        val impRank = commander!!.rank!!.empire!!.ordinal
                        impProgressBar.progress = impRank
                        impNavyRankPercentage.text =
                            String.format(resources.getString(R.string.rank), impRank)

                        fedNavyRank.text = commander!!.rank!!.federation!!.name
                        val fedRank = commander!!.rank!!.federation!!.ordinal
                        fedProgressBar.progress = fedRank
                        fedNavyRankPercentage.text =
                            String.format(resources.getString(R.string.rank), fedRank)
                    }
                } ?: kotlin.run {
                    snackBarMessage { "Not Logged In" }
                }
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


}
