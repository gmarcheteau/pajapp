package com.bigotapps.pajapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

public class FBDialog extends DialogFragment{
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private UiLifecycleHelper uiHelper;
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i("FBDialog", "Logged in...");
	        
	    } else if (state.isClosed()) {
	        Log.i("FBDialog", "Logged out...");
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("FBDialog", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("FBDialog", "Success!");
	            
	            
	        }
	    });
	}
	@Override
	public View onCreateView(LayoutInflater inflater, 
		ViewGroup container, 
	    Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_fbshare, null);
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	    //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
	    authButton.setPublishPermissions("publish_actions");
	    return view;
	}

}
