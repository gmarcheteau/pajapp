package com.bigotapps.pajapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class SettingsActivity extends Activity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
  
	Long topScore;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        
        mPlusClient = new PlusClient.Builder(this, this, this)
        .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        .build();
        
    
      //shared preferences, to store Best Scores and stuff
		SharedPreferences prefs = this.getSharedPreferences(
			      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		
		//retrieve topScore
		topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
    
    }

    @Override
    protected void onStart() {
        super.onStart();
       // mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (SendIntentException e) {
                mPlusClient.connect();
            }
        }
        // Enregistrer le résultat et résoudre l'échec de connexion lorsque l'utilisateur clique.
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    //@Override
    public void onConnected(Bundle b) {
        String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
    public void onClick(View view){
    	Toast.makeText(this, "Dummy - Not sure this button is necessary in Settings", Toast.LENGTH_LONG).show();
    
    	/*
    	if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
    		mPlusClient.connect(); //added because otherwise would only show the progress dialog
    	

    		if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    // Nouvelle tentative de connexion
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }*/
        
    	//if already connected
    	if (view.getId() == R.id.sign_in_button && mPlusClient.isConnected()) {
    		String accountName = mPlusClient.getAccountName();
    		Toast.makeText(this, accountName + " is already connected.", Toast.LENGTH_LONG).show();
        }
    	
    
    }

}
