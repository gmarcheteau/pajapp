package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {


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
	public long lastSensorUpdate = 0;
	public long delay =0;
	public long minDelay = 10;
	
	/**
	 * PajaMetrics
	 */
	public boolean RIGHT_HAND=true;
	public boolean IS_DOWN;
	public int distance = 0;
	public int targetDistance = 60;
	public int countSlow=1;
	public int countFast=1;
	public int countVeryFast=1;
	
	/**TIMING 
	 */
	public boolean PAJA_STARTED = false;
	public long startTime;
	public long duration;
	public long lastChange;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
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
			//action
			changeHands();
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
	    distance=0;
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
		
		// check last sensor update action
		delay=System.currentTimeMillis()-lastSensorUpdate;
		View back= findViewById(R.id.relLayout);
		View hand = findViewById(R.id.imageView1);
		TextView dist = (TextView) findViewById(R.id.textViewDistance);
		TextView slow = (TextView) findViewById(R.id.TextViewSlow);
		TextView fast = (TextView) findViewById(R.id.TextViewFast);
		
		//if Paja is completed
		if(distance==targetDistance){
			endPaja(); //should pass parameters (paja score, etc)
		}
		
		else{
		
			if(delay>minDelay){
				//UP
				if(event.values[1]<sensorSensitivity){
							//hand up
					hand.scrollTo(0, 150);
					lastSensorUpdate=System.currentTimeMillis();
					IS_DOWN=false;
						}
				else{
					//hand down
					if(!IS_DOWN){
						hand.scrollTo(0, -150);
						distance++;
						dist.setText(Integer.toString(distance));
						IS_DOWN=true;
						lastSensorUpdate=System.currentTimeMillis();
						
						//rythm
						long timeSinceLastChange=System.currentTimeMillis()-lastChange;
						
						if(distance>3 && timeSinceLastChange<180){ //VERY FAST
							fast.setVisibility(View.INVISIBLE);
							slow.setVisibility(View.INVISIBLE);
							back.setBackgroundColor(getResources().getColor(R.color.bigRed));
							countVeryFast++;}
						else if(distance>3 && timeSinceLastChange<300){ //FAST
							fast.setVisibility(View.VISIBLE);
							slow.setVisibility(View.INVISIBLE);
							back.setBackgroundColor(getResources().getColor(R.color.bigWhite));
							countFast++;}
						else if(distance>3 && timeSinceLastChange>450){ //SLOW
							slow.setVisibility(View.VISIBLE);
							fast.setVisibility(View.INVISIBLE);
							back.setBackgroundColor(getResources().getColor(R.color.bigBlue));
							countSlow++;}
						else {back.setBackgroundColor(getResources().getColor(R.color.bigWhite));}
						
						
						lastChange=System.currentTimeMillis();
						}
				 	}
				
			}
		}
	}
			   
    public void changeHands(){
    	View hand = findViewById(R.id.imageView1);
    	ImageView iv = (ImageView) hand;
    	if(RIGHT_HAND){
    		iv.setImageResource(R.drawable.openhandleft);}
    	else
    		{iv.setImageResource(R.drawable.openhandright);}
    	
    	RIGHT_HAND=!RIGHT_HAND;
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
}
	

