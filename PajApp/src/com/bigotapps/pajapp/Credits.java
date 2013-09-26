package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Credits extends Activity implements View.OnLongClickListener {

	MediaPlayer mp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        setContentView(R.layout.activity_credits);
	        mp = MediaPlayer.create(getApplicationContext(), R.raw.zipp);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void anim(View view){
		
		//mp.start();
		ImageView animationTarget = (ImageView) view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
	    Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);
	    playSound(1.5f);
		
	}
	public void playSound(float speed){
		
	final float playbackSpeed=speed;
	final SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

	final Integer soundId = soundPool.load(getApplicationContext(), R.raw.a_short, 1);
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
	
	@Override
	public boolean onLongClick(View view){
		ImageView animationTarget = (ImageView) view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);
	    playSound(2f);
	    return true;
}
	
}
