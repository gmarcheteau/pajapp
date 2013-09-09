package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
	public float progressGoal=30;
	public int freqCoeff=1;
	public float avFreq=1;
	
	/**TIMING 
	 */
	public boolean PAJA_STARTED = false;
	public long startTime;
	public long duration=1;
	public long lastChange=1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		  super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	
		if(this.getIntent().getExtras() != null){
			//when user is coming from deep-linking
			String message=this.getIntent().getExtras().getString("message");
			quickToast(message);
		}
		
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
		View back= findViewById(R.id.relLayout);
		View hand = findViewById(R.id.imageView1);
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

						} 	
				} //end of hand down
			}
				else endPaja(); //Paja is ended, score computed etc.	
				
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
    
    //endPaja: aquí tenemos que pasar más parámetros para temas de achievements etc.
    public void endPaja(){
		
		mSensorManager.unregisterListener(this);
		duration=(System.currentTimeMillis()-startTime)/1000;
    	Intent i = new Intent(getApplicationContext(),PajaCompleted.class);
		i.putExtra("score", String.valueOf(getScore()));
		i.putExtra("duration", String.valueOf(duration));
		i.putExtra("golpes", String;valueOf(golpes));
		startActivity(i);
    }
    
 public void goSettings(){
		
		mSensorManager.unregisterListener(this);
		
    	Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
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
	

