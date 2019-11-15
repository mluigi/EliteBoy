package m.luigi.eliteboy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain
import m.luigi.eliteboy.util.runWhenOnline
import m.luigi.eliteboy.util.snackBarMessage

class SettingsActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val action = intent.action
        val data = intent.data
        val code = data?.getQueryParameter("code")

        if (action == Intent.ACTION_VIEW) {
            launch {
                code?.let {
                    runWhenOnline {
                        try {
                            onIO {
                                EDCompanionApi.tokenCallback(code)
                            }
                            onMain {
                                startActivity(
                                    Intent(
                                        this@SettingsActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        CoroutineScope by CoroutineScope(Dispatchers.Main) {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val prefs = PreferenceManager.getDefaultSharedPreferences(this.context)
            val loginEDAPI = findPreference<Preference>("loginEDApi")
            val loginEDSM = findPreference<Preference>("loginEDSM")

            launch {
                when (EDCompanionApi.currentState) {
                    EDCompanionApi.State.LOGGED_OUT -> {
                        loginEDAPI!!.setOnPreferenceClickListener {
                            this@SettingsFragment.launch {
                                try {
                                    loginEDAPI.title = "Awaiting callback"
                                    loginEDAPI.summary = "Click to try again"
                                    EDCompanionApi.currentState = EDCompanionApi.State.LOGGED_OUT
                                    onIO {
                                        EDCompanionApi.login()
                                    }
                                } catch (e: IllegalStateException) {
                                    snackBarMessage { "Couldn't login. Try Again." }
                                }
                            }

                            true
                        }
                    }
                    EDCompanionApi.State.AUTHORIZED -> {
                        loginEDAPI!!.title = "Logged in"
                        loginEDAPI.summary = ""
                    }
                    EDCompanionApi.State.AWAITING_CALLBACK -> {
                        loginEDAPI!!.title = "Awaiting callback"
                        loginEDAPI.summary = "Click to try again"
                        loginEDAPI.setOnPreferenceClickListener {
                            try {
                                EDCompanionApi.currentState = EDCompanionApi.State.LOGGED_OUT
                                EDCompanionApi.login()
                            } catch (e: IllegalStateException) {
                                snackBarMessage { "Couldn't login. Try Again." }
                            }

                            true
                        }
                    }
                }

                //Edsm login
                val loggedin = try {
                    onIO { EDSMApi.checkApiKey() }
                } catch (e: Exception) {
                    false
                }
                if (loggedin) {
                    onMain { loginEDSM!!.title = "Logged in" }
                } else {
                    loginEDSM!!.setOnPreferenceClickListener {
                        GlobalScope.launch {
                            val cmdrEDSM = prefs.getString("pref_edsm_cmdr", "")
                            val apikey = prefs.getString("pref_edsm_api_key", "")
                            when {
                                cmdrEDSM == "" -> onMain {
                                    snackBarMessage { "Insert CMDR name" }
                                }
                                apikey == "" -> onMain {
                                    snackBarMessage { "Insert API Key" }
                                }
                                else -> {
                                    EDSMApi.commander = cmdrEDSM!!
                                    EDSMApi.apiKey = apikey!!
                                    if (try {
                                            onIO { EDSMApi.checkApiKey() }
                                        } catch (e: Exception) {
                                            false
                                        }
                                    ) {
                                        onMain {
                                            snackBarMessage { "Successfully logged in" }
                                            loginEDSM.title = "Logged in"
                                        }
                                    } else {
                                        onMain {
                                            snackBarMessage { "Error logging in." }
                                        }
                                    }
                                }
                            }
                        }

                        true
                    }
                }
            }
        }
    }
}