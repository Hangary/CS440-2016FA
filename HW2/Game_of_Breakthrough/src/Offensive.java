/**
 * Created by qixinzhu on 10/2/16.
 */
public class Offensive extends EvaluationFunc {

    private static double OFFEND_FACTOR = 1.5;
    protected boolean countThreeTopMost;

    public Offensive(boolean three) {
        countThreeTopMost = three;
    }

    public double evaluateBoard(GameBoard gb, char player) {
        char enemy = (char) (GameBoard.FIRST_PLAYER + GameBoard.SECOND_PLAYER - player);
        double evaluation;
        myStruct playerScore = super.evaluateBoard(gb, player, countThreeTopMost);
        myStruct enemyScore = super.evaluateBoard(gb, enemy, countThreeTopMost);

        evaluation = playerScore.playerLive + OFFEND_FACTOR * playerScore.playerAttack
                - enemyScore.playerAttack - OFFEND_FACTOR * enemyScore.playerLive;

        return evaluation;
    }
}
