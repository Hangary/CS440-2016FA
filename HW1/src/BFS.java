import java.util.*;

/**
 * Created by qixinzhu on 9/16/16.
 */
public class BFS {

    public static void BFS(Graph g, Node start, Node end) {

        Set<Node> visited = new HashSet<>();

        Queue<Node> toVisit = new LinkedList<>();   // choose Queue implementation
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
                if (nd.equals(end))
                    return;
            }
        }
        System.out.println("Path doesn't exist!");
    }

    private static Map<Node, Integer> m;
    private static int[][] pathLenth;

    public static void multiBFS(Graph g) {
        int nodeNum = g.getDests().size() + 1;
        m = new HashMap<>(nodeNum);
        int i = 0;
        m.put(g.getStart(), i++);
        for (Node n : g.getDests()) m.put(n, i++);
        pathLenth = new int[nodeNum][nodeNum];

        for (Node n : g.getDests()) {
            Node start = g.getStart();
            BFS(g, start, n);
            int l = m.get(start);
            int k = m.get(n);
            pathLenth[l][k] = n.getDistance();
            pathLenth[k][l] = n.getDistance();
        }

        for (Node n1 : g.getDests()) {
            for (Node n2 : g.getDests()) {
                if (!n1.equals(n2)) {
                    BFS(g, n1, n2);
                    int l = m.get(n1);
                    int k = m.get(n2);
                    pathLenth[l][k] = n2.getDistance();
                    pathLenth[k][l] = n2.getDistance();
                }
            }
        }

        Stack<Node> path = new Stack<>();
        path.push(g.getStart());
        multiBFShelper(g, 0, path, g.getDests());
    }

    public static void multiBFShelper(Graph g, int pLength, Stack<Node> path, List<Node> dests) {
        g.expandNodeNum++;

        if (dests.size() == 1) {
            Node end = dests.get(0);
            Node start = path.peek();
            int l = m.get(start);
            int k = m.get(end);
            int curLen = pathLenth[l][k];
            if (pLength + curLen < g.pathLen) {
                g.pathLen = pLength + curLen;
                g.visitOrder = new LinkedList<>(path);
                g.visitOrder.add(end);
            }
        }
        else {
            if (path.size() == 2) System.out.printf("1st Level Node: %s\n",path.peek().toString());
            else if (path.size() == 3) System.out.printf("\t2nd Level Node: %s\n",path.peek().toString());
            for (Node nd : dests) {
                Node start = path.peek();
                int l = m.get(start);
                int k = m.get(nd);
                int curLen = pathLenth[l][k];
                List<Node> newDests = new ArrayList<>(dests);
                newDests.remove(nd);

                path.push(nd);
                multiBFShelper(g, pLength+curLen, path, newDests);
                path.pop();
            }
        }
    }

    public static void BFSall(Graph g, Node start) {
        Set<Node> visited = new HashSet<>();

        Queue<Node> toVisit = new LinkedList<>();   // choose Queue implementation
        start.setDistance(0);
        visited.add(start);
        toVisit.add(start);
        while (!toVisit.isEmpty()) {
            Node curNode = toVisit.remove();
            for (Edge e : g.getNeighbors(curNode)) {
                Node nd = e.getDestination();
                if (!visited.contains(nd)) {
                    visited.add(nd);
                    nd.setDistance(curNode.getDistance() + 1);
                    toVisit.add(nd);
                }
            }
        }
    }
}
