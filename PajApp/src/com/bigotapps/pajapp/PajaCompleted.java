package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PajaCompleted extends Activity {

	public long[] pattern = {10,1000,100,1000,100,1000};
	String score;
	String duration;

	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paja_completed);
		

		
		
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 
		// Vibrate according to pattern (-1 means don't repeat)
		v.vibrate(pattern,-1);

		
		playSound();
		
		if(this.getIntent().getExtras() != null){
			//use paja parameters
			score = this.getIntent().getExtras().getString("score");
			duration = this.getIntent().getExtras().getString("duration");
			TextView scoreView = (TextView) findViewById(R.id.textViewScore);
			scoreView.setText(score+" pts");
			quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
		}
		
	}
	
	public void playSound(){
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.open);
		mp.start();
	}
	
	public void sharePaja(View v){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Te env’an una paja");
		intent.putExtra(Intent.EXTRA_TEXT, "Hola,\n \nUn amigo quiere compartir contigo la siguiente paja: \n \nDuraci—n: "+duration+"s \nScore: "+score+" pts");
		startActivity(Intent.createChooser(intent, "Send Email"));
	}
	
	 public void newPaja(View v){
	    	Intent i = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
	    }
	 
	 public void WoF(View v){
		 	Intent i = new Intent(getApplicationContext(),WallOfFame.class);
		 	i.putExtra("score", score);
		 	i.putExtra("duration", duration);
		 	//quickToast("Paja score: "+ score +"\n Duration: " + duration + "s");
		 	startActivity(i);
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

