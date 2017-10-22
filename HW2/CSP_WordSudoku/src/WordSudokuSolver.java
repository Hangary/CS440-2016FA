import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class WordSudokuSolver {
    public static void main(String[] args) throws FileNotFoundException {
        int caseNum = 3 + 1;
        for (int i = 3; i < caseNum; i++) {
            GameBoard.expandNodeNum = 0;

            //int i = 3;

            String gridName = "CSP_WordSudoku/grid" + (i + 1) + ".txt";
            String bankName = "CSP_WordSudoku/bank" + (i + 1) + ".txt";
            GameBoard g = new GameBoard(gridName);
            boolean verified = g.verifyAllConstraints();
            System.out.printf("g is verified: %b, completed = %d\n", verified, g.completedCells);

            WordBank wb = new WordBank(bankName, g);

/*
        while (i == 2 || i == 3) {
            if (solutionG == null) {
                BackTracking.UNUSED_NUM++;
                System.out.printf("BackTracking.UNUSED_NUM is : %d\n", BackTracking.UNUSED_NUM);
                System.out.printf("WordBank size is :%d\n", wb.words.size());
                //for (String s : totalDiscard) System.out.println(s);
            } else {          // can find solution
                BackTracking.UNUSED_NUM = 1;
                boolean endSearch = true;
                if (!GameBoard.discardSet.isEmpty()) {
                    endSearch = false;
                    totalDiscard.addAll(GameBoard.discardSet);
                    for (String word : GameBoard.discardSet) wb.removeWord(word);
                }
                for (String s : solutionG.solution) {
                    if (s.indexOf("(N/A)") != -1) {
                        endSearch = false;
                        String word = s.substring(s.indexOf(": ") + 2);
                        totalDiscard.add(word);
                        wb.removeWord(word);
                    }
                }
                System.out.printf("BackTracking.UNUSED_NUM is : %d\n", BackTracking.UNUSED_NUM);
                System.out.printf("WordBank size is :%d\n", wb.words.size());
                if (endSearch) break;
            }
            solutionG = BackTracking.BackTracking(g, new WordBank(wb, g), new HashSet<>());
        }
*/

            long time0 = System.currentTimeMillis();
            //Set<String> totalDiscard = new HashSet<>();
            GameBoard solutionG;
            if (i < 2) {
                solutionG = BackTracking.BackTracking(g, new WordBank(wb, g), new HashSet<>());
            }
            else if (i == 2){
                BackTracking.findAllSolution(g, new WordBank(wb, g));
                solutionG = GameBoard.allSolution.poll();
            }
            else if (i == 3) {      // testing algorithm for unique solution case
                solutionG = BackTracking.uniqueSolution(g, new WordBank(wb, g));
            }
            else {
                solutionG = null;
            }

            //System.out.println(solutionG);
            //System.out.printf("Number of nodes expanded: %d\n",GameBoard.expandNodeNum);
            //System.out.println(solutionG.getSolution());

            long time1 = System.currentTimeMillis();

            //totalDiscard.addAll(GameBoard.discardSet);
            String outputFileName = gridName.substring(0, gridName.indexOf('.')) + "_output.txt";
            PrintWriter out = new PrintWriter(outputFileName);
            out.printf("Case %d:\nTotal search time is %d ms, Number of nodes expanded: %d\n",
                    i + 1, time1 - time0, GameBoard.expandNodeNum);
            out.println(solutionG);
            if (solutionG != null) out.println(solutionG.getSolution());

            out.close();

        /*
        WordBank originalWB = new WordBank(bankName, g);
        while (originalWB.words.peek().successorGames.size() == 0) {
            originalWB.words.poll();
        }


        GameBoard curSol = null;
        Queue<WordBank> wbs = new LinkedList<>();
        WordBank wb = new WordBank(originalWB, g);
        BackTracking.UNUSED_NUM = 3;
        wbs.add(wb);

        String outputFileName = "CSP_WordSudoku/test.txt";
        PrintWriter out = new PrintWriter(outputFileName);

        int ll = 0;
        while (!wbs.isEmpty()) {
            wb = wbs.poll();
            System.out.println(wb.words.size());
            ll++;
            out.println(ll);
            out.println(wb);

            if (wb.words.size() < 20) BackTracking.UNUSED_NUM = 1;
            for (Word w : wb.words) {
                String word = w.word;
                WordBank newWB = new WordBank(originalWB, g);
                newWB.removeWord(word);
                GameBoard sol = BackTracking.BackTracking(g, newWB, new HashSet<>());
                if (sol != null) {
                    wbs.add(newWB);
                    curSol = sol;
                    out.println(sol);
                }
            }
        }
        */

        }


    }
}
