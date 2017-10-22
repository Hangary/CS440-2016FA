import java.util.*;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class Word implements Comparable<Word> {
    protected String word;
    //protected int possibleLocations;
    protected Queue<GameBoard> successorGames;

    /**
     * Default Comparator: successor GameBoards are ordered by (least) completed cell number
     */
    private class SuccessorComparator implements Comparator<GameBoard> {
        public int compare(GameBoard g1, GameBoard g2) {
            return g1.completedCells - g2.completedCells;
        }
    }

/*
    public Word(String s, int n) {
        word = s.toUpperCase();
        possibleLocations = n;
    }
*/

    public Word(String s, GameBoard g) {
        word = s.toUpperCase();
        successorGames = new PriorityQueue<>(new SuccessorComparator());
        //possibleLocations = this.newPossibleLocations(g);
        newPossibleLocations(g);
    }

    @Override
    public int compareTo(Word o) {
        return this.successorGames.size() - o.successorGames.size();
    }

    public void newPossibleLocations(GameBoard g) {
        int len = word.length();
        int count = 0;
        // check possible horizontal locations
        for (int col = 0; col <= GameBoard.GRID_SIZE - len; col++) {
            for (int row = 0; row < GameBoard.GRID_SIZE; row++) {
                boolean isValid = true;
                GameBoard newG = new GameBoard(g);
                List<Cell> newAdds = new ArrayList<>();
                // check if this word can be placed at location (row, col)
                boolean alreadyHas = true;
                for (int i = 0; i < len; i++) {
                    Cell old = g.board[row][col + i];
                    // old is available and feasible for this char
                    if (old.value == '_' && old.isFeasible(word.charAt(i))) {
                        // place the word in a copied GameBoard
                        Cell newC = new Cell(old, word.charAt(i));
                        newG.switchCell(newC);
                        newAdds.add(newC);
                        alreadyHas = false;
                    } else if (old.value != word.charAt(i)) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid) continue;
                //  then verify constraints
                newG.updateALlDomain(newAdds);
                if (newG.hasFailed()) continue;
                isValid = newG.verifyAllConstraints();
                if (isValid) {
                    count++;
                    if (alreadyHas) {
                        newG.addToSolution(word);
                    } else {
                        newG.addToSolution('H', row, col, word);
                    }
                    successorGames.add(newG);
                }
            }
        }
        // check possible vertical locations
        for (int row = 0; row <= GameBoard.GRID_SIZE - len; row++) {
            for (int col = 0; col < GameBoard.GRID_SIZE; col++) {
                boolean isValid = true;
                GameBoard newG = new GameBoard(g);
                List<Cell> newAdds = new ArrayList<>();
                // check if this word can be placed at location (row, col)
                boolean alreadyHas = true;
                for (int i = 0; i < len; i++) {
                    Cell old = g.board[row + i][col];
                    if (old.value == '_' && old.isFeasible(word.charAt(i))) {
                        // place the word in a copied GameBoard
                        Cell newC = new Cell(old, word.charAt(i));
                        newG.switchCell(newC);
                        newAdds.add(newC);
                        alreadyHas = false;
                    } else if (old.value != word.charAt(i)) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid) continue;
                //  then verify constraints
                newG.updateALlDomain(newAdds);
                if (newG.hasFailed()) continue;
                isValid = newG.verifyAllConstraints();
                if (isValid) {
                    count++;
                    if (alreadyHas) {
                        newG.addToSolution(word);
                    } else {
                        newG.addToSolution('V', row, col, word);
                    }
                    successorGames.add(newG);
                }
            }
        }

        //return count;
    }

    @Override
    public String toString() {
        return word + " - " + successorGames.size();
    }
}
