package com.bigotapps.pajapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
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

	public long[] pattern = {10,1000,100,1000,100,1000};
	Long score;
	Long topScore;
	String duration;
	Long golpes;

    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    
    public ImageButton mShareButton; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paja_completed);
		
		 mPlusClient = new PlusClient.Builder(this, this, this).build();
         mPlusClient.connect();

		 mShareButton = (ImageButton) findViewById(R.id.gPlusShareButton);
		 
		//shared preferences, to store Best Scores and stuff
			SharedPreferences prefs = this.getSharedPreferences(
				      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		if(this.getIntent().getExtras() != null){
			//use paja parameters
			score = Long.valueOf(this.getIntent().getExtras().getString("score"));
			duration = this.getIntent().getExtras().getString("duration");
			golpes = Long.valueOf(this.getIntent().getExtras().getString("golpes"));
			TextView scoreView = (TextView) findViewById(R.id.textViewScore);
			scoreView.setText(score+" pts");
			
			//quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
		}
		
		//retrieve topScore and update if relevant
				topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
				if(score>topScore){
			 		topScore=updateTopScore(prefs, score);
			 		findViewById(R.id.TextNewHighScore).setVisibility(View.VISIBLE);
			 	}
		
		// Vibrate according to pattern (-1 means don't repeat)
				v.vibrate(pattern,-1);
				playSound();
				
		//update Badges (Achievement Unlocks)
		updateBadges(prefs);
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    mPlusClient.disconnect();
	    mShareButton.setEnabled(false);
	}
	
	public void playSound(){
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.open);
		mp.start();
	}
	
	public void mailPaja(View v){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Te env�an una paja");
		intent.putExtra(Intent.EXTRA_TEXT, "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuraci�n: "+duration+"s \nScore: "+score+" pts");
		startActivity(Intent.createChooser(intent, "Share Paja"));
	}
	
	public void onClickWhatsApp(View view) {

	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");
	            String text = "Hola,\n \nUn amigo quiere compartir contigo la paja siguiente: \n \nDuraci�n: "+duration+"s \nScore: "+score+" pts";
	    waIntent.setPackage("com.whatsapp");
	    if (waIntent != null) {
	        waIntent.putExtra(Intent.EXTRA_TEXT, text);//
	        startActivity(Intent.createChooser(waIntent, "Share with"));
	    } else {
	        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
	                .show();
	    }

	}
	
	
	 public void newPaja(View v){
	    	Intent i = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
	    }
	 
	 public void WoF(View v){
		 	Intent i = new Intent(getApplicationContext(),WallOfFame.class);
		 	i.putExtra("score", Long.toString(score));
		 	i.putExtra("duration", duration);
		 	//quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
		 	startActivity(i);
	    }
	 
	 public void gplusShare(View view) {
		 
		 final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	      if (errorCode == ConnectionResult.SUCCESS) {
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
	 

	    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        if (result.hasResolution()) {
	            try {
	                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	            } catch (SendIntentException e) {
	                mPlusClient.connect();
	            }
	        }
	        // Enregistrer le r�sultat et r�soudre l'�chec de connexion lorsque l'utilisateur clique.
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
	
	public Long updateTopScore(SharedPreferences prefs, Long newTopScore){
		prefs.edit().putLong("com.bigotapps.pajapp.topscore", newTopScore).commit();
		return newTopScore;
	}
	
	public void updateBadges(SharedPreferences prefs){
		//do stuff here
	}
	
}

