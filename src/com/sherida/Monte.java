package com.sherida;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

/**
 * @author Renan
 *
 */
public class Monte extends AdvancedRobot {
	int moveDirection = 1;// which way to move
	boolean modeSurvive;

	boolean peek; // Don't turn if there's a robot there
	double moveAmount; // How much to move

	@Override
	public void run() {
		paint();
		
		while (true) {
			isSurvive();
			setTurnRadarLeft(Double.POSITIVE_INFINITY);
			ahead(getBattleFieldWidth() * moveDirection);
			execute();
		}
	}
	private void isSurvive() {
		modeSurvive = getOthers() > 2;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		modeKill(e);
	}

	private void modeKill(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();// enemies absolute bearing
		double latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);// enemies later velocity
		double gunTurnAmt;// amount to turn our gun
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());// lock on the radar
		int maxDistance = 250;
		if (e.getDistance() > maxDistance) {// if distance is greater than 150
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 22);// amount
			setTurnGunRightRadians(gunTurnAmt); // turn our gun
			setTurnRightRadians(
					robocode.util.Utils.normalRelativeAngle(absBearing - getHeadingRadians() + latVel / getVelocity()));// drive
			setAhead((e.getDistance() - (maxDistance - 10)) * moveDirection);// move forward
			smartFire(e);// fire
		} else {// if we are close enough...
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 15);
			setTurnGunRightRadians(gunTurnAmt);// turn our gun
			setTurnLeft(-90 - e.getBearing()); // turn perpendicular to the enemy
			setAhead((e.getDistance() - (maxDistance - 10)) * moveDirection);// move forward
			smartFire(e);// fire
		}

	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		 back(50);
	}

	double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	@Override
	public void onHitRobot(HitRobotEvent e) {
			back(50);		
	}

	/**
	 * @param e
	 */
	private void smartFire(ScannedRobotEvent e) {
		if (Math.abs(getGunTurnRemaining()) < 10) {
			if (e.getDistance() > 400) {
				fire(1);
			} else if (e.getDistance() > 200) {
				fire(2);
			} else {
				fire(3);
			}
		}
	}

	@Override
	public void onHitWall(HitWallEvent event) {	
			moveDirection *= -1;
	}

	/**
	 * 
	 */
	private void paint() {
		setScanColor(Color.WHITE);
		setBodyColor(Color.WHITE);
		setRadarColor(Color.darkGray);
	}
}