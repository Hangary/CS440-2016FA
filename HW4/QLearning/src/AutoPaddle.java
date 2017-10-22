/**
 * Created by qixinzhu on 11/19/16.
 */
public class AutoPaddle extends Paddle {
    Ball pong;

    public AutoPaddle(Ball b) {
        super(0.2, 0.02);
        pong = b;
    }

    @Override
    public void move() {
        double center = yD + height / 2;
        if (pong.getY() < center)
            this.moveUp();
        else if (pong.getY() > center)
            this.moveDown();
    }
}
