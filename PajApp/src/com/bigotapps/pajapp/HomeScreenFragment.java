package com.bigotapps.pajapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeScreenFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
		ViewGroup container, 
	    Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_credits, container, false);	  
	    return view;
	}	
}
