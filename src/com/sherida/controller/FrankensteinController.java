package com.sherida.controller;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.sherida.EnemyBot;
import com.sherida.MyUtils;
import com.sherida.movimentation.MinimumRiskMovement;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;

public class FrankensteinController implements IRobotController {

	private boolean flag;
	private EnemyBot target;
	private AdvancedRobot robot;
	private Point2D.Double myPos;
	private Map<String, EnemyBot> enemies;
	
	private Timer timer;
	private MinimumRiskMovement mrMoviment;
	
	@Override
	public void configure(AdvancedRobot robot) {
		this.mrMoviment = new MinimumRiskMovement(robot);
		this.enemies = new HashMap<>();
		this.target = new EnemyBot();
		this.robot = robot;
		this.flag = false;
		
		this.timer = new Timer();
	}

	@Override
	public void handleMovement() {
		robot.setTurnRadarRight(360);
		this.myPos = new Point2D.Double(
				robot.getX(), robot.getY());
		
		if (target.none()) {
			robot.setTurnRight(5);
			robot.setAhead(20);
			return;
		}
		
		mrMoviment.move(enemies.values());
	}

	@Override
	public void handleAim() {
		if (target.none()) return;
		
		double firePower = calculateFirePower();
		double bulletSpeed = 20 - firePower * 3;
		
		long time = (long)(target.distance / bulletSpeed);
		
		double absDeg = MyUtils.absoluteBearing(robot.getX(), robot.getY(), 
				target.getFutureX(time), target.getFutureY(time));
		
		robot.setTurnGunRight(MyUtils.normalizeBearing(absDeg - robot.getGunHeading()));
		if (robot.getGunHeat() == 0 && Math.abs(robot.getGunTurnRemaining()) < 10) {
			robot.setFire(firePower);
		}
	}

	private double calculateFirePower() {
		if (target.velocity == 0) return 3;
		else return Math.min(500 / target.distance, 3);
	}
	
	@Override
	public void handleScan(ScannedRobotEvent evt) {
		EnemyBot enemyBot = (EnemyBot) enemies.get(evt.getName());
		
		if (enemyBot == null) {
			enemyBot = new EnemyBot();
			enemies.put(evt.getName(), enemyBot);
		}

		enemyBot.update(evt, robot);
		enemyBot.pos = MyUtils.calcPoint(myPos, evt.getDistance(), 
				robot.getHeadingRadians() + evt.getBearingRadians());
		
		if (target.none()) {
			target = enemyBot;
		} else {
			if (enemyBot.distance < 150 && enemyBot.isCloser(target)
					|| target.hasStoped() && enemyBot.hasStoped() && enemyBot.isCloser(target)
					|| !target.hasStoped() && enemyBot.hasStoped()
					|| enemyBot.isCloser(target)) {
				target = enemyBot;
			}
		}
	}

	@Override
	public void handleOnRobotDeath(RobotDeathEvent evt) {
		enemies.remove(evt.getName());
		target.reset();
	}
	
	@Override
	public void handleOnHit(HitByBulletEvent evt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleOnHit(HitRobotEvent evt) {
		EnemyBot enemyBot = (EnemyBot) enemies.get(evt.getName());
		if (enemyBot != null) target = enemyBot;
//		robot.back(50);
//		
//		if (!flag) {
//			flag = true;
//			timer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					mrMoviment.generateNewTargetLocation();
//					flag = false;
//				}
//			}, 500);
//		}
	}

	@Override
	public void handleOnHit(HitWallEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleOnDeath(DeathEvent evt) {
		timer.cancel();
		timer.purge();
	}

	@Override
	public void handleOnRoundEnded(RoundEndedEvent evt) {
		timer.cancel();
		timer.purge();
	}
}
