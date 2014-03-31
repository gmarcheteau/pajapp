package com.bigotapps.pajapp;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;


//import android.app.DownloadManager.Request;
//import android.service.textservice.SpellCheckerService.Session;

public class SettingsActivity extends Activity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "SettingsActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
  
    private UiLifecycleHelper uiHelper;
    private StatusCallback callback;
    private GraphUser fbuser;
    private ImageButton fbButton;
    
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private boolean pendingPublishReauthorization = false;
    
    
	Long topScore;
	
	 //shared preferences, to store Best Scores and stuff
	SharedPreferences prefs;
	boolean FB_CONNECT = false;
	boolean GP_CONNECT = false;
	public String fbusername="default name";
	   
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        fbButton=(ImageButton) findViewById(R.id.fbButton);
        findViewById(R.id.GooglePlusSigninButton).setOnClickListener(this);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
     // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

          // callback when session changes state
          @Override
          public void call(Session session, SessionState state, Exception exception) {
        	
        	 SessionState currentstate =  session.getState();
        	 //Toast.makeText(getApplicationContext(), currentstate.toString(), Toast.LENGTH_SHORT).show();
        	  
        	if (session.isOpened()) {
            	// make request to the /me API
            	Request.newMeRequest(session, new Request.GraphUserCallback() {

            		  // callback after Graph API response with user object
            		  @Override
            		  public void onCompleted(GraphUser user, Response response) {
            		    if (user != null) {
            		      fbuser=user;
            		      fbusername=user.getName();
            		      TextView name = (TextView) findViewById(R.id.textViewFBname);
                          name.setText(fbusername);
            		      Toast.makeText(getApplicationContext(), fbusername, Toast.LENGTH_SHORT).show();
            		    }
            		  }
            		}).executeAsync();
            }
          }
        });
        
        
        
        
        mPlusClient = new PlusClient.Builder(this, this, this)
            .setScopes(Scopes.PLUS_LOGIN)
            .build();
        
     // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in, biatch...");
      
        prefs = getSharedPreferences("com.bigotapps.pajapp", Context.MODE_PRIVATE);
        FB_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.FBConnect", false);
		GP_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.gPlusConnect", false);
    
        Switch switchGPlus = (Switch) findViewById(R.id.switchGPlus);
        switchGPlus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	//actions done only if new
                	if(!GP_CONNECT){
                	gPlusConnect();
        			//save preferences
        	        prefs.edit().putBoolean("com.bigotapps.pajapp.gPlusConnect", true).commit();      	        
        	        //should go back to unChecked if user Cancels login
                	}
                } else {
                    // The toggle is disabled
                	Toast.makeText(getApplicationContext(), "G+ unchecked", Toast.LENGTH_SHORT).show();
                	gPlusDisconnect();
                	prefs.edit().putBoolean("com.bigotapps.pajapp.gPlusConnect", false).commit();
                	TextView gpn = (TextView) findViewById(R.id.textViewGPlusname);
                    gpn.setText("G+: disconnected");
                	
                }
            }
        });
        
        /*
        Switch switchFB = (Switch) findViewById(R.id.SwitchFB);
        switchFB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	//actions done only if new
                	if(!FB_CONNECT){
                	fbConnect();
                	//save preferences
        	        prefs.edit().putBoolean("com.bigotapps.pajapp.FBConnect", true).commit();
        	        }
                } else {
                    // The toggle is disabled
                	Toast.makeText(getApplicationContext(), "FB unchecked", Toast.LENGTH_SHORT).show();
                	//save preferences
        	        prefs.edit().putBoolean("com.bigotapps.pajapp.FBConnect", false).commit();
                }
            }
        });
        */
        if (GP_CONNECT){
        	switchGPlus.setChecked(true);
        }
        /*
        if (FB_CONNECT){
            switchFB.setChecked(true);
            }
        */
        
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.change_hand:
			//changeHands();
			return true ;
		case R.id.action_settings:
			//goSettings();
			return true ;
		case R.id.view_badges:
			Intent i = new Intent(getApplicationContext(),WallOfFame.class);
			startActivity(i);
			return true ;
		default:
			return false;
		}
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
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), fbusername, Toast.LENGTH_SHORT).show();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
       
       
        Session.getActiveSession().onActivityResult(this, requestCode, responseCode, data);
        
        uiHelper.onActivityResult(requestCode, responseCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }
    
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
            .onActivityResult(this, requestCode, resultCode, data);
    }
    */

    //@Override
    public void onConnected(Bundle b) {
        String accountName = mPlusClient.getAccountName();
        TextView gpn = (TextView) findViewById(R.id.textViewGPlusname);
        gpn.setText("G+: "+accountName);
        mConnectionProgressDialog.dismiss();
        //Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult result) {
      if (mConnectionProgressDialog.isShowing()) {
        // The user clicked the sign-in button already. Start to resolve
        // connection errors. Wait until onConnected() to dismiss the
        // connection dialog.
        if (result.hasResolution()) {
          try {
                   result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
           } catch (SendIntentException e) {
                   mPlusClient.connect();
           }
        }
      }
      // Save the result and resolve the connection failure upon a user click.
      mConnectionResult = result;
    }
    
    public void onClick(View view){
    	switch(view.getId()){
		case R.id.GooglePlusSigninButton:
			gPlusConnect();
			//save preferences
	        prefs.edit().putBoolean("com.bigotapps.pajapp.gPlusConnect", true).commit();
	        break;
		case R.id.fbButton:
			fbConnect();
			break;
		default:
			//
		}
    
    	
        
    	//if already connected
    	if (view.getId() == R.id.GooglePlusSigninButton && mPlusClient.isConnected()) {
    		String accountName = mPlusClient.getAccountName();
    		Toast.makeText(this, accountName + " is already connected.", Toast.LENGTH_LONG).show();
        }
    	
    	

    
    }

    
public void fbConnect(){
	//Toast.makeText(getApplicationContext(), "launching fbConnect", Toast.LENGTH_SHORT).show();
	// start Facebook Login
	//Toast.makeText(getApplicationContext(), fbusername, Toast.LENGTH_SHORT).show();
	Session session = Session.getActiveSession();

    if (session != null) {

        // Check for publish permissions
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            pendingPublishReauthorization = true;
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                    this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
            return;
        }
    }
	
    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
    .setLink("http://www.lemonde.fr")
    .setApplicationName("Fapp Beta")
    .build();

	
    uiHelper.trackPendingDialogCall(shareDialog.present());
		
}

public void gPlusConnect(){
	if(!isNetworkConnected()){
		Toast.makeText(getApplicationContext(), R.string.notConnected, Toast.LENGTH_SHORT).show();
	}
	else{	
	mPlusClient.connect();
	if(!mPlusClient.isConnected()){
		if (mConnectionResult == null) {
           //mConnectionProgressDialog.show();
        } else {
            try {
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (SendIntentException e) {
                // Try connecting again.
                mConnectionResult = null;
                mPlusClient.connect();
            }
        }

	}
	else Toast.makeText(this.getApplicationContext(), "G+ already connected", Toast.LENGTH_SHORT).show();
	
	}
}

public void gPlusDisconnect(){
	if (mPlusClient.isConnected()) {
        mPlusClient.clearDefaultAccount();
        mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
            @Override
            public void onAccessRevoked(ConnectionResult status) {
                // mPlusClient is now disconnected and access has been revoked.
                // Trigger app logic to comply with the developer policies
            }
         });
    }
}

private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    if (state.isOpened()) {
        fbButton.setVisibility(View.VISIBLE);
    } else if (state.isClosed()) {
        fbButton.setVisibility(View.INVISIBLE);
    }
}

private boolean isNetworkConnected() {
	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	  NetworkInfo ni = cm.getActiveNetworkInfo();
	  if (ni == null) {
	   // There are no active networks.
	   return false;
	  } else
	   return true;
	}
private boolean isSubsetOf(Collection<String> subset,
        Collection<String> superset) {
    for (String string : subset) {
        if (!superset.contains(string)) {
            return false;
        }
    }
    return true;
}



}