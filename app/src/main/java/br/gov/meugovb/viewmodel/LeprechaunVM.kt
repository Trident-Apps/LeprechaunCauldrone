package br.gov.meugovb.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.gov.meugovb.ui.activities.dataStore
import br.gov.meugovb.utils.*
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.flow.first

class LeprechaunVM(app: Application) : AndroidViewModel(app) {

    private val urlBuilder = uriBuilder()
    private val onesignalTag = OneSignalTagSender()

    val urlLiveData: MutableLiveData<String> = MutableLiveData()


    fun getDeepLink(activity: Activity) {
        AppLinkData.fetchDeferredAppLinkData(activity.applicationContext) {
            val deeplink = it?.targetUri.toString()

            if (deeplink == "null") {
                getAppsFlyer(activity)
            } else {
                urlLiveData.postValue(urlBuilder.createUrl(deeplink, null, activity))
                onesignalTag.sendTag(deeplink, null)
            }
        }
    }

    private fun getAppsFlyer(activity: Activity) {
        AppsFlyerLib.getInstance().init(
            Consts.APPS_DEV_KEY, object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    onesignalTag.sendTag("null", data)
                    urlLiveData.postValue(urlBuilder.createUrl("null", data, activity))
                }

                override fun onConversionDataFail(p0: String?) {

                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    TODO("Not yet implemented")
                }

                override fun onAttributionFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

            }, activity
        )
        AppsFlyerLib.getInstance().start(activity)
    }

    suspend fun checkDataStoreValue(key: String, context: Context): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveUrl(url: String, context: Context) {
        val dataStoreKey = stringPreferencesKey("finalUrl")
        context.dataStore.edit { sharedPref ->
            sharedPref[dataStoreKey] = url
        }
    }

}