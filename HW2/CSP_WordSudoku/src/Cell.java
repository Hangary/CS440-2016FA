import java.util.*;

/**
 * Created by qixinzhu on 10/1/16.
 */
public class Cell {
    public static final int LETTER_NUM = 26;
    protected int number;
    protected int row;
    protected int col;
    protected char value;
    private boolean[] feasibleDomain;
    protected int domainSize;

    public Cell(int r, int c, char v) {
        row = r;
        col = c;
        number = row * GameBoard.GRID_SIZE + col;
        value = Character.toUpperCase(v);
        feasibleDomain = new boolean[LETTER_NUM];
        Arrays.fill(feasibleDomain, Boolean.TRUE);
        domainSize = LETTER_NUM;
        if (value != '_') this.excludeLetter(value);

    }

    public Cell(Cell other) {
        this.row = other.row;
        this.col = other.col;
        this.number = other.number;
        this.value = other.value;
        this.domainSize = other.domainSize;
        this.feasibleDomain = Arrays.copyOf(other.feasibleDomain, other.feasibleDomain.length);
    }

    public Cell(Cell other, char v) {
        this(other);
        this.value = v;
        if (value != '_') this.excludeLetter(value);
    }

    public void excludeLetter(char c) {
        char C = Character.toUpperCase(c);
        if (feasibleDomain[C - 'A']) {
            domainSize--;
            feasibleDomain[C - 'A'] = false;
        }
    }

    public boolean isFeasible(char c) {
        return feasibleDomain[c-'A'];
    }

    @Override
    public String toString() {
        return "" + value + "(" + row + "," + col + ")";
    }

}
