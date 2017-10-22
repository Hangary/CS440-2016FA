import java.util.*;

/**
 * Created by qixinzhu on 9/17/16.
 */
public class Greedy {
    private static Set<Node> visited;
    private static Node dest;

    private static class ManhattanComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            return HeuristicFunction.Manhattan(a,dest) - HeuristicFunction.Manhattan(b,dest);
        }
    }

    public static void Greedy(Graph g, Node start, Node end) {
        dest = end;
        visited = new HashSet<>();
        Queue<Node> toVisit = new PriorityQueue<>(new ManhattanComparator());

        start.setDistance(0);
        visited.add(start);
        toVisit.add(start);
        while (!toVisit.isEmpty()) {
            Node curNode = toVisit.remove();
            g.expandNodeNum++;
            if (g.getNeighbors(curNode).length < 1) g.expandNodeNum--;
            for (Edge e : g.getNeighbors(curNode)) {
                Node nd = e.getDestination();
                if (!visited.contains(nd)) {
                    visited.add(nd);
                    nd.setDistance(curNode.getDistance() + 1);
                    nd.setParent(curNode);
                    toVisit.add(nd);
                }

                // find target
                if (nd.equals(end)) return;
            }
        }
    }

}
