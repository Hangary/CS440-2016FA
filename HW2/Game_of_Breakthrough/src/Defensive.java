/**
 * Created by qixinzhu on 10/2/16.
 */
public class Defensive extends EvaluationFunc {
    private static double DEFENSE_FACTOR = 1.5;

    protected boolean countThreeTopMost;

    public Defensive(boolean three) {
        countThreeTopMost = three;
    }

    public double evaluateBoard(GameBoard gb, char player) {
        char enemy = (char) (GameBoard.FIRST_PLAYER + GameBoard.SECOND_PLAYER - player);
        double evaluation;
        myStruct playerScore = super.evaluateBoard(gb, player, countThreeTopMost);
        myStruct enemyScore = super.evaluateBoard(gb, enemy, countThreeTopMost);

        evaluation = DEFENSE_FACTOR * playerScore.playerLive + playerScore.playerAttack
                - DEFENSE_FACTOR * enemyScore.playerAttack - enemyScore.playerLive;

        return evaluation;
    }
}
