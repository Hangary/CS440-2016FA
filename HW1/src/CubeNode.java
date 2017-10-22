/**
 * Created by youwu on 9/16/16.
 */
//package cubeSolver;
import java.util.ArrayList;
import java.util.Map;

public class CubeNode implements Comparable<CubeNode> {
    /**
     * The state of the cube
     */
    public char[] state;
    /**
     * The heuristic value
     */
    public double heuristic;
    /**
     * The g value
     */
    public int g;
    /**
     * The path from the goal state to this node
     */
    public String path;
    /**
     *
     * @param state the state of the cube
     * @param heuristic the heuristic value
     */
    public CubeNode(char[] state, double heuristic) {
        this.state = state;
        this.heuristic = heuristic;
        this.g = 0;
        this.path = "";
    }
    /**
     *
     * @param state the state of the cube
     * @param heuristic the heuristic value
     * @param path the current path
     */
    public CubeNode(char[] state, double heuristic, String path) {
        this.state = state;
        this.heuristic = heuristic;
        this.g = 0;
        this.path = path;
    }
    /**
     *
     * @param state the state of the cube
     * @param heuristic the heuristic value
     * @param g the current g value
     * @param path the current path
     */
    public CubeNode(char[] state, double heuristic, int g, String path) {
        this.state = state;
        this.heuristic = heuristic;
        this.g = g;
        this.path = path;
    }
    /**
     * Generates all successors of the given node.
     * @param node the node to find successors for
     * @return an ArrayList<CubeNode> of all successors for
     * the param node
     */
    public static double hfunction(CubeNode i,CubeNode j){
       double cal=0;
       for(int k=0; k<24;k++)
       {if(i.state[k]!=j.state[k])
    	  cal++; }
    	return cal/12;
    }
    public static ArrayList<CubeNode> getSuccessors(CubeNode node) {
        ArrayList<CubeNode> successors = new ArrayList<CubeNode>();
        for (Map.Entry<Character, int[]> face : Cube.FACES.entrySet()) {
            // Make a clockwise turn
            char[] newState = Cube.rotate(node.state, face.getKey(), 1);
            successors.add(new CubeNode(newState,1,node.path + face.getKey() + " "));
            // Make a counterclockwise turn
            char[] newState1 = Cube.couterRotate(node.state, face.getKey(), 1);
            successors.add(new CubeNode(newState1,1,node.path + face.getKey() + "' "));
        }
        return successors;
    }

    @Override
    public int compareTo(CubeNode other) {
        double EPSILON = 0.000001;
        double this_rank = g + heuristic;
        double other_rank = other.g + other.heuristic;
        if (Math.abs(this_rank - other_rank) < EPSILON) return 0;
        else if (this_rank - other_rank < 0) return -1;
        else return 1;
    }
    public String toString() {
        return new String(this.state);
    }

    // IF not considering rotation invariance, use this hashcode

/*
    public int hashCode() {
    	return this.toString().hashCode();
    }
*/
    // IF considering rotation invariance, use this hashcode


    public int hashCode() {
    	return Cube.originState(this.state).hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof  CubeNode) {
            return this.hashCode() == other.hashCode();
        }
        return false;
    }

    public boolean isGoal(Cube c) {
        return this.toString().equals(c.GOAL);
    }
}