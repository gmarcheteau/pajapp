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
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeScreen extends FragmentActivity {
	
	public MediaPlayer mp;
	
	private DrawerLayout mDrawerLayout;
	
	private HomeScreenFragment HomeScreenFragment;
	private RightDrawerFragment RightDrawerFragment;
	private LeftDrawerFragment LeftDrawerFragment;
	
	public ImageButton buttonLevel1,buttonLevel2,buttonLevel3,buttonLevel4;	
	
	//default level 3 but should be taken from user prefs file
	private int userLevel=3;
		
	
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
    	buttonLevel4 = (ImageButton) findViewById(R.id.buttonLevel4);
    	setButtonLevelState();
        //start sound
        mp = MediaPlayer.create(this, R.raw.zipp);
        
}

	@Override
    public void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawers();
	}
	public void onClick(View view){
		 switch(view.getId()){
			case R.id.settingsButton:
				mDrawerLayout.openDrawer(Gravity.RIGHT);
				break;	
			case R.id.shareButtonCredits:
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
				startPaja(1);
				break;
			case R.id.buttonLevel1:
				startPaja(1);
				break;
			case R.id.buttonLevel2:
				startPaja(2);
				break;
			case R.id.buttonLevel3:
				startPaja(3);
				break;
			case R.id.buttonLevel4:
				startPaja(4);
				break;
			case R.id.buttonLevel5:
				startPaja(5);
				break;
	 }
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
	
}
	
