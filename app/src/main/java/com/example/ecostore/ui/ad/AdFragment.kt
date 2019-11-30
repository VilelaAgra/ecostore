package com.example.ecostore.ui.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecostore.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.*


class AdFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ad, container, false)
        val testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(activity, "ca-app-pub-3940256099942544/6300978111" )
        val mAdView = root.findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return root
    }

}