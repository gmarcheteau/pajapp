package com.bigotapps.pajapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class pajaMetricsFragment extends Fragment {

	public String score, golpes, pain;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        if(getArguments() != null){
	        	score=getArguments().getString("score");
	        	golpes=getArguments().getString("golpes");
	        	pain=getArguments().getString("pain");
	        }
	        else{
	        	score="no score";
	        	golpes="no golpes";
	        	pain="no pain";
	        }
	    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
		ViewGroup container, 
	    Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.paja_metrics, container, false);
	    
	    TextView scoreView = (TextView) view.findViewById(R.id.pajaMetrics_score);
	    scoreView.setText(score);
        TextView golpesView = (TextView) view.findViewById(R.id.pajaMetrics_golpes);
        golpesView.setText(golpes);
        TextView painView = (TextView) view.findViewById(R.id.pajaMetricsPain);
        painView.setText(pain);
	    
	    Log.i("frag",score + " "+golpes+" "+pain);
	    
	    return view;
	}
	 
}
