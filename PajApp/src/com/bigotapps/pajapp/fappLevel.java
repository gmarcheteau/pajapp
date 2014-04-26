package com.bigotapps.pajapp;

public class fappLevel {
	private int progressGoal;
	private int decaySpeed;
	private int progressPainPenalty;
	private int painThreshold;
	private int scoreGolpe;
	private int scorePainPenalty;
	private int scoreLevelDownPenalty;
	
	public fappLevel(int progress_goal, int decay_speed, int progress_pain_penalty, int pain_threshold, int score_pain_penalty, int score_golpe, int score_level_down_penalty){
		progressGoal=progress_goal;
		decaySpeed=decay_speed;
		progressPainPenalty=progress_pain_penalty;
		painThreshold=pain_threshold;
		scoreGolpe=score_golpe;
		scorePainPenalty=score_pain_penalty;
		scoreLevelDownPenalty=score_level_down_penalty;
	}
	
	public int getProgressGoal(){
		return progressGoal;
	}
	
	public int getDecaySpeed(){
		return decaySpeed;
	}
	
	public int getProgressPainPenalty(){
		return progressPainPenalty;
	}
	
	public int getPainThreshold(){
		return painThreshold;
	}
	
	public int getScoreGolpe(){
		return scoreGolpe;
	}
	
	public int getScorePainPenalty(){
		return scorePainPenalty;
	}
	
	public int getScoreLevelDownPenalty(){
		return scoreLevelDownPenalty;
	}
	

}
