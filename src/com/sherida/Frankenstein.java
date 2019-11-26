package com.sherida;

import robocode.*;
import robocode.util.Utils;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

public class Frankenstein extends AdvancedRobot {

	private EnemyBot target;
	private Map<String, EnemyBot> enemies;
	private Rectangle2D.Double battleField;

	private Point2D.Double myPos;
	private Point2D.Double lastPosition;
	private Point2D.Double nextDestination;
	
	public void init() {
		target = new EnemyBot();
		enemies = new HashMap<>();
		double width = getBattleFieldWidth();
		double height = getBattleFieldHeight();
		
		battleField = new Rectangle2D.Double(30, 30, width - 60, height - 60);
		nextDestination = lastPosition = myPos = new Point2D.Double(getX(), getY());
	}
	
	@Override
	public void run() {
		init();
		
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
		if ("".equals(target.name)) {
			setTurnRight(5);
			setAhead(20);
			return;
		}

		myPos = new Point2D.Double(getX(),getY());
		double distanceToTarget = myPos.distance(target.pos);
		double distanceToNextDestination = myPos.distance(nextDestination);

		if(distanceToNextDestination < 15) {
			generateNewTargetLocation(distanceToTarget);
		} else {
			double direction = 1;
			double angle = MyUtils.calcAngle(nextDestination, myPos) - getHeadingRadians();

			if(Math.cos(angle) < 0) {
				angle += Math.PI;
				direction = -1;
			}

			setAhead(distanceToNextDestination * direction);
			setTurnRightRadians(angle = Utils.normalRelativeAngle(angle));
			// hitting walls isn't a good idea, but HawkOnFire still does it pretty often
			setMaxVelocity(Math.abs(angle) > 1 ? 0 : 8d);
		}
	}
	
	private void generateNewTargetLocation(double distanceToTarget) {
		Point2D.Double destination = null;
		double addLast = 1 - Math.rint(Math.pow(Math.random(), getOthers()));
		for (int i = 0; i < 200; i++) {
			Point2D.Double testPoint = MyUtils.calcPoint(myPos, Math.min(distanceToTarget * 0.8, 100 + 200 * Math.random()));
			if (!battleField.contains(testPoint)) continue;
			
			if (destination == null) {
				destination = testPoint;
			} else if (evaluate(testPoint, addLast) < evaluate(destination, addLast)) {
				nextDestination = testPoint;
			}
		}
		if (destination != null) {
			nextDestination = destination;
		}
		lastPosition = myPos;
	}

	private double evaluate(Point2D.Double point, double addLast) {
		// this is basically here that the bot uses more space on the battlefield. In melee it is dangerous to stay somewhere too long.
		double eval = addLast * 0.08 / point.distanceSq(lastPosition);
 
 		for (EnemyBot bot : enemies.values()) {
 			if (bot.energy <= 0) continue;
 			
 			double energyFactor = Math.min(bot.energy / getEnergy(), 2);
 			double angleBetweenBots = MyUtils.calcAngle(myPos, point) - MyUtils.calcAngle(bot.pos, point);
 			eval += energyFactor * (1 + Math.abs(angleBetweenBots)) / point.distanceSq(bot.pos);
 		}
		return eval;
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