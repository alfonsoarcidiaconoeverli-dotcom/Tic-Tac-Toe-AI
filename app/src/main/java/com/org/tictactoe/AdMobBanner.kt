package com.org.tictactoe

import android.app.Activity
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // ✅ crea AdView UNA SOLA VOLTA (evita bug “oggi non si vede”)
    val adView = remember {
        AdView(context).apply {
            adUnitId = "ca-app-pub-6445825465110921/1542457939"
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            // ✅ dimensione adattiva (migliore di AdSize.BANNER)
            adView.setAdSize(getAdaptiveBannerSize(context as? Activity))
            adView.loadAd(AdRequest.Builder().build())
            adView
        },
        update = {
            // ✅ se per qualsiasi motivo non ha un ad caricato, riprova
            if (it.adSize == null) {
                it.setAdSize(getAdaptiveBannerSize(context as? Activity))
            }
        }
    )
}

private fun getAdaptiveBannerSize(activity: Activity?): AdSize {
    // fallback sicuro
    if (activity == null) return AdSize.BANNER

    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

    val density = displayMetrics.density
    val adWidthPixels = displayMetrics.widthPixels
    val adWidth = (adWidthPixels / density).toInt()

    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
}
