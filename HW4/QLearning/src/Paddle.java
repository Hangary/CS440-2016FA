/**
 * Created by qixinzhu on 11/16/16.
 */
public class Paddle {
    double height, yD;
    double v;

    public Paddle() {
        height = 0.2;
        yD = 0.5 - 0.2 / 2;
    }

    public Paddle(double h, double v) {
        height = h;
        yD = 0.5 - h / 2;
        this.v = v;
    }

    public void reset() {
        yD = 0.5 - height / 2;
    }

    public double getHeight() {
        return height;
    }

    public double getY() {
        return yD;
    }

    public double getVelocity() {
        return v;
    }

    public void moveUp() {
        yD = Math.max(yD - v, 0);
    }

    public void moveDown() {
        yD = Math.min(yD + v, 1 - height);
    }

    public void move() {};

}
