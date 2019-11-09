package m.luigi.eliteboy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.sectionHeader
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.utils.StorageUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.util.CoriolisDataHelper
import m.luigi.eliteboy.util.modulesList
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain

class MainActivity : AppCompatActivity() {

    lateinit var imageLoader: ImageLoader
    var initjob: Job = Job()

    init {
        initjob = GlobalScope.launch {
            onIO {
                //initialize modulesList here because it would slow SearchStationFragment
                modulesList
                CoriolisDataHelper.init(assets)
                val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                EDSMApi.commander = prefs.getString("pref_edsm_cmdr", "")!!
                EDSMApi.apiKey = prefs.getString("pref_edsm_api_key", "")!!

                EDCompanionApi.initApi(
                    getSharedPreferences(
                        "Main",
                        Context.MODE_PRIVATE
                    )
                ) { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }

                imageLoader = ImageLoader.getInstance().apply {
                    init(
                        ImageLoaderConfiguration.Builder(this@MainActivity)
                            .memoryCache(LruMemoryCache(2 * 1024 * 1024))
                            .diskCache(UnlimitedDiskCache(StorageUtils.getCacheDirectory(this@MainActivity)))
                            .defaultDisplayImageOptions(
                                DisplayImageOptions.Builder()
                                    .showImageOnFail(R.drawable.ic_error_black_24dp)
                                    .showImageForEmptyUri(R.drawable.ic_error_black_24dp)
                                    .cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .build()
                            )
                            .build()
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        GlobalScope.launch {
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

                    sectionHeader("Search")
                    primaryItem("System") {
                        onClick { _ ->
                            Navigation.findNavController(this@MainActivity, R.id.navHost)
                                .navigate(R.id.searchSystemsFragment)
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

                    footer {
                        primaryItem("") {
                            this.icon = R.drawable.ic_settings_black_24dp
                            onClick { _ ->
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        SettingsActivity::class.java
                                    )
                                )
                                false
                            }
                        }
                    }


                }
            }
        }
    }
}
