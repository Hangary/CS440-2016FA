import java.util.*;

/**
 * Created by qixinzhu on 10/2/16.
 */
public abstract class Agent {

    public static double EPSILON = 0.00001;

    protected int DEPTH_LIMIT;
    protected boolean threeWorkerRule;

    protected char player;
    protected EvaluationFunc eFunc;
    protected char opponent;
    protected int expandNodeNum;
    protected Random rdm;

    protected Agent(char p, EvaluationFunc e) {
        DEPTH_LIMIT = 0;
        expandNodeNum = 0;
        player = p;
        eFunc = e;
        opponent = (char) (GameBoard.FIRST_PLAYER + GameBoard.SECOND_PLAYER - player);
        rdm = new Random();
    }

    protected List<GameBoard> getSuccessors(GameBoard gb, char mover) {
        //expandNodeNum++;
        int forward;
        char enemy;
        List<GameBoard> successors = new ArrayList<>();
        int row = gb.board.length;
        int col = gb.board[0].length;

        if (mover == GameBoard.FIRST_PLAYER) {
            forward = 1;
            enemy = GameBoard.SECOND_PLAYER;
            for (int i = row - 1; i >= 0; i--) {
                getSuccessorHelper(gb, mover, forward, enemy, successors, row, col, i);
            }
        } else {
            forward = -1;
            enemy = GameBoard.FIRST_PLAYER;
            for (int i = 0; i < row; i++) {
                getSuccessorHelper(gb, mover, forward, enemy, successors, row, col, i);
            }
        }
        return successors;
    }

    private void getSuccessorHelper(GameBoard gb, char mover, int forward, char enemy, List<GameBoard> successors, int row, int col, int i) {
        for (int j = 0; j < col; j++) {
            if (gb.board[i][j] == mover && i + forward < row && i + forward >= 0) {
                GameBoard newGB;
                // move forward
                if (gb.board[i + forward][j] == GameBoard.EMPTY) {
                    newGB = new GameBoard(gb);
                    newGB.board[i][j] = GameBoard.EMPTY;
                    newGB.board[i + forward][j] = mover;
                    successors.add(newGB);
                }
                // move left forward
                if (j - 1 >= 0 && gb.board[i + forward][j - 1] != mover) {
                    newGB = new GameBoard(gb);
                    newGB.board[i][j] = GameBoard.EMPTY;
                    if (newGB.board[i + forward][j - 1] == enemy) newGB.captureWorker(enemy);
                    newGB.board[i + forward][j - 1] = mover;
                    successors.add(newGB);
                }
                // move right forward
                if (j + 1 < col && gb.board[i + forward][j + 1] != mover) {
                    newGB = new GameBoard(gb);
                    newGB.board[i][j] = GameBoard.EMPTY;
                    if (newGB.board[i + forward][j + 1] == enemy) newGB.captureWorker(enemy);
                    newGB.board[i + forward][j + 1] = mover;
                    successors.add(newGB);
                }
            }
        }
    }

    public abstract GameBoard makeMove(GameBoard gb);

    public String toString() {
        String s = "" + this.getClass().getName()+" - Depth "+this.DEPTH_LIMIT+ " ("+this.eFunc.getClass().getName() +")";
        return s;
    }

    public void reset() {
        expandNodeNum = 0;
    }
}
