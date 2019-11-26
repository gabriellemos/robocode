package com.sherida;


import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.*;

/**
 * @author Berg
 *
 */
import java.awt.Color; 

import robocode.RateControlRobot;
import robocode.ScannedRobotEvent;
import robocode.control.events.TurnStartedEvent;

public class PainKiller extends RateControlRobot { 
    private int rightDirectionCounter = 0;
    private int leftDirectionCounter = 0;
    private int hitWallCounter = 0;
    private int hitRobotCounter = 0;
    int count = 0; // Keeps track of how long we've
    // been searching for our target
    double gunTurnAmt; // How much to turn our gun when searching
    String trackName; // Name of the robot we're currently tracking
    
    public void run() {
        // Set colors
        setBodyColor(Color.pink);
        setGunColor(Color.pink);
        setRadarColor(Color.pink);
        setScanColor(Color.pink);
        setBulletColor(Color.pink);

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
        
        setAhead(500);
        if (rightDirectionCounter <= 10) {
            setTurnRight(90);
            turnGunLeft(360);
            rightDirectionCounter++;
        } else if (leftDirectionCounter <= 10) {
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
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        setTurnLeft(90);
        setBack(500);
        if (hitRobotCounter > 2) {
            flipDirection();
            hitRobotCounter = 0;
        }

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
     * onScannedRobot:  We have a target.  Go get it.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        
     // Calculate exact location of the robot
        double absoluteBearing = getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

     // If it's close enough, fire!
        if (Math.abs(bearingFromGun) <= 3) {
            turnGunRight(bearingFromGun);
            if (getGunHeat() == 0 && e.getDistance() < 800) {
                if (e.getDistance() <= 600 && getEnergy() > 50) {
                    fire(3);
                } else {
                    fire(2);
                }
            }
        } else {
            turnGunRight(bearingFromGun);
        }
        // Call the scan if the gun is not turning.
        if (bearingFromGun == 0) {
            scan();
        }
        
//        
//        
//        //para mirar o radar no adversário.
//        turnRadarRight(anguloRelativo(e.getBearing()+getHeading()-getRadarHeading()));
//        //para mirar o canhão no adversário.
//        turnGunRight(anguloRelativo(e.getBearing()+getHeading()-getGunHeading())); 
//        //para virar seu robô em direção do adversário
//        turnRight(anguloRelativo(e.getBearing()));
//        // Calculate exact location of the robot
//        mirar(anguloRelativo(e.getBearing()));
//        tiroPorradaEBomba(51);
    }

    public void onWin(WinEvent e) {
        // Victory dance
        turnRight(36000);
    }
    
    /**
     * onHitRobot:  Move away a bit.
     */
    public void onHitRobot(HitRobotEvent e) { 
        setAhead(400);
        if (hitRobotCounter > 2) {
            flipDirection();
            hitRobotCounter = 0;
            executeMovement();
        }
    }
    
    /**
     * onHitRobot:  Move away a bit.
     */
    public void onHitWall(HitRobotEvent e) {
        if(temParedeAi()) {
            setBack(500);
        } else {
            setAhead(500);
        }
    }
    
    /**
     * verifica se está perto da parede.
     * 
     * @return true se estiver perdo da parede, false caso contrário.
     */
    public boolean temParedeAi() {
        return (getX() < 50 || getX() > getBattleFieldWidth() - 50 ||
            getY() < 50 || getY() > getBattleFieldHeight() - 50);
    }

    
    /**
     * Aregaaaaaaaaaçaaaaaaaaaaaaaaaa PÁÁÁÁÁ´´AÁÁÁÁ´´AÁ!
     * 
     * @param distancia
     *          distância do robo.
     */
    public void tiroPorradaEBomba(double distancia) {
        if (distancia > 200 || getEnergy() < 15) {
            fire(2);
        } else if (distancia > 50) {
            fire(2);
        } else {
            fire(3);
        }
    }

    /**
     * Recebe o angulo atual do adversário e mira nele.
     * 
     * @param bearing
     *          angulo do adversário.
     */
    public void mirar(final double bearing) {
        double miraAngulo = getHeading() + bearing - getGunHeading();
        if (!(miraAngulo > -180 && miraAngulo <= 180)) {
            while (miraAngulo <= -180) {
                miraAngulo += 360;
            }
            while (miraAngulo > 180) {
                miraAngulo -= 360;
            }
        }
        turnGunRight(miraAngulo);
    }
    
    /**
     * Retorna o angulo relativo da arma. Se é mais rápido girar pra direita ou esquerda.
     * 
     * @param angulo
     *          angulo atual.
     *  
     * @return angulo relativo.
     */
    public double anguloRelativo(final double angulo) {
        if (angulo> -180 && angulo<= 180) {
            return angulo;
        }
        double relativo = angulo;
        while (relativo<= -180) {
            relativo += 360;
        }
        while (angulo> 180) {
            relativo -= 360;
        }
        return relativo;
    }
    
}
