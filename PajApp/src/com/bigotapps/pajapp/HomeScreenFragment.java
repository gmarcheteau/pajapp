package com.bigotapps.pajapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.*;

public class HomeScreenFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, 
		ViewGroup container, 
	    Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_credits, container, false);
	    
	    //ad initialization
        AdView adView = (AdView) view.findViewById(R.id.ad);
             
        try{
        	AdRequest adRequest = new AdRequest.Builder()
	            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            .addTestDevice("BF37ACEAC7B5CA1DD5881A71B6D1E155")
	            .build();;
            //adView.setAdSize(AdSize.SMART_BANNER);
            //adView.setAdUnitId(getString(R.string.admob_home_id));
            //adView.setAdUnitId("ca-app-pub-6874112603029385/4636328955");
            adView.loadAd(adRequest);
        }
        catch(Exception e){
        	Log.i("Ads_greg",e.getMessage());
        }
	    
	    return view;
	}	
}
