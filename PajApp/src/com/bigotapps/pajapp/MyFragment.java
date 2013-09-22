package com.bigotapps.pajapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MyFragment extends Fragment{
	
	int mCurrentPage;
	int[] BadgeNames = {R.string.badge1_name,R.string.badge2_name,R.string.badge3_name,R.string.badge4_name};
	int[] BadgeDesc = {R.string.badge1_desc,R.string.badge2_desc,R.string.badge3_desc,R.string.badge4_desc};
	int[] BadgeImages = {R.drawable.badge_duration,R.drawable.badge_golpes,R.drawable.badge_share,R.drawable.badge_fire};
	float LOCKED_BADGES_TRANSPARENCY = 0.5f;
	SharedPreferences prefs;
	boolean[] BadgesUnlocked = {false,false,false,false};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /** Getting the shared prefs (status of badges) */
        prefs = this.getActivity().getSharedPreferences("com.bigotapps.pajapp", Context.MODE_PRIVATE);
        BadgesUnlocked[0] = prefs.getBoolean("com.bigotapps.pajapp.badgeDuration_unlocked", false);
        BadgesUnlocked[1] = prefs.getBoolean("com.bigotapps.pajapp.badgeGolpes_unlocked", false);
        BadgesUnlocked[2] = prefs.getBoolean("com.bigotapps.pajapp.badgeShare_unlocked", false);
        BadgesUnlocked[3] = prefs.getBoolean("com.bigotapps.pajapp.badgeScore_unlocked", false);
        
		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();
		
		/** Getting integer data of the key current_page from the bundle */
		mCurrentPage = data.getInt("current_page", 0);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.badge_detail, container,false);				
	
		//fill badge image
		ImageView bview = (ImageView) v.findViewById(R.id.badgeView);
		bview.setImageResource(BadgeImages[mCurrentPage-1]); //to be done correctly
		bview.setAlpha(LOCKED_BADGES_TRANSPARENCY); //shadowed unless unlocked
	
		//fill badge name
		TextView name = (TextView ) v.findViewById(R.id.badgeName);
		name.setText(BadgeNames[mCurrentPage-1]);
		
		//fill badge description
		TextView desc = (TextView ) v.findViewById(R.id.badgeDesc);
		desc.setText(BadgeDesc[mCurrentPage-1]);	
		
		//adapt view if badge not unlocked
		if(BadgesUnlocked[mCurrentPage-1]){
			TextView unlock = (TextView ) v.findViewById(R.id.textUnlockBadge);
			unlock.setVisibility(View.INVISIBLE);
			bview.setAlpha(1f);
		}
		
		return v;
	}

}
