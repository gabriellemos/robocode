package com.sherida.controller;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;

public class MonteController implements IRobotController {

	private int moveDirection;
	private AdvancedRobot robot;
	
	@Override
	public void configure(AdvancedRobot robot) {
		moveDirection = 1; 
		this.robot = robot;
	}

	@Override
	public void handleMovement() {
		robot.setTurnRadarLeft(Double.POSITIVE_INFINITY);
	}

	@Override
	public void handleAim() {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleScan(ScannedRobotEvent evt) {
		double absBearing = evt.getBearingRadians() + robot.getHeadingRadians();// enemies absolute bearing
		double latVel = evt.getVelocity() * Math.sin(evt.getHeadingRadians() - absBearing);// enemies later velocity
		double gunTurnAmt;// amount to turn our gun
		robot.setTurnRadarLeftRadians(robot.getRadarTurnRemainingRadians());// lock on the radar
		int maxDistance = 250;
		if (evt.getDistance() > maxDistance) {// if distance is greater than 150
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - robot.getGunHeadingRadians() + latVel / 22);// amount
			robot.setTurnGunRightRadians(gunTurnAmt); // turn our gun
			robot.setTurnRightRadians(
					robocode.util.Utils.normalRelativeAngle(absBearing - robot.getHeadingRadians() + latVel / robot.getVelocity()));// drive
			robot.setAhead((evt.getDistance() - (maxDistance - 10)) * moveDirection);// move forward
			smartFire(evt);// fire
		} else {// if we are close enough...
			gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - robot.getGunHeadingRadians() + latVel / 15);
			robot.setTurnGunRightRadians(gunTurnAmt);// turn our gun
			robot.setTurnLeft(-90 - evt.getBearing()); // turn perpendicular to the enemy
			robot.setAhead((evt.getDistance() - (maxDistance - 10)) * moveDirection);// move forward
			smartFire(evt);// fire
		}
	}
	
	private void smartFire(ScannedRobotEvent e) {
		if (Math.abs(robot.getGunTurnRemaining()) < 10) {
			if (e.getDistance() > 400) {
				robot.fire(1);
			} else if (e.getDistance() > 200) {
				robot.fire(2);
			} else {
				robot.fire(3);
			}
		}
	}

	@Override
	public void handleOnRobotDeath(RobotDeathEvent evt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleOnHit(HitByBulletEvent evt) {
		robot.back(50);
	}

	@Override
	public void handleOnHit(HitRobotEvent evt) {
		robot.back(50);
	}

	@Override
	public void handleOnHit(HitWallEvent evt) {
		moveDirection *= -1;
	}

	@Override
	public void handleOnDeath(DeathEvent evnt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleOnRoundEnded(RoundEndedEvent evt) {
		// TODO Auto-generated method stub
	}
}
