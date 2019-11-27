package com.sherida.controller;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
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
	public void handleOnDeath(DeathEvent evt);
	public void handleOnRoundEnded(RoundEndedEvent evt);
	
}
