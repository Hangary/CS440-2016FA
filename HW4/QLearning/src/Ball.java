import java.util.Random;

/**
 * Created by qixinzhu on 11/16/16.
 */
public class Ball {
    double xD, yD, xV, yV;

    public Ball(double x, double y, double vx, double vy) {
        xD = x;
        yD = y;
        xV = vx;
        yV = vy;
    }

    public Ball() {
        this(0.5, 0.5, 0.03, 0.01);
    }

    public void reset() {
        xD = 0.5;
        yD = 0.5;
        xV = 0.03;
        yV = 0.01;
    }

    public double getX() {
        return xD;
    }

    public double getY() {
        return yD;
    }

    public double getVx() {
        return xV;
    }

    public double getVy() {
        return yV;
    }

    public void setxV(double xV) {
        this.xV = xV;
    }

    public void setyV(double yV) {
        this.yV = yV;
    }

    public void setxD(double xD) {
        this.xD = xD;
    }

    public void setyD(double yD) {
        this.yD = yD;
    }

    public void move() {
        xD += xV;
        yD += yV;
        //bounce();
    }
}
