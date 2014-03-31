/**
 * 
 */
package com.bigotapps.pajapp;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * @author GregM
 *
 */
public final class FappUtils extends Activity {
	
	public static final void quickToast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	
}
