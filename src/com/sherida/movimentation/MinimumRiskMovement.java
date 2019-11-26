package com.sherida.movimentation;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import com.sherida.EnemyBot;
import com.sherida.MyUtils;

import robocode.AdvancedRobot;
import robocode.util.Utils;

public class MinimumRiskMovement {

	private AdvancedRobot me;
	private Point2D.Double myPos;
	private Point2D.Double lastPosition;
	private Point2D.Double nextDestination;
	private Rectangle2D.Double battleField;
	private Collection<EnemyBot> enemies;
	
	public MinimumRiskMovement(AdvancedRobot robot) {
		double width = robot.getBattleFieldWidth();
		double height = robot.getBattleFieldHeight();
		battleField = new Rectangle2D.Double(30, 30, width - 60, height - 60);
		myPos = lastPosition = nextDestination = new Point2D.Double(robot.getX(), robot.getY());
		me = robot;
	}
	
	public void move(Collection<EnemyBot> enemies) {
		this.enemies = enemies;
		myPos = new Point2D.Double(me.getX(), me.getY());
		if (nextDestination == null) nextDestination = myPos;
		if (myPos.distance(nextDestination) < 15) {
			generateNewTargetLocation();
		} else {
			moveTowards();
		}
	}
	
	private void moveTowards() {
		double direction = 1;
		double distanceToNextDestination = myPos.distance(nextDestination);
		double angle = MyUtils.calcAngle(nextDestination, myPos) - me.getHeadingRadians();

		if(Math.cos(angle) < 0) {
			angle += Math.PI;
			direction = -1;
		}

		me.setAhead(distanceToNextDestination * direction);
		me.setTurnRightRadians(angle = Utils.normalRelativeAngle(angle));
		me.setMaxVelocity(Math.abs(angle) > 1 ? 0 : 8d);
	}
	
	private void generateNewTargetLocation() {
		Point2D.Double destination = null;
		double addLast = 1 - Math.rint(Math.pow(Math.random(), me.getOthers()));
		for (int i = 0; i < 200; i++) {
			Point2D.Double testPoint = MyUtils.calcPoint(myPos, 100 + 200 * Math.random());
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
		double eval = addLast * 0.08 / point.distanceSq(lastPosition);
 
 		for (EnemyBot bot : enemies) {
 			if (bot.energy <= 0) continue;
 			
 			double energyFactor = Math.min(bot.energy / me.getEnergy(), 2);
 			double angleBetweenBots = MyUtils.calcAngle(myPos, point) - MyUtils.calcAngle(bot.pos, point);
 			eval += energyFactor * (1 + Math.abs(angleBetweenBots)) / point.distanceSq(bot.pos);
 		}
		return eval;
	}
	
}
