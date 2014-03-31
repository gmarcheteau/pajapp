package com.bigotapps.pajapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;

public class FBShare extends FragmentActivity {
	
	private FBLoginFragment FBFragment;
	private FBShare2 FBFragment2;
	private static final String TAG = "FBLoginFragment";
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	 
	    setContentView(R.layout.fragment_fbshare2);
	    
	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	    	FBFragment = new FBLoginFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(R.id.fragcontainerlayout, FBFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	    	FBFragment = (FBLoginFragment) getSupportFragmentManager()
	        .findFragmentById(R.id.fragcontainerlayout);
	    }
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.bigotapps.pajapp", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	}
	
	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("GregBug", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("GregBug", "Success!");
	        }
	    });
	}
	
	public void fbShare(View v){
		Toast.makeText(this,"activity - is it sharing?", Toast.LENGTH_SHORT).show();
		
		OpenGraphObject fapp = OpenGraphObject.Factory.createForPost("pajappbeta:fapp");
		fapp.setType("fapp");
		fapp.setTitle("Fapp");
		fapp.getData().setProperty("duration", "29 min");
		fapp.getData().setProperty("score", "6578 pts");
		String url="http://www.nolandalla.com/wp-content/uploads/2014/01/clenched-fist.jpg";
		fapp.setImageUrls(Arrays.asList(url));
		//deep linking
		
		
		OpenGraphAction action = OpenGraphAction.Factory.createForPost("pajappbeta:perform");
		action.setProperty("fapp", fapp);

		FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(this, action,"fapp")
			.build();
		uiHelper.trackPendingDialogCall(shareDialog.present());
		
		}
	 
}
