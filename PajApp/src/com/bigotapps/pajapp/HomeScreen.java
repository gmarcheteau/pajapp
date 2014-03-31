package com.bigotapps.pajapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeScreen extends FragmentActivity {
	
	public MediaPlayer mp;
	
	private DrawerLayout mDrawerLayout;
	
	private HomeScreenFragment HomeScreenFragment;
	private RightDrawerFragment RightDrawerFragment;
	private LeftDrawerFragment LeftDrawerFragment;
	
	public ImageButton buttonLevel1,buttonLevel2,buttonLevel3,buttonLevel4;	
	private int userLevel=1;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.i("GregBug","Drawer State Changed");
			}
        }); //end of DrawerListener
        
        //set Level Button states
        buttonLevel1 = (ImageButton) findViewById(R.id.buttonLevel1);
    	buttonLevel2 = (ImageButton) findViewById(R.id.buttonLevel2);
    	buttonLevel3 = (ImageButton) findViewById(R.id.buttonLevel3);
    	buttonLevel4 = (ImageButton) findViewById(R.id.buttonLevel4);
    	setButtonLevelState();
        //start sound
        mp = MediaPlayer.create(this, R.raw.zipp);
        
}


	public void onClick(View view){
		 switch(view.getId()){
			case R.id.arcadeButton:
				mp.release();
				startPaja();
				break;
			case R.id.settingsButton:
				mp.release();
				mDrawerLayout.openDrawer(Gravity.RIGHT);
				break;	
			case R.id.shareButtonCredits:
				mp.release();
				//share();
				mDrawerLayout.openDrawer(Gravity.LEFT);
				break;
			case R.id.buttonCloseLeft:
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			case R.id.buttonCloseRight:
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				break;
			case R.id.buttonLevel1:
				mp.release();
				startPaja();
				break;
			case R.id.buttonLevel2:
				mp.release();
				startPaja();
				break;
	 }
}
	
	
	 public void startPaja(){
		//Toast.makeText(this, "START PAJA", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this,com.bigotapps.pajapp.MainActivity.class);
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
			startActivity(i);
	 }
	 
	public void setButtonLevelState(){
		//no se pueden modificar los botones desde aqu’?
		//que est‡n en el fragment
	}
	 
}
	
