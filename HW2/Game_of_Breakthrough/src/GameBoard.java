import java.util.Arrays;

/**
 * Created by qixinzhu on 10/2/16.
 */
public class GameBoard {

    public static char FIRST_PLAYER = 'X';
    public static char SECOND_PLAYER = 'O';
    public static char EMPTY = ' ';

    protected char[][] board;
    int numX;
    int numO;
    protected char winner;

    public GameBoard(int row, int col) {
        winner = 0;
        board = new char[row][col];
        numX = 2*col;
        numO = 2*col;
        for (int i=0;i<row;i++) {
            for (int j=0; j<col;j++) {
                if (i < 2) {
                    board[i][j] = FIRST_PLAYER;
                } else if (i>row-3) {
                    board[i][j] = SECOND_PLAYER;
                } else {
                    board[i][j] = EMPTY;
                }
            }
        }
    }

    public GameBoard() {
        this(8,8);
    }

    public GameBoard(GameBoard other) {
        winner = other.winner;
        numX = other.numX;
        numO = other.numO;
        board = new char[other.board.length][other.board[0].length];
        for (int i=0; i<other.board.length;i++) {
            this.board[i] = Arrays.copyOf(other.board[i], other.board[i].length);
        }
    }

    public void captureWorker(char player) {
        if (player == FIRST_PLAYER) numX--;
        else if (player == SECOND_PLAYER) numO--;
    }

    public boolean gameOver(boolean threeWorkerRule) {
        if (threeWorkerRule) {
            if (numX > 2 && numO > 2) {
                int firstCount = 0;
                int secondCount = 0;
                for (int i = 0; i < board[0].length; i++) {
                    if (board[0][i] == SECOND_PLAYER) {
                        secondCount++;
                        if (secondCount == 3) {
                            winner = SECOND_PLAYER;
                            return true;
                        }
                    }
                    if (board[board.length - 1][i] == FIRST_PLAYER) {
                        firstCount++;
                        if (firstCount == 3) {
                            winner = FIRST_PLAYER;
                            return true;
                        }
                    }
                }
                return false;
            }
            if (numX < 3) winner = SECOND_PLAYER;
            else if (numO < 3) winner = FIRST_PLAYER;
            return true;
        } else {        // traditional: 1-worker-to-enemy-base win criterion
            if (numX > 0 && numO > 0) {
                for (int i = 0; i < board[0].length; i++) {
                    if (board[0][i] == SECOND_PLAYER) {
                        winner = SECOND_PLAYER;
                        return true;
                    }
                    if (board[board.length - 1][i] == FIRST_PLAYER) {
                        winner = FIRST_PLAYER;
                        return true;
                    }
                }
                return false;
            }
            if (numX == 0) winner = SECOND_PLAYER;
            else if (numO == 0) winner = FIRST_PLAYER;
            return true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<board.length;i++) {
            sb.append(board[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

}
