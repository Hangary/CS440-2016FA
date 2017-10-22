import java.awt.*;
import java.io.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        //Part_1_1();

        //Part_1_2();

        //Part_1_3();

        Part_1_4();
    }

    public static void Part_1_1() throws FileNotFoundException {
        String fileName = "./results/Part_1_1.txt";
        PrintWriter out = new PrintWriter(new File(fileName));

        playAI(out, 100000, false);

        // state space size = 20^3*4*5 = 160K

        QPaddle.GRID_NUM = 20;
        QPaddle.PADDLE_LOC = 20;
        QPaddle.VELOCITY_X = new double[]{-0.06, 0, 0.06};  //{-0.06, 0, 0.06}
        QPaddle.VELOCITY_Y = new double[]{-0.06, -0.015, 0.015, 0.06};  // {-0.06, -0.015, 0.015, 0.06}
        playAI(out, 1600000, false);


        out.close();
    }

    public static void playAI(PrintWriter out, int train, boolean autoPlay) {
        Ball b = new Ball();
        QPaddle ai = new QPaddle();
        Paddle user;
        if (autoPlay) {
            user = new AutoPaddle(b);
        } else {
            user = new Paddle(1.2, 0);
        }
        Game g = new Game(b, ai, user);

        out.printf("Number of grids: %d * %d\n", QPaddle.GRID_NUM, QPaddle.GRID_NUM);
        out.printf("Number of paddle location: %d\n", QPaddle.PADDLE_LOC);
        out.printf("Discrete X-velocity: %d; Y-velocity: %d\n", QPaddle.VELOCITY_X.length + 1, QPaddle.VELOCITY_Y.length + 1);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < train; i++) {
            /*
            if (i==10) {
                playGUI(g);
                break;
            }
            /*
            /*
            if (i==1000) {
                playGUI(g);
                break;
            }
            */

            g.play();
        }
        out.printf("Training time for %dK iterations: %dms\n", train / 1000, System.currentTimeMillis() - startTime);

        //playGUI(g);
        // test

        g.resetBounce();
        ai.turnOffExploration();
        int test;
        int aiWins = 0;
        for (test = 0; test < 5000; test++) {
            if (g.play() == Game.PLAYER_AI) aiWins++;
        }
        if (autoPlay) {
            out.printf("Percentage of games AI wins over 5K test games: %.1f%%\n", aiWins * 100.0 / test);
        } else {
            out.printf("Average number of bounces over %d test games: %.2f\n\n", test, g.getBounceNum() * 1.0 / test);
        }

    }

    public static void playGUI(Game g) {
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

    public static void Part_1_2() throws FileNotFoundException {
        String fileName = "./results/Part_1_2.txt";
        PrintWriter out = new PrintWriter(new File(fileName));

        playAI(out, 100000, true);
        out.close();
    }

    /**
     * Interactive game: human winning chance depends on animation speed
     * AI does not have anticipation, human does (but also make mistakes and moves slower)
     *
     * ----Press 's' to start, 'up' to move up and 'down' to move down.
     */
    public static void Part_1_3() {
        int train = 100000;
        Ball b = new Ball();
        QPaddle ai = new QPaddle();
        Paddle wall = new Paddle(1.2, 0);
        Paddle user = new Paddle(0.2, 0.04);
        Game g = new Game(b, ai, wall);

        for (int i = 0; i < train; i++) {
            g.play();
        }
        Game g2 = new Game(b, ai, user);
        playGUI(g2);
    }

    /**
     * Gravitational Pong
     */
    public static void Part_1_4() throws FileNotFoundException {
        String fileName = "./results/Part_1_4.txt";
        PrintWriter out = new PrintWriter(new File(fileName));

        playAI2(out, 1000000);

        QPaddle.VELOCITY_Y = new double[]{-0.05, -0.015, 0.015, 0.05};  // {-0.06, -0.015, 0.015, 0.06}
        playAI2(out, 1000000);

        out.close();
    }

    public static void playAI2(PrintWriter out, int train) {
        Ball b = new GravityBall();
        QPaddle ai = new QPaddle();
        Paddle user;
        user = new Paddle(1.2, 0);
        Game g = new Game(b, ai, user);

        out.printf("Number of grids: %d * %d\n", QPaddle.GRID_NUM, QPaddle.GRID_NUM);
        out.printf("Number of paddle location: %d\n", QPaddle.PADDLE_LOC);
        out.printf("Discrete X-velocity: %d; Y-velocity: %d\n", QPaddle.VELOCITY_X.length + 1, QPaddle.VELOCITY_Y.length + 1);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < train; i++) {
            g.play();
        }
        out.printf("Training time for %dK iterations: %dms\n", train / 1000, System.currentTimeMillis() - startTime);

        // test
        g.resetBounce();
        ai.turnOffExploration();
        int test;
        int aiWins = 0;
        for (test = 0; test < 5000; test++) {
            if (g.play() == Game.PLAYER_AI) aiWins++;
        }
        out.printf("Average number of bounces over %d test games: %.2f\n\n", test, g.getBounceNum() * 1.0 / test);

        //playGUI(g);
    }
}
