package com.bigotapps.pajapp;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class WallOfFame extends Activity {
	
	Long currentScore;
	Long topScore;
	String duration;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall_of_fame);
		
		//shared preferences, to store Best Scores and stuff
				SharedPreferences prefs = this.getSharedPreferences(
					      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		
		//get intent parameters
		if(this.getIntent().getExtras() != null){
			currentScore = Long.valueOf(this.getIntent().getExtras().getString("score"));
			duration = this.getIntent().getExtras().getString("duration");
			}
		
		//retrieve topScore
		topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
		//update topScore if relevant
	 	
		if(currentScore>topScore){
	 		topScore=updateTopScore(prefs, currentScore);
	 		quickToast("NEW TOP SCORE!");
	 	}
	 	
	 	//update values being displayed
		TextView currentScoreView = (TextView) findViewById(R.id.TextViewCurrentScore);
		currentScoreView.setText(currentScore+" pts");
		TextView topScoreView = (TextView) findViewById(R.id.TextViewTopScore);
		topScoreView.setText(topScore+" pts");
}
	
	public Long updateTopScore(SharedPreferences prefs, Long newTopScore){
		prefs.edit().putLong("com.bigotapps.pajapp.topscore", newTopScore).commit();
		return newTopScore;
	}
	
	
 /* QuickToaster
 * for runtime debugging etc 
 */
	public void quickToast (String message){
		 Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
	 }
}

	


