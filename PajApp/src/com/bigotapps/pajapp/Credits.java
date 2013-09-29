package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Credits extends Activity {

	MediaPlayer mp;
	View logo;
	View scrollview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        setContentView(R.layout.activity_credits);
	       
	        scrollview = findViewById(R.id.horizontalScrollView1);
	        scrollview.setVisibility(View.INVISIBLE);
	        logo = findViewById(R.id.fappLogo);
	        bounce(logo);
	    	        
	        Thread timer = new Thread() { //new thread         
	            public void run() {
	                //Boolean b = true;
	                try {
	                    
	                        sleep(2000);
	                        runOnUiThread(new Runnable() {  
	                        @Override
	                        public void run() {
	                            // TODO Auto-generated method stub
	                            moveup(logo);
	                            fadeIn(scrollview);
	                                          }
	                    });

	                   // while (b == true);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                finally {
	                }
	            };
	        };
	        timer.start();  
	        
	        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void moveup(View view){
		
		//mp.start();
		View animationTarget = view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.moveup);
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);		
	}
	
public void fadeIn(View view){
		
		//mp.start();
		View animationTarget = view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);		
	}
	
	
	public void bounce(View view){
		
		//mp.start();
		View animationTarget = view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.moveup);
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);
	    playSound(0.6f);
	}
	public void playSound(float speed){
		
	final float playbackSpeed=speed;
	final SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

	final Integer soundId = soundPool.load(getApplicationContext(), R.raw.zipp, 1);
	 AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	 final float volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

	 soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
	 {
	     @Override
	     public void onLoadComplete(SoundPool arg0, int arg1, int arg2)
	     {
	         soundPool.play(soundId, volume, volume, 1, 0, playbackSpeed);
	     }
	 });
	
	}
	
	
	public void onClick(View view){
		 switch(view.getId()){
			case R.id.arcadeButton:
				startPaja();
				break;	
			case R.id.settingsButton:
				goSettings();
				break;	
			
	 }
}
	
	
 public void startPaja(){
		   	Intent i = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
	    }
 public void goSettings(){
	   	Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
		startActivity(i);
 }
	
}
