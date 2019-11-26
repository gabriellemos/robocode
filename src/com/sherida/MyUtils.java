package com.sherida;

import java.awt.geom.Point2D;

public class MyUtils extends Object {

	// computes the absolute bearing between two points
	public static double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}

	// normalizes a bearing to between +180 and -180
	public static double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}

	public static Point2D.Double calcPoint(Point2D.Double point, double distance) {
		double angle = 2 * Math.PI * Math.random();
		return calcPoint(point, distance, angle);
	}
	
	public static Point2D.Double calcPoint(Point2D.Double point, double distance, double angle) {
		return new Point2D.Double(point.x + distance * Math.sin(angle), point.y + distance * Math.cos(angle));
	}
 
	/**
	 * Calculate angle between two points.
	 * 
	 * @param p2
	 * @param p1
	 * @return
	 */
	public static double calcAngle(Point2D.Double p2,Point2D.Double p1){
		return Math.atan2(p2.x - p1.x, p2.y - p1.y);
	}
}