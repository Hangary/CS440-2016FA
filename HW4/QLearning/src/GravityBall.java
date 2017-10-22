/**
 * Created by qixinzhu on 11/21/16.
 */
public class GravityBall extends Ball {
    public void move() {
        super.move();
        yV += 0.002;
        if (yV > 0.08) yV = 0.08;
        else if (yV < -0.08) yV = -0.08;
    }
}
