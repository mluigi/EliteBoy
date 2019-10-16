package m.luigi.eliteboy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.onMain

class AuthActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent.action
        val data = intent.data
        val code = data.getQueryParameter("code")

        GlobalScope.launch {
            onIO {
                EDCompanionApi.tokenCallback(code)
                onMain{startActivity(Intent(this@AuthActivity,MainActivity::class.java))}
            }
        }

    }
}