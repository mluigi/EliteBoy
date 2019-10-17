package m.luigi.eliteboy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.util.info
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain

class SettingsActivity : AppCompatActivity() {

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
            GlobalScope.launch {
                code?.let {
                    onIO {
                        EDCompanionApi.tokenCallback(code)
                        onMain {
                            startActivity(
                                Intent(
                                    this@SettingsActivity,
                                    MainActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val login = findPreference<Preference>("loginEDApi")
            info { login!!.title.toString() }
            if (EDCompanionApi.currentState==EDCompanionApi.State.LOGGED_OUT) {
                login!!.setOnPreferenceClickListener {
                    EDCompanionApi.login()

                    true
                }
            } else{
                login!!.title = "Logged in"
                login.summary = ""
            }
        }
    }
}