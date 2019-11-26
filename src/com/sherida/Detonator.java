package com.sherida;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Renan - a robot by (your name here)
 */
public class Detonator extends AdvancedRobot {
	private int rightDirectionCounter = 0;
	private int leftDirectionCounter = 0;
	private int hitWallCounter = 0;
	private int hitRobotCounter = 0;

	/**
	 * run: Renan's default behavior
	 */
	public void run() {

		// Design of the robot
		setBodyColor(Color.blue);
		setGunColor(Color.black);
		setRadarColor(Color.yellow);
		setScanColor(Color.green);
		setBulletColor(Color.yellow);

		setMaxVelocity(Rules.MAX_VELOCITY);
		// Robot main loop
		while (true) {

			executeMovement();

		}
	}

	/**
	 * Execute some random movements.
	 */
	private void executeMovement() {
		
		setAhead(300);
		if (rightDirectionCounter <= 5) {
			setTurnRight(50);
			turnGunLeft(360);
			rightDirectionCounter++;
		} else if (leftDirectionCounter <= 5) {
			setTurnLeft(50);
			turnGunRight(360);
			leftDirectionCounter++;
		} else {
			leftDirectionCounter = 0;
			rightDirectionCounter = 0;
		}
		waitFor(new TurnCompleteCondition(this));
	}

	/**
	 * Ececutes a flip of the actual.
	 */
	private void flipDirection() {
		if (rightDirectionCounter > leftDirectionCounter) {
			rightDirectionCounter = 5;
			leftDirectionCounter = 0;
		} else {
			rightDirectionCounter = 0;
			leftDirectionCounter = 5;
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing
				- getGunHeading());

		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			if (getGunHeat() == 0 && e.getDistance() < 600) {
				if (e.getDistance() <= 400 && getEnergy() > 50) {
					fire(3);
				} else {
					fire(1);
				}
			}
		} else {
			turnGunRight(bearingFromGun);
		}
		// Call the scan if the gun is not turning.
		if (bearingFromGun == 0) {
			scan();
		}

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		if (hitRobotCounter > 2) {
			flipDirection();
			hitRobotCounter = 0;
		}
		setTurnLeft(50);
		setBack(100);

	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {

		if (hitWallCounter > 2) {
			flipDirection();
			hitWallCounter = 0;
		}
		setBack(200);
		setTurnRight(50);
		hitWallCounter++;
	}

	public void onHitRobot(HitRobotEvent e) {
		if (hitRobotCounter > 2) {
			flipDirection();
			hitRobotCounter = 0;
			executeMovement();
		}
	}

	public void onWin(WinEvent e) {
		// Victory dance
		turnRight(36000);
		turnGunLeft(36000);
	}
}