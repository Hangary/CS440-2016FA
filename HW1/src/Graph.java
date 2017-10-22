
import java.util.*;

/**
 * Created by qixinzhu on 9/12/16.
 */
public class Graph {
    protected Node start;
    protected List<Node> dests;
    protected String[] originalMaze;
    protected Map<Node, List<Edge>> edges;        // Adjacency list
    protected long expandNodeNum;

    protected List<Node> visitOrder;
    protected int pathLen;

    public Graph() {      // unknown nodeNum use TreeMap
        edges = new TreeMap<>();
        dests = new ArrayList<>();
        expandNodeNum = 0;
        pathLen = Integer.MAX_VALUE;
    }

    public Graph(int size) {     // know nodeNum  use HashMap
        edges = new HashMap<>(size, 0.25f);
        dests = new ArrayList<>();
        expandNodeNum = 0;
        pathLen = Integer.MAX_VALUE;
    }

    public void addNode(Node nd) {
        if (edges.containsKey(nd)) {
            throw new RuntimeException("Duplicate Node!");
        }
        edges.put(nd, new ArrayList<>());       // choose List implementation
        if (nd.getName() == "P") start = nd;
        if (nd.getName() == ".") dests.add(nd);
    }

    public void addEdge(Edge e) {
        if (!edges.containsKey(e.getSource()) || !edges.containsKey(e.getDestination())) {
            throw new RuntimeException("Non-existing Node!");
        }
        edges.get(e.getSource()).add(e);
        Edge reverse = new Edge(e.getDestination(), e.getSource(), e.getWeight());
        edges.get(e.getDestination()).add(reverse);
    }

    public Map<Node, List<Edge>> getEdges() {
        return edges;
    }


    public Edge[] getNeighbors(Node nd) {
        List<Edge> neighborEdges = edges.get(nd);
        if (neighborEdges == null) throw new RuntimeException("Non-existing Node!");
        return neighborEdges.toArray(new Edge[neighborEdges.size()]);
    }

    public boolean hasNode(Node nd) {
        return edges.containsKey(nd);
    }

    public Collection<Node> getAllNodes() {
        return edges.keySet();
    }

    public String toString() {
        String gString = "";
        for (Node nd : edges.keySet()) {
            for (Edge e : edges.get(nd)) {
                gString = gString + e + "\n";
            }
        }
        return gString;
    }

    public Node getStart() {
        return start;
    }

    public List<Node> getDests() {
        return new ArrayList<Node>(dests);
    }
    public void saveOriginalMaze(String[] maze) {
        originalMaze = maze;
    }
    public String[] getOriginalMaze() {
        return originalMaze;
    }


    /**
     * reset all nodes' parent and distance
     */
    protected void resetAllNodes() {
        for (Node nd : edges.keySet()) {
            nd.reset();
        }
    }

}
