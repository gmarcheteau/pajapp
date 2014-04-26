	package com.bigotapps.pajapp;


import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;


public class PajaCompleted extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	public long[] pattern = {1000};


	public int score=0;
	public long topScore;
	public long topGolpes;
	public long topDuration;
	public boolean hasShared=false;
	public boolean hasSharedPref;
	public long duration=0;
	public int golpes=0;
	public int pain=0;
	Bundle pajametrics;
	
	public TextView scoreView;
	
	Integer badgeScoreTarget=100000;
	Integer badgeDurationTarget=20;
	boolean badgeShareTarget=false;
	Integer badgeGolpesTarget=200;
	public boolean wantedToShare=false;
	
    private static final String TAG = "GregBug";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    
    public ImageButton mShareButton; 
    public SharedPreferences prefs;
    private UiLifecycleHelper uiHelper;
    
    congratsFragment congratsFragment;
    pajaMetricsFragment pajaMetricsFragment;
    pajaCompleteShareFragment pajaCompleteShareFragment;

  
	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.i(TAG,"SESSION CHANGED??");
		// TODO Auto-generated method stub	
	}
	 
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            
            if(wantedToShare){
            	Log.i(TAG,"WANTS TO SHARE - FROM onActivityResult");
            	gplusShare();
            }
            mPlusClient.connect();
        }
    }
	
	/**
	 * LifeCycle
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.paja_complete);
		//get Pajameters	
		if(this.getIntent().getExtras() != null){
		//use paja parameters
			
			duration = Long.valueOf(getIntent().getExtras().getString("duration"));
			score = Integer.valueOf(getIntent().getExtras().getString("score"));
			golpes = Integer.valueOf(getIntent().getExtras().getString("golpes"));
			pain = Integer.valueOf(getIntent().getExtras().getString("pain"));
			
			//prepare bundle with pajametrics to pass to fragment
			pajametrics = new Bundle();
			pajametrics.putString("score", "Score: "+String.valueOf(score));
			pajametrics.putString("golpes", "Golpes: "+String.valueOf(golpes));
			pajametrics.putString("pain", "Painful golpes: "+String.valueOf(pain));
			
			//scoreView.setText(String.valueOf(score)+" pts");
			Log.i(TAG,"Paja score: "+ score +"\n Duration: " + duration + "s");
			
		}
		
		//define/retrieve the fragments
		if (savedInstanceState == null) {
	        // Add the fragments on initial activity setup
	    	congratsFragment = new congratsFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.congrats_frame, congratsFragment)
	        .commit();
	        
	        pajaMetricsFragment = new pajaMetricsFragment();
	        pajaMetricsFragment.setArguments(pajametrics);
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.pajametrics_frame, pajaMetricsFragment)
	        .commit();
	        
	        pajaCompleteShareFragment = new pajaCompleteShareFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.pajashare_frame, pajaCompleteShareFragment)
	        .commit();
	        
	    } else {
	        // Or set the fragments from restored state info
	    	congratsFragment = (congratsFragment) getSupportFragmentManager()
	    			.findFragmentById(R.id.congrats_frame);
	    	pajaMetricsFragment = (pajaMetricsFragment) getSupportFragmentManager()
	    	        .findFragmentById(R.id.pajametrics_frame);
	    	pajaCompleteShareFragment = (pajaCompleteShareFragment) getSupportFragmentManager()
	    	        .findFragmentById(R.id.pajashare_frame);
	    }
	
		 uiHelper = new UiLifecycleHelper(this, callback);
		 uiHelper.onCreate(savedInstanceState);
		 wantedToShare=false;
	  
		//BUILD GPLUS CLIENT
		 mPlusClient = new PlusClient.Builder(this, this, this)
         .setScopes(Scopes.PLUS_LOGIN)  // Space separated list of scopes
         .build();
		 // Progress bar to be displayed if the connection failure is not resolved.
	      mConnectionProgressDialog = new ProgressDialog(this);
	      mConnectionProgressDialog.setMessage(getString(R.string.gplus_connect));
		 
		 mShareButton = (ImageButton) findViewById(R.id.gPlusShareButton);
		 
		// Get instance of Vibrator from current Context
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 
		//shared preferences, to store Best Scores and stuff
			prefs = this.getSharedPreferences("com.bigotapps.pajapp", Context.MODE_PRIVATE);
			
		//update Preferences
		fetchPrefs(prefs);
		updatePrefs(prefs); //incl badges
		
		boolean FB_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.FB_Connect", false);
		boolean GP_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.gPlusConnect", false);
		
		if(GP_CONNECT){
			//gPlusConnect();
		}
	
		// Vibrate according to pattern (-1 means don't repeat)
				v.vibrate(pattern,-1);
				playSound();
		
	 
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
			Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
			startActivity(i);
			return true ;
		case R.id.view_badges:
			Intent j = new Intent(getApplicationContext(),WallOfFame.class);
			startActivity(j);
			return true ;
		default:
			return false;
		}
	}
	@Override
	protected void onStop() {
	    super.onStop();
	    mPlusClient.disconnect();
	    //mShareButton.setEnabled(false);
	    
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    mPlusClient.disconnect();
	    //mShareButton.setEnabled(false);
	    
	}
	@Override
	protected void onResume(){
		super.onResume();
		//shared preferences, to store Best Scores and stuff
		SharedPreferences prefs = this.getSharedPreferences(
			      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		fetchPrefs(prefs);
		updatePrefs(prefs);
		
		boolean FB_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.FB_Connect", false);
		boolean GP_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.gPlusConnect", false);
		
		if(GP_CONNECT){
			//gPlusConnect();
		}
		
	}
	public void onClick(View view){
		 switch(view.getId()){
			case R.id.gPlusShareButton:
				gplusShare();
				break;
			case R.id.WhatsappButton:
				WhatsAppShare();
				break;
			case R.id.FBShareButton:
				fbShare();
				break;
	 }
}
	public void playSound(){
			MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.open);
			mp.start();
		}
	
	/**

	 * Navigation
	 */
	public void newPaja(){
    	Intent i = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(i);
    }
	public void WoF(){
	 	Intent i = new Intent(getApplicationContext(),WallOfFame.class);
	 	i.putExtra("score", Long.toString(score));
	 	i.putExtra("duration", duration);
	 	//quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
	 	startActivity(i);
    }
	
	/**
	 * Preference Management
	 */
	public void updatePrefs(SharedPreferences prefs){
			prefs.edit().putLong("com.bigotapps.pajapp.topscore",Math.max(score, topScore)).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topduration",Math.max(duration, topDuration)).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topgolpes",Math.max(golpes, topGolpes)).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.hasshared",hasShared||hasSharedPref).commit();
			fetchPrefs(prefs); //need to re-fetch in order to update local values tested in updateBadges 
			updateBadges(prefs);
		}
	public void fetchPrefs(SharedPreferences prefs){
		topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
		topGolpes=prefs.getLong("com.bigotapps.pajapp.topgolpes", 0);
		topDuration=prefs.getLong("com.bigotapps.pajapp.topduration", 0);
		hasSharedPref=prefs.getBoolean("com.bigotapps.pajapp.hasshared", false);
	}
	
	
	public void updateBadges(SharedPreferences prefs){
		//do stuff here
		if (topScore>badgeScoreTarget){
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeScore_unlocked", true).commit();
			//should do something funkier than a toast
			Toast.makeText(this.getApplicationContext(), "new badge: duration", Toast.LENGTH_SHORT).show();
		}
		if (topDuration>badgeDurationTarget){
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeDuration_unlocked", true).commit();
			//should do something funkier than a toast
			Toast.makeText(this.getApplicationContext(), "new badge: duration", Toast.LENGTH_SHORT).show();
		}
		if (topGolpes>badgeGolpesTarget){
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeGolpes_unlocked", true).commit();
			//should do something funkier than a toast
			Toast.makeText(this.getApplicationContext(), "new badge: golpes", Toast.LENGTH_SHORT).show();
		}
		if (hasShared&!hasSharedPref){
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", true).commit();
			//should do something funkier than a toast
			Toast.makeText(this.getApplicationContext(), "new badge: share", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Facebook
	 */
	public void fbShare(){
		
		OpenGraphObject fapp = OpenGraphObject.Factory.createForPost("pajappbeta:fapp");
		fapp.setType("fapp");
		fapp.setTitle("Fapp");
		fapp.getData().setProperty("duration", String.valueOf(duration));
		fapp.getData().setProperty("score", String.valueOf(score));
		
		String url="http://www.nolandalla.com/wp-content/uploads/2014/01/clenched-fist.jpg";
		fapp.setImageUrls(Arrays.asList(url));
		
		OpenGraphAction action = OpenGraphAction.Factory.createForPost("pajappbeta:perform");
		action.setProperty("fapp", fapp);

		FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(this,action,"fapp")
		        .build();
		uiHelper.trackPendingDialogCall(shareDialog.present());
		
		}
	private Session.StatusCallback callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};

	/**
	 * Google Plus
	 */
	public void gPlusConnect(){
		if(!isNetworkConnected()){
			Toast.makeText(getApplicationContext(), R.string.notConnected, Toast.LENGTH_SHORT).show();
		}
		else{	
		mPlusClient.connect();
		
		if (!mPlusClient.isConnected()) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
            } else {
                try {
                       mConnectionResult.startResolutionForResult(this,REQUEST_CODE_RESOLVE_ERR);
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
	public void gplusShare() {	 
		
		 if (!mPlusClient.isConnected()) {
			 Log.w("GregBug", "GPlus not connected. Calling gPlusConnect().");
			 //boolean to call gplusShare when connected (if called here will cause thread issues)
			 wantedToShare=true;
			 prefs.edit().putBoolean("com.bigotapps.pajapp.gPlusConnect", true).commit();
			 gPlusConnect();
			 
		 }
		 else{
			 final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			 //if G+ installed
			 if (errorCode == ConnectionResult.SUCCESS) {
		    	  hasShared=true;
		    	  prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", true).commit();
		    	     	  
		    	 PlusShare.Builder builder = new PlusShare.Builder(this, mPlusClient);	             
		         builder.addCallToAction("CHALLENGE",Uri.parse("https://plus.google.com/u/0/106383952060977394931"),"pajaBack");
	
		         builder.setContentUrl(Uri.parse("https://plus.google.com/u/0/106383952060977394931"));
		         
		         // Set the target deep-link ID (for mobile use).
		         builder.setContentDeepLinkId("pajaBack",
		                 null, null, null);
		        
		          // Set the share text.
		         String messageGPlusPost = getString((R.string.messageGPlus)) + "\n" + String.valueOf(score) + " pts,"+" "+String.valueOf(duration)+"s"; 
		          builder.setText(messageGPlusPost);
	
		          startActivityForResult(builder.getIntent(), 0);
	   
		      } else {
		          // Prompt the user to install the Google+ app.
		    	  GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
		      }
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
    @Override
	public void onConnected(Bundle b) {
    	mConnectionProgressDialog.dismiss();
        String accountName = mPlusClient.getAccountName();
        Log.w(TAG,accountName + " is connected.");
        mShareButton.setEnabled(true); 
        if(wantedToShare){
			Log.i(TAG,"Wants to share, calling gplusShare again.");
			gplusShare();
		}
        
    }
    @Override
    public void onDisconnected() {
	    	Log.d(TAG, "G+ disconnected");
	    	mShareButton.setEnabled(false);
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
    
    /**
	 * Whatsapp
	 */
    public void WhatsAppShare() {
	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");
	            String text = "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuration: "+duration+"s \nScore: "+score+" pts";
	    waIntent.setPackage("com.whatsapp");
	    if (waIntent != null) {
	    	hasShared=true;
	    	prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", true).commit();
	    	waIntent.putExtra(Intent.EXTRA_TEXT, text);//
	        startActivity(Intent.createChooser(waIntent, "Share with"));
	    } else {
	        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
	                .show();
	    }

	}
   
    /**
	 * Mail
	 */
    public void mailPaja(){
		hasShared=true;
		prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", true).commit();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Te mandan una paja");
		intent.putExtra(Intent.EXTRA_TEXT, "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuration: "+duration+"s \nScore: "+score+" pts");
		startActivity(Intent.createChooser(intent, "Share Paja"));
	}
    
    public void anim(View view){
		View animationTarget = view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
	    Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);
	}
	
}

