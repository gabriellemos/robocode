package com.sherida;

import robocode.*;
import java.util.Map;

import com.sherida.movimentation.MinimumRiskMovement;

import java.util.HashMap;
import java.awt.geom.Point2D;
import java.awt.Color;

public class Frankenstein extends AdvancedRobot {

	private EnemyBot target;
	private Map<String, EnemyBot> enemies;

	private Point2D.Double myPos;
	
	private MinimumRiskMovement mrMoviment;
	
	@Override
	public void run() {
		target = new EnemyBot();
		enemies = new HashMap<>();
		
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		setColors(Color.black, Color.black, Color.black);

		while(true) {
			setTurnRadarRight(360);
			shootLogic();
			movimentationLogic();
			execute();
		}
	}

	private void movimentationLogic() {
		myPos = new Point2D.Double(getX(), getY());
		if ("".equals(target.name)) {
			setTurnRight(5);
			setAhead(20);
			return;
		}

		if (mrMoviment == null) {
			mrMoviment = new MinimumRiskMovement(this);
		}
		mrMoviment.move(enemies.values());
	}

	private void shootLogic() {
		if ("".equals(target.name)) return;

		double firePower = calculateFirePower();
		double bulletSpeed = 20 - firePower * 3;

		long time = (long)(target.distance / bulletSpeed);

		// Calculando rotacao do canhão para as posicoes previstas (x,y)
		double absDeg = MyUtils.absoluteBearing(getX(), getY(), 
			target.getFutureX(time), target.getFutureY(time));

		setTurnGunRight(MyUtils.normalizeBearing(absDeg - getGunHeading()));
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	}
	
	private double calculateFirePower() {
		if (target.velocity == 0) return 3;
		else return Math.min(500 / target.distance, 3);
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent evt) {
		EnemyBot enemyBot = (EnemyBot) enemies.get(evt.getName());
		
		if (enemyBot == null) {
			enemyBot = new EnemyBot();
			enemies.put(evt.getName(), enemyBot);
		}

		enemyBot.update(evt, this);
		enemyBot.pos = MyUtils.calcPoint(myPos, evt.getDistance(), 
			getHeadingRadians() + evt.getBearingRadians());
		
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
	public void onRobotDeath(RobotDeathEvent evt) {
		enemies.remove(evt.getName());
		target.reset();
	}
	
	@Override
	public void onHitRobot(HitRobotEvent evt) {
		EnemyBot enemyBot = (EnemyBot) enemies.get(evt.getName());
		if (enemyBot != null) target = enemyBot;
	}
}