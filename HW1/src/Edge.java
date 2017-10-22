/**
 * Created by qixinzhu on 9/11/16.
 */
public class Edge implements Comparable<Edge> {
    private Node source;
    private Node destination;
    private int weight;
    private boolean visited;            // for iterative DFS

    public Edge(Node src, Node dest) {
        this(src, dest, 1);
    }

    public Edge(Node src, Node dest, int w) {
        source = src;
        destination = dest;
        weight = w;
        visited = false;
    }

    public void visit() {
        visited = true;
    }

    public void unvisit() {visited = false;}

    public boolean isVisited() {
        return visited;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public String toString() {
        return String.format("[%s--%s] (%d)", source, destination, weight);
    }

    public int compareTo(Edge e) {
        return this.weight - e.weight;
    }

}
