import java.util.*;

/**
 * Created by qixinzhu on 9/16/16.
 */
public class DFS {

    private static Set<Node> visited;

    public static void DFS(Graph g, Node start, Node end) {
        visited = new HashSet<>();
        dfsHelper(g, start, end, 0);
    }

    private static boolean dfsHelper(Graph g, Node start, Node end, int dist) {
        g.expandNodeNum++;
        visited.add(start);
        start.setDistance(dist);
        if (start.equals(end)) return true;
        if (g.getNeighbors(start).length < 1) g.expandNodeNum--;
        for (Edge e : g.getNeighbors(start)) {
            Node nd = e.getDestination();
            if (!visited.contains(nd)) {
                nd.setParent(start);
                if (dfsHelper(g, nd, end, dist + 1)) return true;
                else visited.remove(nd);
            }
        }
        return false;
    }

    public static void IDS(Graph g, Node start, Node end) {
        visited = new HashSet<>();
        boolean found = false;
        int limit = 0;
        while (!found) {
            //System.out.printf("path length limit = %d\n",limit);
            if (idsHelper(g, start, end, 0, limit)) found = true;
            limit++;
        }
    }

    private static int Manhattan(Node nd, Node end) {
        return Math.abs(nd.getRow() - end.getRow()) + Math.abs(nd.getCol() - end.getCol());
    }

    private static boolean idsHelper(Graph g, Node start, Node end, int dist, int limit) {
        visited.add(start);
        start.setDistance(dist);
        g.expandNodeNum++;
        if (start.equals(end)) return true;
        /**
         * use Manhattan Distance here to help pruning non-optimum branches
         */
        else if (Manhattan(start, end) + dist > limit) {
            visited.remove(start);
            return false;
        }

        if (g.getNeighbors(start).length < 1) g.expandNodeNum--;
        for (Edge e : g.getNeighbors(start)) {
            Node nd = e.getDestination();
            if (!visited.contains(nd)) {
                nd.setParent(start);
                if (idsHelper(g, nd, end, dist + 1, limit)) return true;
                else visited.remove(nd);
            }
        }
        return false;
    }
}
