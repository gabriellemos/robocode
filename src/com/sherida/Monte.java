package com.sherida;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

/**
 * @author Renan
 *
 */
public class Monte extends AdvancedRobot{
	int moveDirection=1;//which way to move
	@Override
	public void run() {
		paint();
		
		while(true) {
			setTurnRadarLeft(360);
			ahead(getBattleFieldWidth());
			execute();
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		//setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
		//execute();	
	//	setTurnGunRight(normalizeBearing(getHeading() - getGunHeading() + e.getBearing()));		
	//	smartFire(e);		
		//execute();
		//setTurnRight(getGunHeading());	
		//setAhead(e.getDistance() -20);
		
		double absBearing=e.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
		double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//enemies later velocity
		double gunTurnAmt;//amount to turn our gun
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
		if(Math.random()>.9){
			setMaxVelocity((12*Math.random())+12);//randomly change speed
		}
		if (e.getDistance() > 150) {//if distance is greater than 150
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//amount to turn our gun, lead just a little bit
			setTurnGunRightRadians(gunTurnAmt); //turn our gun
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
			setAhead((e.getDistance() - 140)*moveDirection);//move forward
			smartFire(e);//fire
		}
		else{//if we are close enough...
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//amount to turn our gun, lead just a little bit
			setTurnGunRightRadians(gunTurnAmt);//turn our gun
			setTurnLeft(-90-e.getBearing()); //turn perpendicular to the enemy
			setAhead((e.getDistance() - 140)*moveDirection);//move forward
			smartFire(e);//fire
		}	
		
	}
	
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	
	@Override
	public void onHitRobot(HitRobotEvent event) {
		back(20);
	}
	
	/**
	 * @param e
	 */
	private void smartFire(ScannedRobotEvent e) {
		if(Math.abs(getGunTurnRemaining()) < 10) {
			if(e.getDistance() > 500 ) {
				fire(0.5);
			}else if(e.getDistance() > 400) {
				fire(2);
			}else {
				fire(3);
			}				
		}
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		turnLeft(90);		
	}

	/**
	 * 
	 */
	private void paint() {
		setScanColor(Color.WHITE);
		setBodyColor(Color.BLACK);
		setRadarColor(Color.YELLOW);		
	}
}