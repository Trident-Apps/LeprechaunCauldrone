package br.gov.meugovb.utils

import android.app.Activity
import android.content.Context
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.sql.Time
import java.util.*

class uriBuilder {

    fun createUrl(deepLink: String, data: MutableMap<String, Any>?, activity: Activity): String {
        val gadid =
            AdvertisingIdClient.getAdvertisingIdInfo(activity.applicationContext).id.toString()

        val url = Consts.BASE_URL.toUri().buildUpon().apply {
            appendQueryParameter(Consts.SECURE_GET_PARAMETR, "secure_key")
            appendQueryParameter(Consts.DEV_TMZ_KEY, TimeZone.getDefault().id)
            appendQueryParameter(Consts.GADID_KEY, gadid)
            appendQueryParameter(Consts.DEEPLINK_KEY, deepLink)
            appendQueryParameter(Consts.SOURCE_KEY, data?.get("media_source").toString())
            appendQueryParameter(
                Consts.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
            )
            appendQueryParameter(Consts.ADSET_ID_KEY, data?.get("adset_id").toString())
            appendQueryParameter(Consts.CAMPAIGN_ID_KEY, data?.get("campaign_id").toString())
            appendQueryParameter(Consts.APP_COMPAIGN_KEY, data?.get("campaign").toString())
            appendQueryParameter(Consts.ADSET_KEY, data?.get("adset").toString())
            appendQueryParameter(Consts.ADGROUP_KEY, data?.get("adgroup").toString())
            appendQueryParameter(Consts.ORIG_COST_KEY, data?.get("orig_cost").toString())
            appendQueryParameter(Consts.AF_SITEID_KEY, data?.get("af_siteid").toString())

        }.toString()
        return url
    }

}