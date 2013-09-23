package com.bigotapps.pajapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Credits extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void anim(View view){
		ImageView animationTarget = (ImageView) view;
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
	    //Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
	    animationTarget.startAnimation(animation);
	}
}
