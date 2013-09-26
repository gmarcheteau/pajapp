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
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {

	private static final int RESULT_LOAD_IMAGE = 100;
	/**
	 * sensor stuff 
	 */
	private SensorManager mSensorManager;
	private Sensor mSensor;
	public int sensorSensitivity=6;
	/**
	 * Minimum delay between sensor readings
	 * To avoid conflicts 
	 */
	public long lastSensorUpdate = 1;
	public long delay =1;
	public float frequency=1;
	public float frequency_previous=1;
	public long minDelay = 10;
	public long timeSinceLastChange=0;
	
	/**
	 * PajaMetrics
	 */
	public boolean RIGHT_HAND=true;
	public boolean IS_DOWN;
	public int golpes = 0;
	public int targetGolpes = 50;
	public int countSlow=1;
	public int countFast=1;
	public int countVeryFast=1;
	public float progress=0;
	public float progressGoal=100;
	public int freqCoeff=1;
	public float avFreq=1;
	
	/**TIMING 
	 */
	public boolean PAJA_STARTED = false;
	public long startTime;
	public long duration=1;
	public long lastChange=1;
	
	/**SOUND
	 */
	SoundPool soundPool;
	Integer soundId;
	AudioManager mgr;
	float volume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		loadSound();
		volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	
		if(this.getIntent().getExtras() != null){
			//when user is coming from deep-linking
			String message=this.getIntent().getExtras().getString("message");
			quickToast(message);
		}
		//initialize pref file if new
		initUser();
		
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
			changeHands();
			return true ;
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
	    golpes=0;
	    mSensorManager.unregisterListener(this);
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
		}
		else{
		// check last sensor update action
		delay=System.currentTimeMillis()-lastSensorUpdate;
		
		//graphic element handlers
		View back= findViewById(R.id.backOfPaja);
		View hand = findViewById(R.id.ImageHand);
		TextView dist = (TextView) findViewById(R.id.textViewProgress);
		TextView slow = (TextView) findViewById(R.id.TextViewSlow);
		TextView fast = (TextView) findViewById(R.id.TextViewFast);
		
			if(delay>minDelay){
				
				//if Paja is not completed
				if(progress<progressGoal){
				
				if(event.values[1]<0){//hand up
					if(IS_DOWN){
						hand.scrollTo(0, 150);
						lastSensorUpdate=System.currentTimeMillis();
						IS_DOWN=false;
						}
					}
				
				else if(event.values[1]>sensorSensitivity){ //hand down
					if(!IS_DOWN){
						hand.scrollTo(0, -150);
						golpes++;
						timeSinceLastChange=System.currentTimeMillis()-lastChange;
						
						frequency=1000/(timeSinceLastChange);//should avoid dividing by 0
						progress=getProgress(); 
						
						dist.setText(Integer.toString(Math.round(progress)));
						IS_DOWN=true;
						lastSensorUpdate=System.currentTimeMillis();
						
						//rythm. Still being used to compute the QualityScore
										
						if(golpes>3 && timeSinceLastChange<180){ //VERY FAST
					//		fast.setVisibility(View.INVISIBLE);
					//		slow.setVisibility(View.INVISIBLE);
							//back.setBackgroundColor(getResources().getColor(R.color.bigRed));
							countVeryFast++;}
						else if(golpes>3 && timeSinceLastChange<300){ //FAST
					//		fast.setVisibility(View.VISIBLE);
					//		slow.setVisibility(View.INVISIBLE);
							//back.setBackgroundColor(getResources().getColor(R.color.bigWhite));
							countFast++;}
						else if(golpes>3 && timeSinceLastChange>450){ //SLOW
					//		slow.setVisibility(View.VISIBLE);
					//		fast.setVisibility(View.INVISIBLE);
							//back.setBackgroundColor(getResources().getColor(R.color.bigBlue));
							countSlow++;}
						//else {back.setBackgroundColor(getResources().getColor(R.color.bigWhite));}
						
						//RGB background update
						back.setBackgroundColor(Color.rgb(Math.round(254*progress/progressGoal), 0, 0));
						
						lastChange=System.currentTimeMillis();
							
						playSound(volume,0.6f+getProgress()/200);

						} 	
				} //end of hand down
			}
				else endPaja(); //Paja is ended, score computed etc.	
				
			}
		}
		
		
	}
			   
    public void changeHands(){
    	View hand = findViewById(R.id.ImageHand);
    	ImageView iv = (ImageView) hand;
    	if(RIGHT_HAND){
    		iv.setImageResource(R.drawable.openhandleft);}
    	else
    		{iv.setImageResource(R.drawable.openhandright);}
    	
    	RIGHT_HAND=!RIGHT_HAND;
    }
    
    public float getProgress(){
    	if(duration==0){
    	avFreq=1;
    	}
    	else{
    		duration=(System.currentTimeMillis()-startTime)/1000;
    		avFreq=golpes/(duration+1);
    	}
    	
    	// frequency coeff
    	if(frequency>0.5*avFreq){freqCoeff=+1;} //if acceleration (at least 20%)
    	else if (frequency<avFreq){freqCoeff=-1;} //if deceleration (at least 20%)
    	//else if (freqCoeff=2; //if more or less stable, bonus
    	
    	//quickToast(Float.toString(freqCoeff));
    	
    	if (freqCoeff<0){
    		progress+=freqCoeff*timeSinceLastChange*1/300;
    	}
    	
    	else 
    		{
    		progress+=frequency*freqCoeff*1/10;
    		}
    	progress=Math.max(progress,0);
    	return progress;
    }
    
	public int getScore(){
		float coeff_Slow=3;
		float coeff_Fast=3;
		float coeff_VeryFast=1;
		float coeff_Duration=3;
		return Math.round(
				countSlow*coeff_Slow*
				countFast*coeff_Fast*
				countVeryFast*coeff_VeryFast*
				duration*coeff_Duration
				/100);
	}
    

    public void endPaja(){
		
		mSensorManager.unregisterListener(this);
		duration=(System.currentTimeMillis()-startTime)/1000;
    	Intent i = new Intent(getApplicationContext(),PajaCompleted.class);
		i.putExtra("score", String.valueOf(getScore()));
		i.putExtra("duration", String.valueOf(duration));


		i.putExtra("golpes", String.valueOf(golpes));

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
		i.putExtra("score", String.valueOf(getScore()));
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
public void loadSound(){
	soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	soundId = soundPool.load(getApplicationContext(), R.raw.a_short, 1);
	 mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	 soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
	 {
	     @Override
	     public void onLoadComplete(SoundPool arg0, int arg1, int arg2)
	     {
	         quickToast("loaded");
	         //launch sound
	         playSound(volume,0.6f);
	     }
	 });
	
	}
public void playSound(float volume,float speed){
	soundPool.play(soundId, volume, volume, 1, -1, speed);
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
        ImageView back= (ImageView) findViewById(R.id.backOfPaja);
        //back.setBackgroundResource(R.drawable.badge_fire);
        //back.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        try {
			back.setImageBitmap(decodeUri(selectedImage));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
}