import java.util.ArrayList;
import java.util.Random;

/**
 * Created by qixinzhu on 10/4/16.
 */
public class GreedyAgent extends Agent {

    protected static int RAND_SEED = 3;

    public GreedyAgent(char player, EvaluationFunc eFunc, boolean threeWorkerRule) {
        super(player, eFunc);
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
            /**
             * Greedy Agent: only evaluate next move
             */
            double v = eFunc.evaluateBoard(gb, this.player);
            if (v > maxEvaluation + EPSILON || ties.size() == 0) {
                maxEvaluation = v;
                ties = new ArrayList<>();
                ties.add(next);
            } else if (v > maxEvaluation - EPSILON) {
                ties.add(next);
            }
        }
        //System.out.printf("chosen move: %.6f\n%s\n",maxEvaluation, nextMove);
        if (ties.size() == 0) {
            //System.out.println("Error! No moves found!");
            return gb;
        }
        return ties.get(rdm.nextInt(ties.size()));
    }
}
