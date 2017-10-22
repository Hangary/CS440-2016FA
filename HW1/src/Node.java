import java.util.*;

/**
 * Created by qixinzhu on 9/11/16.
 */
public class Node implements Comparable<Node> {

    public static int uniqueNum = 0;


    private String name;
    private int distance;        // used to record distance in a shortest path, or sccNum in DFS
    protected int nodeNum;
    private Node parent;            // used to backtrack shortest path
    private int row, col;

    public Node(String n) {
        this(n, -1, -1);
    }

    public Node(String n, int r, int c) {
        name = n;
        distance = Integer.MAX_VALUE;
        parent = null;
        row = r;
        col = c;
    }

    public Node(Node other) {
        this(other.name, other.row, other.col);
        nodeNum = other.nodeNum;
    }

    public int getRow() {return row;}

    public int getCol() {return col;}


    public void reset() {
        distance = Integer.MAX_VALUE;
        parent = null;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + "(" + row + ", " + col + ")";
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int d) {
        distance = d;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node nd) {
        parent = nd;
    }

    public boolean equals(Object other) {
        if (other instanceof Node) {
            return this.name.equals(((Node) other).name)
                    && this.row == ((Node) other).row && this.col == ((Node) other).col;
        }
        return false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public int compareTo(Node o) {
        return this.toString().compareTo(o.toString());
    }
}
