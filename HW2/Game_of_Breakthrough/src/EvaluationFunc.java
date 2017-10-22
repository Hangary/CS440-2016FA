import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by qixinzhu on 10/2/16.
 */
public abstract class EvaluationFunc {
    private static double EPSILON = 0.00001;

    protected class myStruct {
        double playerLive;
        double playerAttack;

        public myStruct(double pL, double pA) {
            playerLive = pL;
            playerAttack = pA;
        }
    }

    public abstract double evaluateBoard(GameBoard gb, char player);

    public myStruct evaluateBoard(GameBoard gb, char player, boolean countThreeTopMost) {

        if (gb.gameOver(countThreeTopMost)) {
            if (gb.winner == player) return new myStruct(Integer.MAX_VALUE / 100, Integer.MAX_VALUE / 100);
            else return new myStruct(Integer.MIN_VALUE / 100, Integer.MIN_VALUE / 100);
        }

        double playerLive = 0;
        double playerAttack = 0;
        int row = gb.board.length;
        int col = gb.board[0].length;

        Queue<Integer> threeTopMost = new PriorityQueue<>();
        //for (int i = 0; i < 3; i++) threeTopMost.add(0);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (gb.board[i][j] == player) {
                    playerLive += 1;     // alive score
                    if (player == GameBoard.FIRST_PLAYER) {
                        playerAttack += 1 / (row - 1 - i + EPSILON);         // position score
                        if (threeTopMost.size() <3 || i > threeTopMost.peek()) {
                            // save top 3 workers
                            threeTopMost.poll();
                            threeTopMost.add(i);
                        }
                    } else {        // SECOND_PLAYER
                        playerAttack += 1 / (i + EPSILON);     // position score
                        if (threeTopMost.size() <3 || row - 1 - i > threeTopMost.peek()) {
                            threeTopMost.poll();
                            threeTopMost.add(row - 1 - i);
                        }
                    }
                }
            }
        }

        while (threeTopMost.size() > 1) {
            int i = threeTopMost.poll();
            if (countThreeTopMost) {
                playerAttack += 1 / (row - 1 - i + EPSILON);
            }
        }
        //playerAttack += countThreeTopMost ? (1 / threeTopMost.poll()) : 0 * threeTopMost.poll();
        if (threeTopMost.size() > 0 ) {
            int i = threeTopMost.poll();
            playerAttack += 1 / (row - 1 - i + EPSILON);
        }

        return new myStruct(playerLive, playerAttack);
    }
}
