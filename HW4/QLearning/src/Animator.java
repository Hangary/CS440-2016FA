import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by qixinzhu on 11/16/16.
 */
public class Animator implements Runnable {
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_WIDTH = 20;
    private static final int FRAME_WIDTH = 480;
    private static final int FRAME_HEIGHT = 480;
    public final static int ANI_DELAY = 30; // milliseconds between screen

    /**
     * Inner class: keylistener
     */
    class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            int nKey = e.getKeyCode();
            switch (nKey) {
                case 83:    // 's'
                    running = true;
                    break;
                case 38:    // UP
                    playerUser.moveUp();
                    frame.repaint();
                    break;
                case 40:    // DOWN
                    playerUser.moveDown();
                    frame.repaint();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    /**
     * Inner class: JComponent
     */
    class MyComponent extends JComponent {
        public void paintComponent(Graphics g) {

            // paint playerAI
            int yAI = (int) (playerAI.getY() * FRAME_HEIGHT);
            int hAI = (int) (playerAI.getHeight() * FRAME_HEIGHT);
            g.fillRect(FRAME_WIDTH + PADDLE_WIDTH + BALL_SIZE, yAI, PADDLE_WIDTH, hAI);

            // paint playerUser
            int yU = (int) (playerUser.getY() * FRAME_HEIGHT);
            int hU = (int) (playerUser.getHeight() * FRAME_HEIGHT);
            g.fillRect(0, yU, PADDLE_WIDTH, hU);

            // paint pong
            int xB = PADDLE_WIDTH + (int) (pong.getX() * FRAME_WIDTH);
            int yB = (int) (pong.getY() * FRAME_HEIGHT);
            g.setColor(Color.RED);
            g.fillOval(xB, yB, BALL_SIZE, BALL_SIZE);
            g.setColor(Color.BLACK);

        }
    }

    /**
     * instance variables and methods
     */

    JFrame frame;
    //JPanel panel;
    JComponent component;
    Ball pong;
    Paddle playerAI, playerUser;
    Game game;
    private Thread thrAnim;
    private boolean running;
    long iteration;
    String title;

    public Animator(String title, Game g, boolean interactive) {
        iteration = 0;
        game = g;
        this.title = title;
        frame = new JFrame(String.format("%s %s %d", title, "Iteration:", iteration));
        frame.setSize(FRAME_WIDTH + PADDLE_WIDTH * 2 + BALL_SIZE, FRAME_HEIGHT + 20 + BALL_SIZE);
        frame.setLocation(150, 150);
        //panel = new JPanel();
        //frame.add(panel);
        component = new MyComponent();
        component.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.add(component);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        if (interactive) {
            component.addKeyListener(new MyKeyListener());
            component.setFocusable(true);
            running = false;
        } else running = true;

        pong = g.getPong();
        playerAI = g.getPlayerAI();
        playerUser = g.getPlayerUser();
    }

    public void update() {
        frame.repaint();
    }

    public void fireUpAnimThread() { // called initially
        if (thrAnim == null) {
            thrAnim = new Thread(this); // pass the thread a runnable object (this)
            thrAnim.start();
        }
    }

    // implements runnable - must have run method
    public void run() {
        // lower this thread's priority; let the "main" aka 'Event Dispatch'
        // thread do what it needs to do first
        thrAnim.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long lStartTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == thrAnim) {
            if (!game.isOver() && running) {
                game.update();
            } else if (game.isOver()) {
                //running = false;
                iteration++;
                game.reset();
                frame.setTitle(String.format("%s %s %d", title, "Iteration:", iteration));
            }

            frame.repaint();

            try {
                // The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update)
                // between frames takes longer than ANI_DELAY, then the difference between lStartTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                lStartTime += ANI_DELAY;
                Thread.sleep(Math.max(0,
                        lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
            public void run() {
                try {
                    Ball b = new Ball();
                    QPaddle ai = new QPaddle();
                    Paddle user = new Paddle(1, 0);
                    Game g = new Game(b, ai, user);
                    Animator game = new Animator("", g, false);
                    game.fireUpAnimThread();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
