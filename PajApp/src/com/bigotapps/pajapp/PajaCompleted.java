package com.bigotapps.pajapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;


public class PajaCompleted extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	public long[] pattern = {1000,100};


	Integer score;
	long topScore;
	long topGolpes;
	long topDuration;
	boolean hasShared=false;
	boolean hasSharedPref;
	long duration;
	long golpes;
	
	Integer badgeScoreTarget=100000;
	Integer badgeDurationTarget=60;
	boolean badgeShareTarget=false;
	Integer badgeGolpesTarget=200;
	public boolean wantedToShare=false;
	
    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    
    public ImageButton mShareButton; 
    SharedPreferences prefs;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paja_completed);
		
		 mPlusClient = new PlusClient.Builder(this, this, this).build();
		 // Progress bar to be displayed if the connection failure is not resolved.
	        mConnectionProgressDialog = new ProgressDialog(this);
	        mConnectionProgressDialog.setMessage("Signing in, biatch...");

		 mShareButton = (ImageButton) findViewById(R.id.gPlusShareButton);
		 
		//shared preferences, to store Best Scores and stuff
			prefs = this.getSharedPreferences("com.bigotapps.pajapp", Context.MODE_PRIVATE);
		
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		if(this.getIntent().getExtras() != null){
			//use paja parameters
			duration = Long.valueOf(this.getIntent().getExtras().getString("duration"));
			score = Integer.valueOf(this.getIntent().getExtras().getString("score"));

			golpes = Long.valueOf(this.getIntent().getExtras().getString("golpes"));
			TextView scoreView = (TextView) findViewById(R.id.textViewScore);
			scoreView.setText(score+" pts");
			
			//quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
		}
		
		fetchPrefs(prefs);
		updatePrefs(prefs); //incl badgess
		
		boolean FB_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.FB_Connect", false);
		boolean GP_CONNECT = prefs.getBoolean("com.bigotapps.pajapp.gPlusConnect", false);
		
		if(GP_CONNECT){
			gPlusConnect();
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
			gPlusConnect();
		}
	}

	public void playSound(){
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.open);
		mp.start();
	}
	
	public void mailPaja(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Te mandan una paja");
		intent.putExtra(Intent.EXTRA_TEXT, "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuration: "+duration+"s \nScore: "+score+" pts");
		startActivity(Intent.createChooser(intent, "Share Paja"));
	}
	
	public void WhatsAppShare() {

	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");
	            String text = "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuration: "+duration+"s \nScore: "+score+" pts";
	    waIntent.setPackage("com.whatsapp");
	    if (waIntent != null) {
	    	hasShared=true;
	    	waIntent.putExtra(Intent.EXTRA_TEXT, text);//
	        startActivity(Intent.createChooser(waIntent, "Share with"));
	    } else {
	        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
	                .show();
	    }

	}
	
	
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
	 
	 public void onClick(View view){
		 switch(view.getId()){
			case R.id.gPlusShareButton:
				gplusShare();
				break;
			case R.id.WhatsappButton:
				WhatsAppShare();
				break;
			case R.id.mailButton:
				mailPaja();
				break;
			case R.id.NewPajaButton:
				newPaja();
				break;
			case R.id.WOFButton:
				WoF();
				break;
			
	 }
}
	 
	 public void gplusShare() {
		 
		 if (!mPlusClient.isConnected()) {
			 Toast.makeText(getApplicationContext(), "notConnectedCalled", Toast.LENGTH_SHORT).show();
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
	    	  PlusShare.Builder builder = new PlusShare.Builder(this, mPlusClient);

	       // Set call-to-action metadata.
	       /*
	          builder.addCallToAction(
	              "REPLY", // call-to-action button label 
	              Uri.parse("http://plus.google.com/pages/create"), // call-to-action url (for desktop use)
	              "pajaBack" // call to action deep-link ID (for mobile use), 512 characters or fewer );
	          */
	          
	          // Set the content url (for desktop use).
	          builder.setContentUrl(Uri.parse("http://www.google.frhttp://commons.wikimedia.org/wiki/File:Clenched_human_fist.png"));

	          // Set the target deep-link ID (for mobile use).
	          builder.setContentDeepLinkId("pajaBack",
	                 null, null, null);
	        
	          // Set the share text.
	          builder.setText("I'm sending you an appJob!");

	          startActivityForResult(builder.getIntent(), 0);
	   
	      } else {
	          // Prompt the user to install the Google+ app.
	    	  GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
	      }
		 }
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            
            if(wantedToShare){
            	Toast.makeText(getApplicationContext(), "WANTS TO SHARE", Toast.LENGTH_SHORT).show();
            	gplusShare();
            }
            mPlusClient.connect();
        }
    }

    //@Override
    public void onConnected(Bundle b) {
    	mConnectionProgressDialog.dismiss();
        String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
        mShareButton.setEnabled(true);
        
    }

    @Override
    public void onDisconnected() {
	    	Log.d(TAG, "disconnected");
	    	mShareButton.setEnabled(false);
	    }
	 
	 
	 /* QuickToaster
	 * for runtime debugging etc 
	 */
	public void quickToast (String message){
		 Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
	 }
	
	
	
	public void updatePrefs(SharedPreferences prefs){
			prefs.edit().putLong("com.bigotapps.pajapp.topscore",Math.max(score, topScore)).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topduration",Math.max(duration, topDuration)).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topgolpes",Math.max(golpes, topGolpes)).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.hasshared",hasShared||hasSharedPref).commit();
			fetchPrefs(prefs); //need to re-fetch in order to update local values tested in updateBadges 
			updateBadges(prefs);
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
	


	
	public void fetchPrefs(SharedPreferences prefs){
		topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
		topGolpes=prefs.getLong("com.bigotapps.pajapp.topgolpes", 0);
		topDuration=prefs.getLong("com.bigotapps.pajapp.topduration", 0);
		hasSharedPref=prefs.getBoolean("com.bigotapps.pajapp.hasshared", false);
	}
	
	

	public void gPlusConnect(){
		mPlusClient.connect();
		
		if(!mPlusClient.isConnected()){
			if (mConnectionResult == null) {
	           mConnectionProgressDialog.show();
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

