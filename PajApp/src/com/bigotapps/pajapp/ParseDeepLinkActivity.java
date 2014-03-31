package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.plus.PlusShare;

public class ParseDeepLinkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test deep-link from G+
        String deepLinkId = PlusShare.getDeepLinkId(this.getIntent());
        Intent target = parseDeepLinkId(deepLinkId);
        if (target != null) {
          startActivity(target);
        }
        else
        {
        	// Check for an incoming FB deep link
            Uri targetUri = this.getIntent().getData();
            if (targetUri != null) {
                Log.i("DEEP", "Incoming deep link: " + targetUri);
            }
        }

        finish();
    }

    /**
     * Get the intent for an activity corresponding to the deep-link ID.
     *
     * @param deepLinkId The deep-link ID to parse.
     * @return The intent corresponding to the deep-link ID.
     */
    private Intent parseDeepLinkId(String deepLinkId) {
        Intent route = new Intent();
        if ("pajaBack".equals(deepLinkId)) {
            route.setClass(getApplicationContext(), MainActivity.class);
            route.putExtra("message", "Paja back to your friend");
            Log.i("DEEP", "Incoming deep link: " + deepLinkId);
        } else {
            // Fallback to the MainActivity in your app.
            route.setClass(getApplicationContext(), MainActivity.class);
            route.putExtra("message", "Not sure where you came from");
            Log.i("DEEP", "Incoming deep link: G+ not pajapp");
        }
        return route;
    }
    
   
    
    
}