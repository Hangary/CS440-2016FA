/**
 * Created by qixinzhu on 11/18/16.
 */
public class QPaddle extends Paddle {

    /**
     * Parameter adjustment
     */
    private static int learning_rate_constant = 30;
    public static double gamma = 0.8;

    /**
     * global constants
     */
    public static int GRID_NUM = 12;
    public static double[] VELOCITY_X = {0};
    public static double[] VELOCITY_Y = {-0.015, 0.015};
    public static int PADDLE_LOC = 12;
    public static double[] ACTIONS = {-0.04, 0, 0.04};

    /**
     * Inner class
     */
    protected class State {
        int gridX, gridY, vX, vY, pLoc;

        private State(double bx, double by, double vx, double vy, double py) {
            gridX = (int) (bx * GRID_NUM);
            gridY = (int) (by * GRID_NUM);
            vX = 0;
            for (; vX < VELOCITY_X.length; vX++) if (vx < VELOCITY_X[vX]) break;
            vY = 0;
            for (; vY < VELOCITY_Y.length; vY++) if (vy < VELOCITY_Y[vY]) break;
            pLoc = Math.min((int) (12 * py / (1 - height)), 11);
        }
    }

    /**
     * Q-learning matrix:
     * [grid_x] [grid_y] [velocity_x] [velocity_y] [paddle_location] [actions] [Q/N]
     * actions = 0 -> reward;
     * Q/N: 0 - Q-value, 1 - number of trials for that action
     */
    double[][][][][][][] Qmatrix;
    State before, after;
    long iterNum;
    boolean exploration = true;

    public QPaddle() {
        super();
        iterNum = 0;
        Qmatrix = new double[GRID_NUM][GRID_NUM][VELOCITY_X.length + 1][VELOCITY_Y.length + 1][PADDLE_LOC][ACTIONS.length + 1][2];
    }

    public void recordBefore(double bx, double by, double vx, double vy, double py) {
        before = new State(bx, by, vx, vy, py);
    }

    public void recordAfter(double bx, double by, double vx, double vy, double py) {
        after = new State(bx, by, vx, vy, py);
    }

    private double getReword(State s) {
        return Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][0][0];
    }

    private double getQValue(State s, int action) {
        return Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][action + 1][0];
    }

    private void setQValue(State s, int action, double value) {
        Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][action + 1][0] = value;
    }

    public void move(int action) {
        yD += ACTIONS[action];
        if (yD < 0) yD = 0;
        else if (yD > 1 - height) yD = 1 - height;
    }

    public void turnOffExploration() {
        exploration = false;
    }

    public int selectAction() {
        State s = before;
        int action = 0;
        double utilMax = Integer.MIN_VALUE;
        for (int i = 0; i < ACTIONS.length; i++) {
            try {
                double qValue = Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][i + 1][0];
                double actNum = Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][i + 1][1];
                double f;
                if (exploration) f = qValue + 1 / (0.01 + actNum);
                else f = qValue;
                if (f > utilMax) {
                    action = i;
                    utilMax = f;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e);
            }
        }
        return action;
    }

    public void updateTD(int action) {
        State s1 = before, s2 = after;
        double s1Q = getQValue(s1, action);
        double actNum = Qmatrix[s1.gridX][s1.gridY][s1.vX][s1.vY][s1.pLoc][action + 1][1];
        double alpha = learning_rate_constant / (learning_rate_constant + actNum);
        double R = getReword(s1);
        double s2MaxQ = -1;

        try {
            if (s2.gridX < GRID_NUM && s2.gridX >= 0) {
                for (int i = 0; i < ACTIONS.length; i++) {
                    double Qs2a = Qmatrix[s2.gridX][s2.gridY][s2.vX][s2.vY][s2.pLoc][i + 1][0];
                    s2MaxQ = Math.max(s2MaxQ, Qs2a);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }

        double newQ = s1Q + alpha * (R + gamma * s2MaxQ - s1Q);
        setQValue(s1, action, newQ);

        Qmatrix[s1.gridX][s1.gridY][s1.vX][s1.vY][s1.pLoc][action + 1][1]++;
        before = null;
        after = null;
    }

    public void receiveReward() {
        State s = before;
        Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][0][0] = 1;
    }

    public void receivePenalty() {
        State s = before;
        Qmatrix[s.gridX][s.gridY][s.vX][s.vY][s.pLoc][0][0] = -1;
    }

    public void addIterNum() {
        iterNum++;
    }

    public long getIterNum() {
        return iterNum;
    }

}
