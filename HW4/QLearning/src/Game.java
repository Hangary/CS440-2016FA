import java.awt.*;
import java.util.Random;

/**
 * Created by qixinzhu on 11/18/16.
 */
public class Game {
    public static Random rmd = new Random(1);
    public static int PLAYER_AI = 0;
    public static int PLAYER_USER = 1;

    Ball pong;
    Paddle playerUser;
    QPaddle playerAI;
    boolean isOver;
    long startTime;
    int bounceNum;

    public Game(Ball b, QPaddle ai, Paddle user) {
        pong = b;
        playerAI = ai;
        playerUser = user;
        isOver = false;
        bounceNum = 0;
        startTime = System.currentTimeMillis();
    }

    public Ball getPong() {
        return pong;
    }

    public Paddle getPlayerAI() {
        return playerAI;
    }

    public Paddle getPlayerUser() {
        return playerUser;
    }

    public void reset() {
        pong.reset();
        playerAI.reset();
        playerUser.reset();
        isOver = false;
    }

    public void resetBounce() {
        bounceNum = 0;
    }

    public int getBounceNum() {
        return bounceNum;
    }

    public boolean isOver() {
        return isOver;
    }

    public void bounce() {
        double yD = pong.getY();
        double yV = pong.getVy();
        double xD = pong.getX();
        double xV = pong.getVx();
        if (yD < 0) {
            yD = -yD;
            yV = -yV;
        } else if (yD > 1) {
            yD = 2 - yD;
            yV = -yV;
        }

        if (xD < 0 && yD >= playerUser.getY() && yD <= playerUser.getY() + playerUser.getHeight()) {
            xD = -xD;
            xV = -xV;
        } else if (xD > 1 && yD >= playerAI.getY() && yD <= playerAI.getY() + playerAI.getHeight()) {
            xD = 2 - xD;
            xV = -xV + 0.015 * (rmd.nextDouble() * 2 - 1);
            yV = yV + 0.03 * (rmd.nextDouble() * 2 - 1);
            playerAI.receiveReward();
            bounceNum++;
        }
        if (xV >= -0.03 && xV <= 0) xV = -0.03;
        else if (xV <= 0.03 && xV >= 0) xV = 0.03;
        pong.setxD(xD);
        pong.setxV(xV);
        pong.setyD(yD);
        pong.setyV(yV);
    }

    public int play() {
        int winner = -1;
        while (!isOver) {
            update();

            if (pong.getX() < 0) {
                winner = PLAYER_AI;
            } else if (pong.getX() > 1) {
                winner = PLAYER_USER;
            }
        }
        reset();
        playerAI.addIterNum();
        if (playerAI.getIterNum() % 10000 == 0) {
            System.out.printf("Iteration %dK [%dms]\n", playerAI.getIterNum() / 1000, System.currentTimeMillis() - startTime);
        }
        return winner;
    }

    public void update() {
        playerAI.recordBefore(pong.getX(), pong.getY(), pong.getVx(), pong.getVy(), playerAI.getY());
        int action = playerAI.selectAction();
        playerAI.move(action);
        playerUser.move();
        //playerUser.move();
        pong.move();
        bounce();
        playerAI.recordAfter(pong.getX(), pong.getY(), pong.getVx(), pong.getVy(), playerAI.getY());
        if (pong.getX() > 1) playerAI.receivePenalty();
        playerAI.updateTD(action);
        if (pong.getX() < 0 || pong.getX() > 1) isOver = true;
    }

    public static void main(String[] args) {
        Ball b = new Ball();
        QPaddle ai = new QPaddle();
        Paddle user = new Paddle(1.2, 0);
        Game g = new Game(b, ai, user);
        long startTime = System.currentTimeMillis();
        int train = 100000;
        for (int i = 0; i < train; i++) g.play();
        System.out.printf("Training time for %dK iterations: %dms\n", train / 1000, System.currentTimeMillis() - startTime);

        /**
         * test ?? games
         */
        g.resetBounce();
        ai.turnOffExploration();
        int test;
        int aiWins = 0;
        for (test = 0; test < 5000; test++) {
            int winner = g.play();
            if (winner == PLAYER_AI) aiWins++;
        }
        System.out.printf("Percentage of games AI wins over 5K test games: %.1f%%\n", aiWins * 100.0 / test);

        EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
            public void run() {
                try {
                    Animator ani = new Animator("", g, true);
                    ani.fireUpAnimThread();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
