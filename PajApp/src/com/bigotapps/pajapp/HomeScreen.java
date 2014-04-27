package com.bigotapps.pajapp;


import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bigotapps.pajapp.fApplication.TrackerName;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;

public class HomeScreen extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener  {
	
	public MediaPlayer mp;
	
	 private ProgressDialog mConnectionProgressDialog;
	 private PlusClient mPlusClient;
	 private ConnectionResult mConnectionResult;
	 private UiLifecycleHelper uiHelper;
	 private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	
	private DrawerLayout mDrawerLayout;
	
	private HomeScreenFragment HomeScreenFragment;
	private RightDrawerFragment RightDrawerFragment;
	private LeftDrawerFragment LeftDrawerFragment;
	
	public ImageButton buttonLevel1,buttonLevel2,buttonLevel3,buttonLevel4;	
	
	public SharedPreferences prefs;
	boolean isDiff2Unlocked,isDiff3Unlocked;
	int totalFapps=0;
		
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //ANALYTICS: Get a Tracker (should auto-report)
        ((fApplication) getApplication()).getTracker(fApplication.TrackerName.APP_TRACKER);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_credits_drawer);
        
        if (savedInstanceState == null) {
	        // Add the fragments on initial activity setup
	    	HomeScreenFragment = new HomeScreenFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.content_frame, HomeScreenFragment)
	        .commit();
	        
	        RightDrawerFragment = new RightDrawerFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.right_drawer, RightDrawerFragment)
	        .commit();
	        
	        LeftDrawerFragment = new LeftDrawerFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.left_drawer, LeftDrawerFragment)
	        .commit();
	        
	    } else {
	        // Or set the fragments from restored state info
	    	HomeScreenFragment = (HomeScreenFragment) getSupportFragmentManager()
	        .findFragmentById(R.id.content_frame);
	    	RightDrawerFragment = (RightDrawerFragment) getSupportFragmentManager()
	    	        .findFragmentById(R.id.right_drawer);
	    	LeftDrawerFragment = (LeftDrawerFragment) getSupportFragmentManager()
	    	        .findFragmentById(R.id.left_drawer);
	    }
	
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        //Set the drawer listener and attach it to the drawer
        mDrawerLayout.setDrawerListener(new DrawerListener(){

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				Log.i("GregBug","Drawer Closed");
				//adjustContentPosition(true);//TRUE: parameter means that content frame should be re-centered
			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				Log.i("GregBug","Drawer Opened");
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				//Log.i("GregBug","Drawer Slide: "+String.valueOf(arg1));
				
				//sliding the content view: need to do it right, otherwise slows down everything? 
				//adjustContentPosition(false);
				
			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				//Log.i("GregBug","Drawer State Changed");
			}
        }); //end of DrawerListener
		
        //set Level Button states
        buttonLevel1 = (ImageButton) findViewById(R.id.buttonLevel1);
    	buttonLevel2 = (ImageButton) findViewById(R.id.buttonLevel2);
    	buttonLevel3 = (ImageButton) findViewById(R.id.buttonLevel3);
    	setButtonLevelState();
    	
        //start sound
        mp = MediaPlayer.create(this, R.raw.zipp);
        
      //BUILD GPLUS CLIENT
		 mPlusClient = new PlusClient.Builder(this, this, this)
        .setScopes(Scopes.PLUS_LOGIN)  // Space separated list of scopes
        .build();
		 // Progress bar to be displayed if the connection failure is not resolved.
	      mConnectionProgressDialog = new ProgressDialog(this);
	      mConnectionProgressDialog.setMessage(getString(R.string.gplus_connect2));
	      
	    //shared preferences, to store Best Scores and stuff
	      	getPrefs();
}

	@Override
    public void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawers();
      //shared preferences, to store Best Scores and stuff
      	getPrefs();
	}
	
	@Override
    public void onStart() {
        super.onStart();
      //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	@Override
    public void onStop() {
        super.onStop();
        mPlusClient.disconnect();
      //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	
	@Override
	public void onDestroy() {
    super.onDestroy();
  //Get an Analytics tracker to report app stops & uncaught exceptions etc.
    GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	
	public void onClick(View view){
		 switch(view.getId()){
			case R.id.shareHomeButton:
				mDrawerLayout.openDrawer(Gravity.RIGHT);
				break;	
			case R.id.goLevelHomeButton:
				mDrawerLayout.openDrawer(Gravity.LEFT);
				break;
			case R.id.buttonCloseLeft:
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			case R.id.buttonCloseRight:
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				break;
			case R.id.buttonFakeFapp:
				fakePaja();
				break;
			case R.id.fappButtonCredits:
				sendAnalyticsEvent("game","start","default level");
				startPaja(1);
				break;
			case R.id.buttonLevel1:
				sendAnalyticsEvent("game","start","level 1");
				startPaja(1);
				break;
			case R.id.buttonLevel2:
				sendAnalyticsEvent("game","start","level 2");
				if(isDiff2Unlocked) startPaja(2);
				else Toast.makeText(this, getString(R.string.unlockDiff2), Toast.LENGTH_SHORT).show();
				break;
			case R.id.buttonLevel3:
				sendAnalyticsEvent("game","start","level 3");
				if(isDiff3Unlocked) startPaja(3);
				else Toast.makeText(this, getString(R.string.unlockDiff3_1)+String.valueOf(15-totalFapps)+" "+getString(R.string.unlockDiff3_2), Toast.LENGTH_SHORT).show();
				break;
			case R.id.FB_share_drawer:
				sendAnalyticsEvent("share","share from Home","Facebook");
				fbShare();
				break;
			case R.id.GPlus_share_drawer:
				sendAnalyticsEvent("share","share from Home","Google Plus");
				gplusShare();
				break;
			case R.id.W_share_drawer:
				sendAnalyticsEvent("share","share from Home","Whatsapp");
				WhatsAppShare();
				break;
			case R.id.Rate_drawer:
				sendAnalyticsEvent("rate","rate from Home","rate");
				//do something
				Toast.makeText(this, "Please rate the app on Google Play", Toast.LENGTH_SHORT).show();
				break;
			case R.id.helpButton:
				sendAnalyticsEvent("help","click on Help from Home","help");
				//do something
				Toast.makeText(this, "Trigger Tutorial", Toast.LENGTH_SHORT).show();
				break;
	 }
}
	
	public void getPrefs(){
		//shared preferences, to store Best Scores and stuff
		prefs = this.getSharedPreferences("com.bigotapps.pajapp", Context.MODE_PRIVATE);
		isDiff2Unlocked=prefs.getBoolean("com.bigotapps.pajapp.diff2Unlocked", false);
		isDiff3Unlocked=prefs.getBoolean("com.bigotapps.pajapp.diff3Unlocked", false);
		totalFapps=prefs.getInt("com.bigotapps.pajapp.totalFapps", 0);
		Toast.makeText(this, String.valueOf(isDiff2Unlocked)+"-"+String.valueOf(isDiff3Unlocked)+"-"+String.valueOf(totalFapps), Toast.LENGTH_SHORT).show();
	}
	
	 public void startPaja(int difficulty){
		mp.release();
		 //Toast.makeText(this, "START PAJA", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,com.bigotapps.pajapp.MainActivity.class);
		i.putExtra("difficulty", difficulty);
		Log.i("GregBug","extra in Intent: difficulty "+String.valueOf(difficulty));
		startActivity(i);
		    }
	
	 public void share(){
			Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
			//Intent i = new Intent(this,MainActivity.class);
			//startActivity(i);
			    }
	 public void goFBShare(){
			//Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this,com.bigotapps.pajapp.FBShare.class);
			startActivity(i);
	 }
	 
	public void fakePaja(){
		 Intent i = new Intent(getApplicationContext(),PajaCompleted.class);
			i.putExtra("score", "5000");
			i.putExtra("duration", "100");
			i.putExtra("golpes", "2000");
			i.putExtra("pain", "8");
			startActivity(i);
	 }
	 
	public void setButtonLevelState(){
		//no se pueden modificar los botones desde aqu’?
		//que est‡n en el fragment
	}
	
	private void adjustContentPosition(boolean allClosed) {
        //MISSING: when a drawer is opened and trying to directly open the other: content view is centered
		
		Integer slide=0;
        View content =findViewById(R.id.content_frame);
      
       if(allClosed){//a drawer is opened and user clicks on content frame => need to reset position
    	   content.setX(0);
       }
       
       else{ // a drawer is sliding
    	  if(mDrawerLayout.isDrawerVisible(Gravity.LEFT)){// left drawer is sliding
			
    		float leftX= findViewById(R.id.left_drawer).getRight();
			float contentX = findViewById(R.id.content_frame).getLeft();
			slide = Math.round(leftX-contentX);
			content.offsetLeftAndRight(slide);
			
		}
		
		else if (mDrawerLayout.isDrawerVisible(Gravity.RIGHT)){//right drawer is sliding
			float rightX = findViewById(R.id.right_drawer).getLeft();
			float contentX = findViewById(R.id.content_frame).getRight();
			slide = Math.round(rightX-contentX);
			content.offsetLeftAndRight(slide);
		}
		//Log.i("GregBug","calling adjust position"+String.valueOf(slide));
       }
	
    }
	
	public void sendAnalyticsEvent (String category, String action, String label){
		// Get tracker.
	    Tracker t = ((fApplication) this.getApplication()).getTracker(
	        TrackerName.APP_TRACKER);
	    // Build and send an Event.
	    t.send(new HitBuilders.EventBuilder()
	        .setCategory(category)
	        .setAction(action)
	        .setLabel(label)
	        .build());

	}
	
	 /* Facebook
	 */
	public void fbShare(){
		try{
			OpenGraphObject fapp = OpenGraphObject.Factory.createForPost("pajappbeta:fapp");
			fapp.setType("fapp");
			fapp.setTitle("Fapp");
			//fapp.getData().setProperty("duration", "FAKE");
			//fapp.getData().setProperty("score", "FAKE");
		
			String url="http://www.nolandalla.com/wp-content/uploads/2014/01/clenched-fist.jpg";
			fapp.setImageUrls(Arrays.asList(url));
			
			OpenGraphAction action = OpenGraphAction.Factory.createForPost("pajappbeta:perform");
			action.setProperty("fapp", fapp);
		
			FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(this,action,"fapp")
			        .build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
			}
		catch (Exception e){
			//TODO something something
			Log.i("Greg","Exception on fbShare: "+e.getMessage());
		}
	}
	private Session.StatusCallback callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.i("Greg","SESSION CHANGED??");
		// TODO Auto-generated method stub	
	}
		

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
			 gPlusConnect();
			 
		 }
		 else{
			 final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			 //if G+ installed
			 if (errorCode == ConnectionResult.SUCCESS) {
		    	 PlusShare.Builder builder = new PlusShare.Builder(this, mPlusClient);	             
		         builder.addCallToAction("CHALLENGE",Uri.parse("https://plus.google.com/u/0/106383952060977394931"),"pajaBack");
	
		         builder.setContentUrl(Uri.parse("https://plus.google.com/u/0/106383952060977394931"));
		         
		         // Set the target deep-link ID (for mobile use).
		         builder.setContentDeepLinkId("pajaBack",
		                 null, null, null);
		        
		          // Set the share text.
		         String messageGPlusPost = getString((R.string.messageGPlus_global)); 
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
        Log.w("Greg",accountName + " is connected.");        
			gplusShare();
		}
        
    
    @Override
    public void onDisconnected() {
	    	Log.d("Greg", "G+ disconnected");
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
	            String text = getString(R.string.messageWhatsapp_global);
	    waIntent.setPackage("com.whatsapp");
	    if (waIntent != null) {
	    	waIntent.putExtra(Intent.EXTRA_TEXT, text);//
	        startActivity(Intent.createChooser(waIntent, "Share with"));
	    } else {
	        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
	                .show();
	    }

	}
   
	
}
	
