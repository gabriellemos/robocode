package com.sherida;


import robocode.Robot;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;

public class EnemyBot extends Object {

	public Point2D.Double pos;

	public double bearing;
	public double distance;
	public double heading;

	public double energy;
	public double velocity;
	public String name = "";

	public double x;
	public double y;
	
	private Long stopedAt;

	public EnemyBot() {
		reset();
	}
	
	public void update(ScannedRobotEvent e) {
        bearing = e.getBearing();
        distance = e.getDistance();
        heading = e.getHeading();

        energy = e.getEnergy();
        velocity = e.getVelocity();
        name = e.getName();
        
        stopedAt = velocity == 0 ? System.currentTimeMillis() : null;
    }

    public void update(ScannedRobotEvent e, Robot robot) {
    	update(e);

    	double absBearingDeg = (robot.getHeading() + e.getBearing());
    	if (absBearingDeg < 0) absBearingDeg += 360;

    	x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
    	y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
    }

	public void reset() {
        bearing = distance = heading = energy = velocity = x = y = 0.0;
        name = "";
    }

    public double getFutureX(long when){
        return x + Math.sin(Math.toRadians(heading)) * velocity * when;
    }

    public double getFutureY(long when){
        return y + Math.cos(Math.toRadians(heading)) * velocity * when;
    }

	public double getFutureT(Robot robot, double bulletVelocity){
        // enemy velocity
        double v_E = velocity;

        // temp variables
        double x_diff = x - robot.getX();
        double y_diff = y - robot.getY();

        // angles of enemy's heading
        double sin = Math.sin(Math.toRadians(heading));
        double cos = Math.cos(Math.toRadians(heading));

        // calculated time
        double T;
        double v_B = bulletVelocity;

        double xy = (x_diff*sin + y_diff*cos);

        T = ( (v_E*xy) + Math.sqrt(sqr(v_E)*sqr(xy) + (sqr(x_diff) + sqr(y_diff))*(sqr(v_B) + sqr(v_E))) ) / (sqr(v_B) - sqr(v_E));

        return T;
    }

    private static double sqr(double in){
        return in * in;
    }
    
    public boolean none() {
    	return "".equals(name);
    }
    
    public boolean hasStoped() {
    	return velocity == 0 && System.currentTimeMillis() - stopedAt > 500;
    }
    
    public boolean isCloser(EnemyBot other) {
    	return distance < other.distance;
    }
}