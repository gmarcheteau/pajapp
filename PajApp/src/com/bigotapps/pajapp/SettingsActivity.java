package com.bigotapps.pajapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.plus.PlusClient;


public class SettingsActivity extends Activity {
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		
/*		mPlusClient = new PlusClient.Builder(this, this, this)
        .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        .build();
		// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
*/
		
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

	


