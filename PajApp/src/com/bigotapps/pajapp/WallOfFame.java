package com.bigotapps.pajapp;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class WallOfFame extends Activity {
	
	Long currentScore;
	Long topScore;
	String duration;
	boolean badgeScore_unlocked=false;
    boolean badgeDuration_unlocked=false;
    boolean badgeGolpes_unlocked=false;
    boolean badgeShare_unlocked=false;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall_of_fame);
		
		//shared preferences, to store Best Scores and stuff
				SharedPreferences prefs = this.getSharedPreferences(
					      "com.bigotapps.pajapp", Context.MODE_PRIVATE);
		
		
		//retrieve all user datetopScore
		fetchUserData(prefs);
		
	 	
	 	//update values being displayed
	/*	TextView currentScoreView = (TextView) findViewById(R.id.TextViewCurrentScore);
		currentScoreView.setText(currentScore+" pts");
		TextView topScoreView = (TextView) findViewById(R.id.TextViewTopScore);
		topScoreView.setText(topScore+" pts");
	*/
		//adjust visibility of badges
		ImageView badgeDuration = (ImageView) findViewById(R.id.ImageBadgeDuration);
		ImageView badgeScore = (ImageView) findViewById(R.id.ImageBadgeScore);
		ImageView badgeGolpes = (ImageView) findViewById(R.id.ImageBadgeGolpes);
		ImageView badgeShare = (ImageView) findViewById(R.id.ImageBadgeShare);
		
		if(!badgeScore_unlocked){
			badgeScore.setVisibility(View.INVISIBLE);
		}
		if(!badgeDuration_unlocked){
			badgeDuration.setVisibility(View.INVISIBLE);
		}
		if(!badgeGolpes_unlocked){
			badgeGolpes.setVisibility(View.INVISIBLE);
		}
		if(!badgeShare_unlocked){
			badgeShare.setVisibility(View.INVISIBLE);	
		}
		
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

public void fetchUserData(SharedPreferences prefs){
	topScore = prefs.getLong("com.bigotapps.pajapp.topscore", 0);
	badgeScore_unlocked=prefs.getBoolean("com.bigotapps.pajapp.badgeScore_unlocked", false);
	badgeDuration_unlocked=prefs.getBoolean("com.bigotapps.pajapp.badgeDuration_unlocked", false);
	badgeGolpes_unlocked=prefs.getBoolean("com.bigotapps.pajapp.badgeGolpes_unlocked", false);
	badgeShare_unlocked=prefs.getBoolean("com.bigotapps.pajapp.badgeShare_unlocked", false);
}
}

	


