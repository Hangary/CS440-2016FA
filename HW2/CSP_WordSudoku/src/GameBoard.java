/**
 * Created by qixinzhu on 10/1/16.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GameBoard implements Comparable<GameBoard>{

    public static long expandNodeNum = 0;
    public static Set<String> discardSet = new HashSet<>();
    public static Queue<GameBoard> allSolution = new PriorityQueue<>();

    public static final int GRID_SIZE = 9;
    protected Cell[][] board;
    protected Stack<String> solution;
    protected int solutionSize ;
    protected int completedCells;
    protected boolean hasFailed;
    //public int expandNodeNum;
    //private Queue<Cell> cells;


    public GameBoard() {
        solutionSize = 0;
        board = new Cell[GRID_SIZE][GRID_SIZE];
        solution = new Stack<>();
        completedCells = 0;
        hasFailed = false;
        //cells = new PriorityQueue<>(new CellComparator());
    }

    public GameBoard(String fileName) throws FileNotFoundException {
        this();
        File inputFile = new File(fileName);
        Scanner in = new Scanner(inputFile);
        List<Cell> filledCells = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            String str = in.nextLine();
            for (int j = 0; j < GRID_SIZE; j++) {
                char c = Character.toUpperCase(str.charAt(j));
                board[i][j] = new Cell(i, j, c);
                if (c != '_') {
                    completedCells++;
                    filledCells.add(board[i][j]);
                }
                //cells.add(board[i][j]);
            }
        }
        in.close();
        //assert (completedCells + cells.size() == GRID_SIZE * GRID_SIZE);
        updateALlDomain(filledCells);
        System.out.println(this.toString());
    }

    public GameBoard(GameBoard other) {
        board = new Cell[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = new Cell(other.board[i][j]);
            }
        }
        solution = (Stack<String>) other.solution.clone();
        completedCells = other.completedCells;
        hasFailed = false;
        solutionSize = other.solutionSize;
    }
/*
    public Cell getNextCell() {
        return cells.remove();
    }
*/

    public void addToSolution(String word) {
        solution.push("(N/A): " + word);
    }

    public void addToSolution(char O, int R, int C, String word) {
        assert (O == 'H' || O == 'V');
        StringBuilder sb = new StringBuilder();
        sb.append(O);
        sb.append(',');
        sb.append(R);
        sb.append(',');
        sb.append(C);
        sb.append(": ");
        sb.append(word);
        solution.push(sb.toString());
        solutionSize++;
    }

    public boolean isComplete() {
        return (completedCells == GRID_SIZE * GRID_SIZE);
    }

    public boolean hasFailed() {
        return hasFailed;
    }

    public void switchCell(Cell newC) {
        board[newC.row][newC.col] = newC;
        completedCells++;
    }

    public String getSolution() {
        StringBuilder sb = new StringBuilder();
        Stack<String> tmp = new Stack<>();
        Stack<String> sol = (Stack<String>) solution.clone();
        while (!sol.isEmpty()) tmp.push(sol.pop());
        while (!tmp.isEmpty()) {
            sb.append(tmp.pop());
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean verifyAllConstraints() {
        boolean[][] rows = new boolean[GRID_SIZE][Cell.LETTER_NUM];
        boolean[][] cols = new boolean[GRID_SIZE][Cell.LETTER_NUM];
        boolean[][] blks = new boolean[GRID_SIZE][Cell.LETTER_NUM];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                char c = board[i][j].value;
                if (c == '_') continue;
                // -- check rows
                if (rows[i][c - 'A']) return false;
                else rows[i][c - 'A'] = true;
                // -- check columns
                if (cols[j][c - 'A']) return false;
                else cols[j][c - 'A'] = true;
                // -- check blocks
                if (blks[j / 3 + (i / 3) * 3][c - 'A']) return false;
                else blks[j / 3 + (i / 3) * 3][c - 'A'] = true;
            }
        }
        return true;
    }

    public void updateALlDomain(List<Cell> cells) {
        for (Cell c : cells) {
            int row = c.row;
            int col = c.col;
            for (int i = 0; i < GRID_SIZE; i++) {
                Cell updateRow = board[row][i];
                if (updateRow != c) updateRow.excludeLetter(c.value);
                Cell updateCol = board[i][col];
                if (updateCol != c) updateCol.excludeLetter(c.value);
                Cell updateBlk = board[(row / 3) * 3 + i / 3][(col / 3) * 3 + i % 3];
                if (updateBlk != c) updateBlk.excludeLetter(c.value);
                if (updateBlk.domainSize < 1 || updateCol.domainSize < 1 || updateRow.domainSize < 1) {
                    hasFailed = true;
                }
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(GRID_SIZE * (GRID_SIZE + 1));
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                sb.append((board[i][j]).value);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public int compareTo(GameBoard other) {
        return this.solutionSize - other.solutionSize;
    }
}
