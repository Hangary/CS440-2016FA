import java.util.ArrayList;
import java.util.Random;

/**
 * Created by qixinzhu on 10/2/16.
 */
public class MinimaxAgent extends Agent {

    protected static int RAND_SEED = 1;

    // 3: avg 15k/move
    // 4: avg 370k/move
    // 5: avg ~7000k/move

    public MinimaxAgent(char player, EvaluationFunc eFunc, boolean threeWorkerRule) {
        super(player, eFunc);
        DEPTH_LIMIT = 4;
        this.threeWorkerRule = threeWorkerRule;
        rdm.setSeed(RAND_SEED);
    }

    @Override
    public GameBoard makeMove(GameBoard gb) {
        ArrayList<GameBoard> ties = new ArrayList<>();
        double maxEvaluation = Integer.MIN_VALUE;
        for (GameBoard next : this.getSuccessors(gb, this.player)) {
            //System.out.println(next);
            expandNodeNum++;
            double v = minimax(next, DEPTH_LIMIT - 1, false);
            if (v > maxEvaluation + EPSILON || ties.size() == 0) {
                maxEvaluation = v;
                ties = new ArrayList<>();
                ties.add(next);
            } else if (v > maxEvaluation - EPSILON) {
                ties.add(next);
            }
        }
        if (ties.size() == 0) {
            //System.out.println("Error! No moves found!");
            return gb;
        }
        //System.out.printf("chosen move: %.6f\n%s\n",maxEvaluation, nextMove);
        return ties.get(rdm.nextInt(ties.size()));
    }

    private double minimax(GameBoard gb, int searchDepth, boolean maximizing) {
        expandNodeNum++;
        if (searchDepth == 0 || gb.gameOver(threeWorkerRule)) return eFunc.evaluateBoard(gb, this.player);
        double bestValue;
        if (maximizing) {
            bestValue = Integer.MIN_VALUE;
            for (GameBoard nextGB : this.getSuccessors(gb, this.player)) {
                double v = minimax(nextGB, searchDepth - 1, false);
                bestValue = Math.max(bestValue, v);
            }
            return bestValue;
        } else {
            bestValue = Integer.MAX_VALUE;
            for (GameBoard nextGB : this.getSuccessors(gb, this.opponent)) {
                //System.out.println(nextGB);
                double v = minimax(nextGB, searchDepth - 1, true);
                bestValue = Math.min(bestValue, v);
            }
            return bestValue;
        }
    }
}
