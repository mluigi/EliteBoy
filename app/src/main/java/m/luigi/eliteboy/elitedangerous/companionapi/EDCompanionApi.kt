package m.luigi.eliteboy.elitedangerous.companionapi

import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import m.luigi.eliteboy.BuildConfig.FrontierClientId
import m.luigi.eliteboy.elitedangerous.companionapi.data.*
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.CommodityDeserializer
import m.luigi.eliteboy.elitedangerous.companionapi.data.deserializers.StarportDeserializer
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime


object EDCompanionApi {

    const val LIVE_SERVER = "https://companion.orerve.net"
    private const val AUTH_SERVER = "https://auth.frontierstore.net"
    private const val CALLBACK_URL = "eliteboy://fd-auth-redirect"
    private const val AUTH_URL = "/auth"
    const val DECODE_URL = "/decode"
    private const val TOKEN_URL = "/token"
    private const val AUDIENCE = "audience=steam,frontier"
    private const val SCOPE = "scope=capi"
    const val PROFILE_URL = "/profile"
    const val MARKET_URL = "/market"
    const val SHIPYARD_URL = "/shipyard"

    private lateinit var sharedPrefs: SharedPreferences

    var currentState = State.LOGGED_OUT
    private var credentials = Credentials()
    private val clientID = FrontierClientId
    private var authSessionId = ""
    private var verifier = ""

    private var authCallback: (String) -> Unit = {}

    private var cachedProfile: Profile? = null

    private var profileLastUpdate = LocalDateTime.now()

    enum class Endpoint(val url: String) {
        PROFILE("$LIVE_SERVER$PROFILE_URL"),
        MARKET("$LIVE_SERVER$MARKET_URL"),
        SHIPYARD("$LIVE_SERVER$SHIPYARD_URL"),
    }

    enum class State {
        LOGGED_OUT,
        AWAITING_CALLBACK,
        AUTHORIZED
    }


    fun initApi(
        sharedPreferences: SharedPreferences,
        authcallback: (String) -> Unit
    ) {
        setAuthCallback(authcallback)
        sharedPrefs = sharedPreferences
        val credentialsJson = sharedPrefs.getString("creds", null)
        credentialsJson?.let {
            credentials = Credentials.fromJson(credentialsJson)
            try {
                refreshToken()
            } catch (e: java.lang.Exception) {
                currentState = State.LOGGED_OUT
            }
        }
    }

    private fun saveCreds() {
        sharedPrefs.edit().putString("creds", credentials.toJson()).apply()
    }

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Commodity::class.java, CommodityDeserializer())
        .registerTypeAdapter(Starport::class.java, StarportDeserializer())
        .create()!!

    fun login() {
        check(currentState == State.LOGGED_OUT) { "Wrong state" }

        currentState = State.AWAITING_CALLBACK
        val codeChallenge = createCodeChallenge()
        val url =
            "$AUTH_SERVER$AUTH_URL?response_type=code" +
                    "&$AUDIENCE&$SCOPE" +
                    "&client_id=$clientID" +
                    "&code_challenge=${codeChallenge}" +
                    "&code_challenge_method=S256" +
                    "&state=$authSessionId" +
                    "&redirect_uri=${Uri.encode(CALLBACK_URL)}"
        authCallback(url)
    }

    private fun createCodeChallenge(): String {
        val sr = SecureRandom()
        val code = ByteArray(32)
        sr.nextBytes(code)
        verifier =
            base64UrlEncode(code)
        val bytes = verifier.toByteArray(charset("US-ASCII"))

        val rawAuthSessionId = ByteArray(8)
        sr.nextBytes(rawAuthSessionId)
        authSessionId = base64UrlEncode(rawAuthSessionId)

        val md = MessageDigest.getInstance("SHA-256")
        md.update(bytes, 0, bytes.size)
        val digest = md.digest()
        return base64UrlEncode(digest)
    }

    private fun base64UrlEncode(blob: ByteArray): String {
        val base64 = encodeToString(blob, URL_SAFE or NO_WRAP or NO_PADDING)
        return base64.replace('+', '-').replace('/', '_').replace("=", "")
    }

    fun tokenCallback(code: String) {
        val urlConnection = getRequest("$AUTH_SERVER$TOKEN_URL")
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        urlConnection.doOutput = true
        urlConnection.requestMethod = "POST"
        val body =
            "grant_type=authorization_code&client_id=${clientID}&code_verifier=${verifier}&code=${code}&redirect_uri=${URLEncoder.encode(
                CALLBACK_URL, "UTF-8"
            )}"

        DataOutputStream(urlConnection.outputStream).writeBytes(body)

        getResponse(urlConnection)
        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            val responseData = getResponseData(urlConnection)
            val jsonObject = JsonParser.parseString(responseData)
            credentials.accessToken = jsonObject.asJsonObject["access_token"].asString
            credentials.refreshToken = jsonObject.asJsonObject["refresh_token"].asString
            credentials.expiryDate =
                LocalDateTime.now().plusSeconds(jsonObject.asJsonObject["expires_in"].asLong)

            currentState = State.AUTHORIZED
            saveCreds()
        } else {
            currentState = State.LOGGED_OUT
        }
    }

    fun refreshToken() {
        currentState = State.AWAITING_CALLBACK
        val urlConnection = getRequest("$AUTH_SERVER$TOKEN_URL")
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        urlConnection.doOutput = true
        val body =
            "grant_type=refresh_token&client_id=${clientID}&refresh_token=${credentials.refreshToken}"
        DataOutputStream(urlConnection.outputStream).writeBytes(body)
        getResponse(urlConnection)
        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            val responseData = getResponseData(urlConnection)
            val jsonObject = JsonParser.parseString(responseData)
            credentials.accessToken = jsonObject.asJsonObject["access_token"].asString
            credentials.refreshToken = jsonObject.asJsonObject["refresh_token"].asString
            credentials.expiryDate =
                LocalDateTime.now().plusSeconds(jsonObject.asJsonObject["expires_in"].asLong)

            currentState = State.AUTHORIZED
            saveCreds()
        } else {
            currentState = State.LOGGED_OUT
        }
    }

    fun getProfile(forced: Boolean = false): Profile? {

        if ((cachedProfile == null ||
                    forced ||
                    LocalDateTime.now() > profileLastUpdate.plusMinutes(5)) &&
            currentState == State.AUTHORIZED
        ) {
            val url = Endpoint.PROFILE.url
            val connection = getRequest(url)
            connection.doInput = true
            getResponse(connection)
            val json = getResponseData(connection)
            cachedProfile = gson.fromJson(json, Profile::class.java)
        }

        return cachedProfile
    }

    fun getLastPosition(forced: Boolean = false): String {
        return getProfile(forced)?.lastSystem?.name ?: "Sol"
    }

    fun getMarket(): Market? {
        val url = Endpoint.MARKET.url
        val connection = getRequest(url)
        connection.doInput = true
        getResponse(connection)
        val json = getResponseData(connection)
        return gson.fromJson(json, Market::class.java)
    }

    fun getShipyard(): Shipyard? {
        val url = Endpoint.SHIPYARD.url
        val connection = getRequest(url)
        connection.doInput = true
        getResponse(connection)
        val json = getResponseData(connection)
        return gson.fromJson(json, Shipyard::class.java)
    }


    private fun setAuthCallback(callback: (String) -> Unit) {
        authCallback = callback
    }

    private fun getResponse(httpURLConnection: HttpURLConnection) {
        httpURLConnection.connect()
    }

    private fun getResponseData(connection: HttpURLConnection): String {
        return connection.inputStream.bufferedReader().readText()
    }

    private fun getRequest(url: String): HttpURLConnection {
        val request = URL(url).openConnection() as HttpURLConnection
        request.instanceFollowRedirects = true
        request.connectTimeout = 10000
        request.readTimeout = 10000
        request.setRequestProperty(
            "User-Agent",
            "EliteBoy"
        )

        if (currentState == State.AUTHORIZED) {
            request.setRequestProperty("Authorization", "Bearer ${credentials.accessToken}")
        }

        return request
    }
}