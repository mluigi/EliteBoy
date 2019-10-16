package m.luigi.eliteboy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.sectionHeader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain

class MainActivity : AppCompatActivity() {

    var waitForInitApi: Deferred<Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        GlobalScope.launch {
            onIO {
                waitForInitApi = async {
                    EDCompanionApi.initApi(
                        getSharedPreferences(
                            "Main",
                            Context.MODE_PRIVATE
                        )
                    ) { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }
                }

            }
            onMain {
                drawer {
                    toolbar = mainToolbar
                    accountHeader {
                        profile {
                            name = "CMDR"
                        }
                    }

                    primaryItem("Profile") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.profileFragment)
                            false
                        }
                    }
                    primaryItem("News") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.newsFragment)
                            false
                        }
                    }
                    primaryItem("Settings") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.newsFragment)
                            false
                        }
                    }
                    sectionHeader("Search")
                    primaryItem("System") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.systemsFragment)
                            false
                        }
                    }
                    primaryItem("Station") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.stationsFragment)
                            false
                        }
                    }
                    primaryItem("Nearest") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.nearestFragment)
                            false
                        }
                    }
                }
            }

        }
    }


}
