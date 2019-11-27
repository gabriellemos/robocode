package com.sherida;

import java.awt.Color;

import com.sherida.controller.FrankensteinController;
import com.sherida.controller.IRobotController;
import com.sherida.controller.MonteController;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;

public class Sherida extends AdvancedRobot {
	
	private IRobotController monte;
	private IRobotController frankenstein;
	private IRobotController activeController;
	
	@Override
	public void run() {
		monte = new MonteController();
		frankenstein = new FrankensteinController();
		setColors(Color.black, Color.black, Color.black);
		frankenstein.configure(this);
		monte.configure(this);
		
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		activeController = frankenstein;
		
		while(true) {
			if (getOthers() == 1 && activeController instanceof FrankensteinController)
				setColors(Color.white, Color.white, Color.white);
			
			activeController = getOthers() == 1 ? monte : frankenstein;
			activeController.handleMovement();
			activeController.handleAim();
			execute();
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent evt) {
		activeController.handleScan(evt);
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent evt) {
		activeController.handleOnRobotDeath(evt);
	}
	
	@Override
	public void onHitRobot(HitRobotEvent evt) {
		activeController.handleOnHit(evt);
	}
	
	@Override
	public void onHitByBullet(HitByBulletEvent evt) {
		activeController.handleOnHit(evt);
	}
	
	@Override
	public void onHitWall(HitWallEvent evt) {	
		activeController.handleOnHit(evt);
	}
	
	@Override
	public void onDeath(DeathEvent evt) {
		frankenstein.handleOnDeath(evt);
		monte.handleOnDeath(evt);
	}
	
	@Override
	public void onRoundEnded(RoundEndedEvent evt) {
		frankenstein.handleOnRoundEnded(evt);
		monte.handleOnRoundEnded(evt);
	}

}
