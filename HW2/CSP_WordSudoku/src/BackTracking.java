import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class BackTracking {

    public static int UNUSED_NUM = 1;

    public static GameBoard BackTracking(GameBoard g, WordBank wb, Set<String> discard) {
        if (g.isComplete()) {
            GameBoard.discardSet = new HashSet<>(discard);
            return g;
        }
        GameBoard.expandNodeNum++;
        while (!wb.isEmpty()) {
            Word w = wb.getNextWord();
            while (!w.successorGames.isEmpty()) {
                GameBoard newGB = w.successorGames.poll();
                //if (GameBoard.expandNodeNum % 1000 == 0) System.out.printf("current expande node: %d\n", GameBoard.expandNodeNum);
                WordBank newWB = new WordBank(wb, newGB);
                GameBoard result = BackTracking(newGB, newWB, new HashSet<>(discard));
                if (result != null) return result;
            }
            //System.out.printf("%s has been explored\n",w.toString());
            discard.add(w.word);
            if (discard.size() >= UNUSED_NUM) {
                //System.out.println(discard.size());
                //GameBoard.discardSet = new HashSet<>(discard);
                //----------
                return null;
            }
            if (wb.remainChar + g.completedCells < GameBoard.GRID_SIZE*GameBoard.GRID_SIZE) {
                return null;
            }
        }
        return null;
    }

    public static void findAllSolution(GameBoard g, WordBank wb) {
        if (g.isComplete()) {
            GameBoard.allSolution.add(g);
            return;
        }
        GameBoard.expandNodeNum++;
        Word w = wb.getNextWord();
        while (w != null) {
            while (!w.successorGames.isEmpty()) {
                GameBoard newGB = w.successorGames.poll();
                if (GameBoard.expandNodeNum % 1000 == 0) {
                    System.out.printf("current expande node: %d\n", GameBoard.expandNodeNum);
                    if (!GameBoard.allSolution.isEmpty())
                        System.out.printf("current best solution size: %d\n", GameBoard.allSolution.peek().solutionSize);
                }
                WordBank newWB = new WordBank(wb, newGB);
                findAllSolution(newGB, newWB);
                //if (result != null) return result;
            }
            g.addToSolution(w.word);
            if (wb.remainChar + g.completedCells < GameBoard.GRID_SIZE*GameBoard.GRID_SIZE) {
                return;
            }
            WordBank update = new WordBank(wb, g);
            wb = update;
            w = wb.getNextWord();
        }
        return;
    }

    public static GameBoard uniqueSolution(GameBoard g, WordBank wb) {
        GameBoard solutionG = BackTracking.BackTracking(g, new WordBank(wb, g), new HashSet<>());
        Set<String> totalDiscard = new HashSet<>();
        // throw any words cannot fit in
        while (true) {
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
                // filter out words auto-fit in
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

        // try to throw one word at a time
        Queue<WordBank> wbs = new LinkedList<>();
        wbs.add(wb);
        while (!wbs.isEmpty()) {
            wb = wbs.poll();
            for (Word w : wb.words) {
                String word = w.word;
                WordBank newWB = new WordBank(wb, g);
                newWB.removeWord(word);
                GameBoard validSol = BackTracking.BackTracking(g, newWB, new HashSet<>());
                if (validSol != null) {
                    wbs.add(newWB);
                    solutionG = validSol;
                    totalDiscard.add(w.word);
                }
            }
        }

        for (String word : totalDiscard) {
            solutionG.addToSolution(word);
        }
        return solutionG;
    }


}
