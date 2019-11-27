package com.sherida.controller;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public interface IRobotController {

	public void configure(AdvancedRobot robot);
	public void handleMovement();
	public void handleAim();
	public void handleScan(ScannedRobotEvent evt);
	public void handleOnRobotDeath(RobotDeathEvent evt);
	public void handleOnHit(HitByBulletEvent evt);
	public void handleOnHit(HitRobotEvent evt);
	public void handleOnHit(HitWallEvent evt);
	
}
