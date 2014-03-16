package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class Credits extends Activity {

	public MediaPlayer mp;
	View logo;
	View scrollview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        setContentView(R.layout.activity_credits);
	       
	        //scrollview = findViewById(R.id.horizontalScrollView1);
	        //scrollview.setVisibility(View.INVISIBLE);
	        logo = findViewById(R.id.fappLogo);
	        bounce(logo);
	        
	        mp = MediaPlayer.create(this, R.raw.start);
	    	        
	        Thread timer = new Thread() { //new thread         
	            public void run() {

	                //Boolean b = true;
	                try {
	                    	sleep(1000);
	                        runOnUiThread(new Runnable() {  
	                        @Override
	                        public void run() {
	                            // TODO Auto-generated method stub
	                            //moveup(logo);
	                            //fadeIn(scrollview);
	                            mp.start();
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
	
	@Override
	protected void onPause(){
		super.onPause();
		try{
			mp.pause();	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		try{
			mp.start();	
		}
		catch(Exception e){
			e.printStackTrace();
		}
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
	    playSound(R.raw.zipp,0.7f);
	}
	public void playSound(int res,float speed){
		
	final float playbackSpeed=speed;
	final SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

	final Integer soundId = soundPool.load(getApplicationContext(), res, 1);
	 AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	 final float volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

	 soundPool.play(soundId, volume, volume, 1, 0, playbackSpeed);
	
	}
	
	
	
	public void onClick(View view){
		 switch(view.getId()){
			case R.id.arcadeButton:
				playSound(R.raw.staple, 1f);
				mp.release();
				startPaja();
				break;
			case R.id.settingsButton:
				playSound(R.raw.staple, 1f);
				mp.release();
				goSettings();
				break;	
			case R.id.fappButtonCredits:
				playSound(R.raw.staple, 1f);
				mp.release();
				startPaja();
				break;
			case R.id.shareButtonCredits:
				playSound(R.raw.staple, 1f);
				mp.release();
				share();
				break;
			case R.id.fappLogo:
				//playSound(R.raw.staple, 1f);
				bounce(logo);
				break;	
			
	 }
}
	
	
	 public void startPaja(){
		Toast.makeText(this, "START PAJA", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,com.bigotapps.pajapp.MainActivity.class);
		startActivity(i);
		    }
	 public void goSettings(){
		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,com.bigotapps.pajapp.SettingsActivity.class);
		startActivity(i);
 }
	 public void share(){
			Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
			//Intent i = new Intent(this,MainActivity.class);
			//startActivity(i);
			    }
	 
	
}
