import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class GameOfBreakthroughSolver {
    public static void main(String[] args) throws FileNotFoundException {
/*
        GameBoard gb = new GameBoard();
        String fileName = "Game_of_Breakthrough/board1.txt";
        File inputFile = new File(fileName);
        Scanner in = new Scanner(inputFile);
        int i = 0;
        while (in.hasNextLine()) {
            char[] chars = (in.nextLine()).toCharArray();
            for (int j=0;j<gb.board[0].length;j++) {
                if (j<chars.length) gb.board[i][j] = chars[j];
                else gb.board[i][j] = ' ';
            }
            i++;
        }
        in.close();
        System.out.printf("%.6f\n%s\n",
                (new Defensive()).evaluateBoard(gb,GameBoard.FIRST_PLAYER), gb);
        Agent ag1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Defensive());

        List<GameBoard> tmp = ag1.getSuccessors(gb, ag1.player);
        for (GameBoard g : tmp) System.out.printf("%.6f\n%s\n",
                (new Defensive()).evaluateBoard(g,GameBoard.FIRST_PLAYER), g);

        List<GameBoard> tmp2 = ag1.getSuccessors(gb, ag1.opponent);
        for (GameBoard g : tmp2) System.out.printf("%.6f\n%s\n",
                (new Defensive()).evaluateBoard(g,GameBoard.FIRST_PLAYER), g);

        GameBoard next = ag1.makeMove(gb);
        System.out.printf("%.6f\n%s\n",
                (new Defensive()).evaluateBoard(next,GameBoard.FIRST_PLAYER), next);
*/

/*
        Agent ag1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Offensive(false), false);
        Agent ag2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Defensive(false), false);
        Agent ag3 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Offensive(false), false);
        Agent ag4 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Defensive(false), false);

        play(1, new GameBoard(), ag1, ag2, false);
            //play(15, new GameBoard(), ag1, ag2, false);
        play(2, new GameBoard(), ag4, ag3, false);
            //play(16, new GameBoard(), ag4, ag3, false);
        play(3, new GameBoard(), ag1, ag3, false);
        play(4, new GameBoard(), ag4, ag2, false);

        // 4th credit: Long Rectangular Board

        play(5, new GameBoard(5,10), ag1, ag2, false);
        play(6, new GameBoard(5,10), ag4, ag3, false);
        play(7, new GameBoard(5,10), ag1, ag3, false);
        play(8, new GameBoard(5,10), ag4, ag2, false);

        // 4th credit: 3 Workers to Base
        // --- may have to use depth 3 and 5 for time reason

        Agent ag5 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Offensive(true), true);
        Agent ag6 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Defensive(true), true);
        Agent ag7 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Offensive(true), true);
        Agent ag8 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Defensive(true), true);

        play(9, new GameBoard(), ag5, ag6, true);
        play(10, new GameBoard(), ag8, ag7, true);
        play(11, new GameBoard(), ag5, ag7, true);
        play(12, new GameBoard(), ag8, ag6, true);

        // change on Oct-15:
        // additional simulation for new 3-worker rule (<3 worker auto-lose)
        //play(27, new GameBoard(), ag5, ag7, true);

        // Bonus points -- Greedy agent

        Agent ag9 = new GreedyAgent(GameBoard.SECOND_PLAYER, new Offensive(false), false);
        Agent ag10 = new GreedyAgent(GameBoard.FIRST_PLAYER, new Defensive(false), false);

        play(13, new GameBoard(), ag1, ag9, false);
        play(14, new GameBoard(), ag10, ag2, false);


        // judge the effect of playing order

        Agent ag11 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Offensive(false), false);
        Agent ag12 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Defensive(false), false);
        Agent ag13 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Offensive(false), false);
        Agent ag14 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Defensive(false), false);

        // for 5*10 gameboard
        play(21, new GameBoard(5,10), ag1, ag2, false);
        play(22, new GameBoard(5,10), ag4, ag3, false);
        play(23, new GameBoard(5,10), ag1, ag11, false);
        play(24, new GameBoard(5,10), ag12, ag2, false);
        play(25, new GameBoard(5,10), ag13, ag3, false);
        play(26, new GameBoard(5,10), ag4, ag14, false);


        // original 8*8 gameboard

        play(17, new GameBoard(), ag1, ag11, false);
        play(18, new GameBoard(), ag12, ag2, false);
        play(19, new GameBoard(), ag13, ag3, false);
        play(20, new GameBoard(), ag4, ag14, false);
*/

        // Oct-20: re-run all simulations
        //create4MatchUp("Org-", new GameBoard(), false);

        //create4MatchUp("3WR-", new GameBoard(), true);

        //create4MatchUp("LRB-", new GameBoard(5, 10), false);

        create4MatchUp("Add-", new GameBoard(), false);

        // add greedy
        //runGreedy(new GameBoard());
    }

    public static void runGreedy(GameBoard gb) throws FileNotFoundException {
        String prefix = "GRD-";
        Agent o1, o2, d1, d2;
        // Bonus points -- Greedy agent
        // C: Minimax vs Greedy (Minimax goes first)
        o1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Offensive(false), false);
        o2 = new GreedyAgent(GameBoard.SECOND_PLAYER, new Offensive(false), false);
        d1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Defensive(false), false);
        d2 = new GreedyAgent(GameBoard.SECOND_PLAYER, new Defensive(false), false);

        play(prefix + "C1", 1, gb, o1, d2, false);
        play(prefix + "C2", 1, gb, d1, o2, false);
        play(prefix + "C3", 1, gb, o1, o2, false);
        play(prefix + "C4", 1, gb, d1, d2, false);

        // D: Greedy vs. Minimax (Greedy goes first)
        o1 = new GreedyAgent(GameBoard.FIRST_PLAYER, new Offensive(false), false);
        o2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Offensive(false), false);
        d1 = new GreedyAgent(GameBoard.FIRST_PLAYER, new Defensive(false), false);
        d2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Defensive(false), false);

        play(prefix + "D1", 1, gb, o1, d2, false);
        play(prefix + "D2", 1, gb, d1, o2, false);
        play(prefix + "D3", 1, gb, o1, o2, false);
        play(prefix + "D4", 1, gb, d1, d2, false);
    }

    public static void create4MatchUp(String prefix, GameBoard gb, boolean threeWorkerRule) throws FileNotFoundException {

        Agent o1, o2, d1, d2;
        int num = 4;
        if (prefix.equals("Add-")) num = 8;

        // A : Minimax vs. minimax
        o1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        o2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        d1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);
        d2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);

        play(prefix + "A1", num, gb, o1, d2, threeWorkerRule);
        play(prefix + "A2", num, gb, d1, o2, threeWorkerRule);
        play(prefix + "A3", num, gb, o1, o2, threeWorkerRule);
        play(prefix + "A4", num, gb, d1, d2, threeWorkerRule);

        // B: Alpha-beta vs. alpha-beta
        o1 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        o2 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        d1 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);
        d2 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);

        play(prefix + "B1", num, gb, o1, d2, threeWorkerRule);
        play(prefix + "B2", num, gb, d1, o2, threeWorkerRule);
        play(prefix + "B3", num, gb, o1, o2, threeWorkerRule);
        play(prefix + "B4", num, gb, d1, d2, threeWorkerRule);

        if (prefix.equals("Add-")) return;

        // C: Minimax vs. alpha-beta (minimax goes first)
        o1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        o2 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        d1 = new MinimaxAgent(GameBoard.FIRST_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);
        d2 = new AlphabetaAgent(GameBoard.SECOND_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);

        play(prefix + "C1", 1, gb, o1, d2, threeWorkerRule);
        play(prefix + "C2", 1, gb, d1, o2, threeWorkerRule);
        play(prefix + "C3", 1, gb, o1, o2, threeWorkerRule);
        play(prefix + "C4", 1, gb, d1, d2, threeWorkerRule);

        // D: Alpha-beta vs. minimax (alpha-beta goes first)
        o1 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        o2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Offensive(threeWorkerRule), threeWorkerRule);
        d1 = new AlphabetaAgent(GameBoard.FIRST_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);
        d2 = new MinimaxAgent(GameBoard.SECOND_PLAYER, new Defensive(threeWorkerRule), threeWorkerRule);

        play(prefix + "D1", 1, gb, o1, d2, threeWorkerRule);
        play(prefix + "D2", 1, gb, d1, o2, threeWorkerRule);
        play(prefix + "D3", 1, gb, o1, o2, threeWorkerRule);
        play(prefix + "D4", 1, gb, d1, d2, threeWorkerRule);

    }

    public static void play(String gameName, int iteration, GameBoard origin, Agent ag1, Agent ag2, boolean threeWorkerRule) throws FileNotFoundException {
        String outputFileName = "Game_of_Breakthrough/result/Game_" + gameName + "_output.txt";
        PrintWriter out = new PrintWriter(outputFileName);

        for (int dup = 0; dup < iteration; dup++) {
            GameBoard gb = new GameBoard(origin);
            ag1.reset();
            ag2.reset();
            int ag1Step = 0, ag2Step = 0;
            long ag1_start, ag1_end, ag2_start, ag2_end;
            long ag1_total_time = 0, ag2_total_time = 0;
            int initialWorkers = 2 * gb.board[0].length;
            while (!gb.gameOver(threeWorkerRule)) {
                ag1Step++;
                //System.out.printf("--- Step %d ---\n", ag1Step);
                ag1_start = System.currentTimeMillis();
                gb = ag1.makeMove(gb);
                ag1_end = System.currentTimeMillis();
                //System.out.printf("[%dms] Player1 [%d] moves:\n%s\n", ag1_end - ag1_start, ag1.expandNodeNum, gb);
                ag1_total_time += ag1_end - ag1_start;
                if (gb.gameOver(threeWorkerRule)) break;
                ag2Step++;
                ag2_start = System.currentTimeMillis();
                gb = ag2.makeMove(gb);
                ag2_end = System.currentTimeMillis();
                //System.out.printf("[%dms] Player2 [%d] moves:\n%s\n", ag2_end - ag2_start, ag2.expandNodeNum, gb);
                ag2_total_time += ag2_end - ag2_start;
            }
            out.printf("\n-------------\nTrial %d:\n", dup + 1);
            out.printf("Final state:\n%s\n", gb);
            out.printf("Winner is: (%s) %s\n", (gb.winner == GameBoard.FIRST_PLAYER) ? "First Player" : "Second Player",
                    (gb.winner == GameBoard.FIRST_PLAYER) ? ag1 : ag2);
            out.printf("Total number of moves required till the win: %d\n", ag1Step);
            out.printf("\nTotal number of game tree nodes expanded:\n");
            out.printf("%-38s%d\n", ag1, ag1.expandNodeNum);
            out.printf("%-38s%d\n", ag2, ag2.expandNodeNum);
            out.printf("\nAverage number of nodes expanded per move:\n");
            out.printf("%-38s(%d)\n", ag1, ag1.expandNodeNum / ag1Step);
            out.printf("%-38s(%d)\n", ag2, ag2.expandNodeNum / ag2Step);
            out.printf("\nAverage amount of time per move:\n");
            out.printf("%-38s[%dms]\n", ag1, ag1_total_time / ag1Step);
            out.printf("%-38s[%dms]\n", ag2, ag2_total_time / ag1Step);
            out.printf("\n%s captured %d opponent workers\n", ag1, initialWorkers - gb.numO);
            out.printf("%s captured %d opponent workers\n", ag2, initialWorkers - gb.numX);

            System.out.printf("#%d Game %s is completed [%dms]\n", dup + 1, gameName, (ag1_total_time + ag2_total_time));
        }
        out.close();
    }
}

