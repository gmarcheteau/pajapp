package com.bigotapps.pajapp;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigotapps.pajapp.fApplication.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends Activity implements SensorEventListener {

	private static final int RESULT_LOAD_IMAGE = 100;
	/**
	 * sensor stuff 
	 */
	private SensorManager mSensorManager;
	private Sensor mSensor;
	public int sensorSensitivity=10;
	/**
	 * Minimum delay between sensor readings
	 * To avoid conflicts 
	 */
	public long lastSensorUpdate = 1;
	public long lastGolpe = 0;
	public long delay_golpe=1000;
	public long delay_sensor=1000;
	public long minDelay = 20;
	public long timeSinceLastChange=300;
	public long previousLastChange=0;
	public long IDLENESS = 60;
	
	public boolean dolor=false;
	
	private static final String TAG = "GregBug";
	
	/**
	 * PajaMetrics
	 */
	public boolean RIGHT_HAND=true;
	public boolean IS_DOWN;
	public int golpes = 0;
	public int painCount=0;
	public int targetGolpes = 50;
	public int countSlow=1;
	public int countFast=1;
	public int countVeryFast=1;
	public float progress=0;
	public int progressGoal;
	public long painThresold;
	public int decay_speed;
	public int progressPainPenalty;
	public int scorePainPenalty;
	public int scoreLevelDownPenalty;
	public int scoreGolpe;
	public int freqCoeff=1;
	public float avFreq=1;
	public float factorMult=2;
	public int backHeight;
	public int rectHeight=100;
	public float statusProgress=0;
	
	
	public int level=1;
	public int exlevel=1;
	public int maxLevel=1;
	public int difficulty=1;
	
	public fappLevel[] fappLevels;
	public int[][] levelParams;
	
	public int score=0;
	public int[] lastSavedScore={0,0,0,0};
	
	/**
	Rectangle and drawing
	 */
	public int backFadeOut=10;
	public int redFadeOut=10;
	public boolean jumpLevel=true;
	int r,g,b=0;
	public String cMessageTemp="";
	public View rect;
	public View back;
	public ImageView movingHand;
	public TextView scoreText, cmessage,levelText;
	Vibrator v;
	
	/**TIMING 
	 */
	public boolean PAJA_STARTED = false;
	public long startTime;
	public long duration=1;
	public long lastChange;
	public long[] pattern = {10,1000,100,1000,100,1000};
	
	/**SOUND
	 */
	SoundPool soundPool;
	Integer soundId;
	Integer soundId2;
	AudioManager mgr;
	float volume;
    
    private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//ANALYTICS: Get a Tracker (should auto-report)
        ((fApplication) getApplication()).getTracker(fApplication.TrackerName.APP_TRACKER);
        
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        setContentView(R.layout.activity_main);
	        
	      //ad initialization
	        AdView adView = (AdView) this.findViewById(R.id.ad2);
	        
	        
	        try{
	        	AdRequest adRequest = new AdRequest.Builder()
		            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		            .addTestDevice("BF37ACEAC7B5CA1DD5881A71B6D1E155")
		            .build();
	            adView.loadAd(adRequest);
	        }
	        catch(Exception e){
	        	Log.i("Ads_greg",e.getMessage());
	        }
	        
	        
	     // Create the interstitial.
	        interstitial = new InterstitialAd(this);
	        interstitial.setAdUnitId(getString(R.string.admob_completed_id));
	     // Create ad request.
	        AdRequest adRequest = new AdRequest.Builder()
		        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		        .addTestDevice("BF37ACEAC7B5CA1DD5881A71B6D1E155")
		        .build();;
	     // Begin loading your interstitial.
	        interstitial.loadAd(adRequest);
	        
		
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		loadSound();
		volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	
		if(this.getIntent().getExtras().getString("message") != null){
			//when user is coming from deep-linking
			String message=this.getIntent().getExtras().getString("message");
			quickToast(message);
			}
		
		//if levels are unlocked
		if(this.getIntent().getExtras().getInt("difficulty") >0){
			difficulty=this.getIntent().getExtras().getInt("difficulty");
			Log.i("GregBug","Difficulty: "+String.valueOf(maxLevel));
			setLevelParameters(difficulty);
		}
		else{
			setLevelParameters(1);
			Log.i("GregBug","Setting difficulty to 0 as no difficulty found");
		}
		
		//initialize pref file if new
		initUser();
		
		//graphic element handlers
  		back= (View) findViewById(R.id.back);
  		rect= findViewById(R.id.rectLayout);
  		movingHand = (ImageView) findViewById(R.id.movingHand);
  		scoreText = (TextView) findViewById(R.id.textViewCurrentScore);
  		levelText = (TextView) findViewById(R.id.levelView);
  		cmessage = (TextView) findViewById(R.id.centerMessage);
  		// Get instance of Vibrator from current Context
  		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
  		
  		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOut(movingHand);
                movingHand.setVisibility(View.INVISIBLE);
            }
        }, 3000);
  		
            		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_settings:
				goSettings();
				return true ;
			case R.id.view_badges:
				goGallery();
				return true ;
			case R.id.forcePajaComplete:
				endPaja();
				return true ;
			case R.id.getAllBadges:
				getAllBadges();
				return true ;
			case R.id.badgeGalleryDebug:
				goGallery();
				return true ;
			case R.id.view_credits:
				goCredits();
				return true ;
			case R.id.change_back:
				changeBackground();
				return true ;
			default:
				return false;
		}
	}


	@Override
	protected void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	protected void onPause() {
	    super.onPause();
	    soundPool.pause(soundId);
	    golpes=0;
	    mSensorManager.unregisterListener(this);
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
    //Get an Analytics tracker to report app stops & uncaught exceptions etc.
      GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
  
  @Override
	public void onDestroy() {
    super.onDestroy();
  //Get an Analytics tracker to report app stops & uncaught exceptions etc.
    GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
  
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if(!PAJA_STARTED){
			startTime=System.currentTimeMillis();
			PAJA_STARTED=true;
			//initialize level
			level=1;
			changeLevel(0);
		}
		else{
		// check last sensor update action
		delay_golpe=System.currentTimeMillis()-lastGolpe;
		delay_sensor= System.currentTimeMillis()-lastSensorUpdate;
		
		backHeight=back.getHeight();
		
			if(delay_sensor>minDelay){
					//back.setBackgroundColor(Color.rgb(Math.round(254*progress/progressGoal), 0, 0));
					back.setBackgroundColor(Color.rgb(0, 0, 0));
				if(delay_golpe>IDLENESS){//continuous decay	
					progress=Math.max(progress-decay_speed, 0);
					statusProgress=progress/progressGoal;
					score=Math.max(0, score-decay_speed);
					//Log.i("level","score penalty for idleness");
					}
					
				//if level is not completed
				if(progress<progressGoal){	
					if(delay_golpe<painThresold&&!IS_DOWN){	//Pain if freq is too high		
						if (delay_golpe!=previousLastChange){
							painCount++;
							sendAnalyticsEvent("game","penalty","pain",Long.valueOf(delay_golpe));
							Log.w("golpe","PAIN --- Delay: "+String.valueOf(delay_golpe)+"   Threshold: "+String.valueOf(painThresold));
						 	//Progress penalty for Pain activation
					    	soundPool.play(soundId2,volume,volume,1,0,1.3f);
					    	progress=Math.max(0,progress-progressPainPenalty);
					    	statusProgress=progress/progressGoal;
					    	
					    	//Score penalty
					    	score=Math.max(0,score-scorePainPenalty);
					    	Log.w("golpe","Score penalty: "+String.valueOf(scorePainPenalty)+"     New score: "+String.valueOf(score));
							
							if(redFadeOut>0){
								back.setBackgroundColor(Color.rgb((25*redFadeOut), 0, 0));
						    	redFadeOut--;
						   	
						    	if(dolor!=true){
						    		v.vibrate(100);
						    	}
						    	dolor=false;
								}
							
							else{
								redFadeOut=10;
								back.setBackgroundColor(Color.rgb(0, 0, 0));
								dolor=false;
								cmessage.setText("");
								previousLastChange=delay_golpe;
								}
						}
					}
							
					//decay or pain brings progress down to 0 => level down
					if(progress==0 && level>1){
						changeLevel(-1);
						score=Math.max(0, score-scoreLevelDownPenalty);
						cmessage.setText(cMessageTemp);
						cmessage.setVisibility(View.VISIBLE);
					}
					
					////nextLevel FadeOut animation           
					if(jumpLevel==true){
						
						if(backFadeOut>0){
							back.setBackgroundColor(Color.rgb((25*backFadeOut), (25*backFadeOut), (25*backFadeOut)));
					    	backFadeOut--;
					    	
						}else{
							backFadeOut=10;
							back.setBackgroundColor(Color.rgb(0, 0, 0));
							
							//Next level vibration feedback
					    	//v.vibrate(1000);
					    	
							jumpLevel=false;
							//cmessage.setVisibility(View.INVISIBLE);
						}  	 	
					}
					
					//paint rectangle
					rect.setBackgroundColor(Color.rgb(Math.round(254*progress/progressGoal), Math.round(254*progress/progressGoal)*g, Math.round(254*progress/progressGoal)*b));
					
					if(progress>0){
				    	playSound(volume,Math.min(2f, 0.5f+1.6f*(progress)/1000));
				    	}
					
					//Update progress meter height
					float tempRectHeight=statusProgress*backHeight;
					rectHeight=(int)tempRectHeight;
					rect.setMinimumHeight(rectHeight);
				
					if(event.values[1]<0&&IS_DOWN){//hand up
							//hand.scrollTo(0, 150);
							//lastSensorUpdate=System.currentTimeMillis();
							Log.e("golpe","Antigolpe");
							IS_DOWN=false;
						}
					
					else if(event.values[1]>sensorSensitivity&&!IS_DOWN){ //hand down
							golpes++;
							score+=scoreGolpe;
							Log.i("golpe","Golpe: "+String.valueOf(delay_golpe)+"ms delay");
							//delay=System.currentTimeMillis()-lastChange;
							IS_DOWN=true;
							lastGolpe=System.currentTimeMillis();
											
							if(golpes>3){
								updateProgress(1);				
							}
							rect.setBackgroundColor(Color.rgb(Math.round(254*progress/progressGoal), Math.round(254*progress/progressGoal)*g, Math.round(254*progress/progressGoal)*b));
					} //end of hand down				
				
					scoreText.setText(Integer.toString(score));
				}
				else{//if level is completed
					changeLevel(1);
					cmessage.setText(cMessageTemp);
					cmessage.setVisibility(View.VISIBLE);
					jumpLevel=true;	
				} 
			}	
		
			else{
				Log.i("sensor","too early");
			}
		}
		
		
	}
	

    public float updateProgress(int i){
    	
    	float vprogress;
    	
    	if(progress==0 && i<=0){
    		progress=0;
    	}else if(progress>0 && i<0){
    		progress=Math.max(0, progress+i);
    	}else{
        	if(delay_golpe!=0){
        		vprogress=(1000/delay_golpe)*factorMult;
            	progress=Math.max(0,progress+vprogress);
            	//progress=progress+20;       	
        	}    			
    	}
    	
    	statusProgress=progress/progressGoal;
    	//if(progress%3==1 || true){
    	
    	//}
    	return progress;
    	
    }
   
    public void changeLevel(int l){
    	//the paja is over
    	if(level+l==5){
    		sendAnalyticsEvent("game","fApp completed","fApp completed",Long.valueOf(score));
    		endPaja();
    		Log.i("level", "paja ended");
    	}
    	else{
    		if(l<0&&level>1){
    			sendAnalyticsEvent("game","level down","from level "+String.valueOf(level),Long.valueOf(level));
	    		Toast.makeText(this, "level down", Toast.LENGTH_SHORT).show();
	    		Log.i("level","reached 0 out of decay or pain => level down");
	    			level=level+l;
		    		progressGoal=fappLevels[level-1].getProgressGoal();
		    		decay_speed=fappLevels[level-1].getDecaySpeed();
		    		progressPainPenalty=fappLevels[level-1].getProgressPainPenalty();
		    		painThresold=fappLevels[level-1].getPainThreshold();
		    		scorePainPenalty=fappLevels[level-1].getScorePainPenalty();
		    		scoreLevelDownPenalty=fappLevels[level-1].getScoreLevelDownPenalty();
		    		scoreGolpe=fappLevels[level-1].getScoreGolpe();
		    		score=Math.min(score,lastSavedScore[level-1]); //score accumulated at failed level is lost
		    		progress=progressGoal*0.7f;
		    		levelText.setText(String.valueOf(level));
	    			}
	    	else if(l>=0){//level up
	    			sendAnalyticsEvent("game","level up","to level "+String.valueOf(level+l),Long.valueOf(level+l));
		    		level=level+l;
		    		progressGoal=fappLevels[level-1].getProgressGoal();
		    		decay_speed=fappLevels[level-1].getDecaySpeed();
		    		progressPainPenalty=fappLevels[level-1].getProgressPainPenalty();
		    		painThresold=fappLevels[level-1].getPainThreshold();
		    		scorePainPenalty=fappLevels[level-1].getScorePainPenalty();
		    		scoreLevelDownPenalty=fappLevels[level-1].getScoreLevelDownPenalty();
		    		scoreGolpe=fappLevels[level-1].getScoreGolpe();
		    		lastSavedScore[Math.max(0, level-2)]=score; //secure score reached so far
		    		progress=progressGoal*0.25f;
		    		levelText.setText(String.valueOf(level));
	    	}
    	}
  
	}
	


    public void endPaja(){
		
		soundPool.release();
    	mSensorManager.unregisterListener(this);
		duration=(System.currentTimeMillis()-startTime)/1000;
    	Intent i = new Intent(getApplicationContext(),PajaCompleted.class);
		i.putExtra("score", String.valueOf(score));
		i.putExtra("duration", String.valueOf(duration));
		i.putExtra("golpes", String.valueOf(golpes));
		i.putExtra("pain", String.valueOf(painCount));

		//display the interstitial
        //displayInterstitial();
		startActivity(i);
    }
 public void goGallery(){
		
		mSensorManager.unregisterListener(this);
    	Intent i = new Intent(getApplicationContext(),badgeGallery.class);
		startActivity(i);
    }
    
public void getAllBadges(){
	SharedPreferences prefs = this.getSharedPreferences(
		      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
	prefs.edit().putBoolean("com.bigotapps.pajapp.badgeScore_unlocked", true).commit();
	prefs.edit().putBoolean("com.bigotapps.pajapp.badgeDuration_unlocked", true).commit();
	prefs.edit().putBoolean("com.bigotapps.pajapp.badgeGolpes_unlocked", true).commit();
	prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", true).commit();
	quickToast("all badges unlocked");
	}

    
 public void goSettings(){
		
		mSensorManager.unregisterListener(this);
		
    	Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
		i.putExtra("score", String.valueOf(score));
		i.putExtra("duration", String.valueOf(duration));
		startActivity(i);
    }
 
 public void goBadges(){
		
		mSensorManager.unregisterListener(this);

		Intent i = new Intent(getApplicationContext(),WallOfFame.class);
		startActivity(i);
 }
 
 public void goCredits(){
		
		mSensorManager.unregisterListener(this);

		Intent i = new Intent(getApplicationContext(),Credits.class);
		startActivity(i);
}
    
    
    /**
	 * QuickToaster
	 * for runtime debugging etc 
	 */
	public void quickToast (String message){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();	
	}

	
	public void initUser(){
		SharedPreferences prefs = this.getSharedPreferences(
			      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		boolean IS_NEW=prefs.getBoolean("com.bigotapps.pajapp.isnew", true);
		
		if(IS_NEW){
			//initialize achievements etc.
			prefs.edit().putBoolean("com.bigotapps.pajapp.isnew", false).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeScore_unlocked", false).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeDuration_unlocked", false).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeGolpes_unlocked", false).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.badgeShare_unlocked", false).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.badgeScore_progress", 0).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.badgeGolpes_progress", 0).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.badgeDuration_progress", 0).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.hasshared", false).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topgolpes", 0).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topscore", 0).commit();
			prefs.edit().putLong("com.bigotapps.pajapp.topduration", 0).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.gPlusConnect", false).commit();
			prefs.edit().putBoolean("com.bigotapps.pajapp.FBConnect", false).commit();
		}
				
	}
	
   
	
	
public void anim(View view){
	ImageView animationTarget = (ImageView) view;
    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
	Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
    animationTarget.startAnimation(animation);
}
public void fadeOut(View view){
	AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	anim.setDuration(1000);
	view.startAnimation(anim);
}
public void loadSound(){
	soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	soundId = soundPool.load(getApplicationContext(), R.raw.shake, 1);
	mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
	 {
	     @Override
	     public void onLoadComplete(SoundPool arg0, int arg1, int arg2)
	     {
	         Log.i(TAG, "Sound Loaded");
	         //launch sound
	         //playSound(volume,1f);
	     }
	 });
	
	soundId2 = soundPool.load(getApplicationContext(), R.raw.ouch2, 1);
	
	}
public void playSound(float volume,float speed){
	soundPool.stop(soundId);
	soundPool.play(soundId, volume, volume, 0, 1, speed);
	//soundPool.setLoop(soundId, -1);
	//soundPool.setRate(soundId, speed);
} 

public void updateSound(float speed){
	soundPool.setRate(soundId, speed);
}

public void changeBackground(){
	Intent i = new Intent(
	Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	startActivityForResult(i, RESULT_LOAD_IMAGE);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
     
    if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        
        //
        back= (ImageView) findViewById(R.id.back);
        //back.setBackgroundResource(R.drawable.badge_fire);
        //back.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        /*
        try {
			back.setImageBitmap(decodeUri(selectedImage));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        // String picturePath contains the path of selected Image
    }


}

private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

    // Decode image size
    BitmapFactory.Options o = new BitmapFactory.Options();
    o.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

    // The new size we want to scale to
    final int REQUIRED_SIZE = 140;

    // Find the correct scale value. It should be the power of 2.
    int width_tmp = o.outWidth, height_tmp = o.outHeight;
    int scale = 1;
    while (true) {
        if (width_tmp / 2 < REQUIRED_SIZE
           || height_tmp / 2 < REQUIRED_SIZE) {
            break;
        }
        width_tmp /= 2;
        height_tmp /= 2;
        scale *= 2;
    }

    // Decode with inSampleSize
    BitmapFactory.Options o2 = new BitmapFactory.Options();
    o2.inSampleSize = scale;
    return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

}

public void setLevelParameters(int difficulty){
	
	fappLevels = new fappLevel[4];

	switch(difficulty){
		case 1:
			fappLevels[0] = new fappLevel(100,1,5,120,5,5,0);
			fappLevels[1] = new fappLevel(200,1,10,120,5,5,5);
			fappLevels[2] = new fappLevel(300,1,20,120,5,5,5);
			fappLevels[3] = new fappLevel(400,1,40,120,5,5,5);
			break;
		case 2:
			fappLevels[0] = new fappLevel(100,1,5,60,10,10,0);
			fappLevels[1] = new fappLevel(200,2,10,65,10,20,10);
			fappLevels[2] = new fappLevel(300,4,20,67,10,30,10);
			fappLevels[3] = new fappLevel(400,5,40,70,10,40,10);
			break;
		case 3:
			fappLevels[0] = new fappLevel(100,1,5,60,15,20,0);
			fappLevels[1] = new fappLevel(200,2,10,65,15,30,15);
			fappLevels[2] = new fappLevel(300,4,20,67,15,40,15);
			fappLevels[3] = new fappLevel(400,5,40,70,15,50,15);
			break;
		case 4:
			fappLevels[0] = new fappLevel(100,1,5,60,20,30,0);
			fappLevels[1] = new fappLevel(200,2,10,65,20,40,20);
			fappLevels[2] = new fappLevel(300,4,20,67,20,50,20);
			fappLevels[3] = new fappLevel(400,5,40,70,20,60,20);
			break;
		case 5:
			fappLevels[0] = new fappLevel(100,1,5,60,25,40,0);
			fappLevels[1] = new fappLevel(200,2,10,65,25,50,25);
			fappLevels[2] = new fappLevel(300,4,20,67,25,60,25);
			fappLevels[3] = new fappLevel(400,5,40,70,25,100,25);
			break;
	}
	Log.i("level","difficulty: "+String.valueOf(difficulty));
	
}

// Invoke displayInterstitial() when you are ready to display an interstitial.
public void displayInterstitial() {
  if (interstitial.isLoaded()) {
    interstitial.show();
    Log.i("Ads_greg","display interst - loaded");
  }
  else {Log.i("Ads_greg","display interst - not loaded");}
}

public void sendAnalyticsEvent (String category, String action, String label, long value){
	// Get tracker.
    Tracker t = ((fApplication) this.getApplication()).getTracker(
        TrackerName.APP_TRACKER);
    // Build and send an Event.
    t.send(new HitBuilders.EventBuilder()
        .setCategory(category)
        .setAction(action)
        .setLabel(label)
        .setValue(value)
        .build());

}

}