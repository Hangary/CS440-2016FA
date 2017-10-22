import java.util.ArrayList;
import java.util.Random;

/**
 * Created by qixinzhu on 10/2/16.
 */
public class AlphabetaAgent extends Agent {

    protected static int RAND_SEED = 2;

    // 3: avg 3k/move
    // 4: avg 30k/move
    // 5: avg 193k/move
    // 6: avg 4420k/move

    public AlphabetaAgent(char player, EvaluationFunc eFunc, boolean threeWorkerRule) {
        super(player, eFunc);
        DEPTH_LIMIT = 6;
        this.threeWorkerRule = threeWorkerRule;
        rdm.setSeed(RAND_SEED);
    }

    @Override
    public GameBoard makeMove(GameBoard gb) {
        ArrayList<GameBoard> ties = new ArrayList<>();
        double maxEvaluation = Integer.MIN_VALUE;
        for (GameBoard next : this.getSuccessors(gb, this.player)) {
            expandNodeNum++;
            //System.out.println(next);
            double v = alphabeta(next, DEPTH_LIMIT - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
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
        return ties.get(rdm.nextInt(ties.size()));
    }

    private double alphabeta(GameBoard gb, int searchDepth, double alpha, double beta, boolean maximizing) {
        expandNodeNum++;
        if (searchDepth == 0 || gb.gameOver(threeWorkerRule)) return eFunc.evaluateBoard(gb, this.player);
        if (maximizing) {
            double v = Integer.MIN_VALUE;
            for (GameBoard nextGB : this.getSuccessors(gb, this.player)) {
                v = Math.max(v, alphabeta(nextGB, searchDepth - 1, alpha, beta, false));
                if (v >= beta) return v;
                alpha = Math.max(alpha, v);
            }
            return v;
        } else {
            double v = Integer.MAX_VALUE;
            for (GameBoard nextGB : this.getSuccessors(gb, this.opponent)) {
                v = Math.min(v, alphabeta(nextGB, searchDepth - 1, alpha, beta, true));
                if (v <= alpha) return v;
                beta = Math.min(beta, v);
            }
            return v;
        }
    }
}
