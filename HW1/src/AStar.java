import java.util.*;

/**
 * Created by qixinzhu on 9/17/16.
 */
public class AStar {
    private static Set<Node> visited;
    private static Node dest;

    private static class ManhattanComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            return a.getDistance() + HeuristicFunction.Manhattan(a, dest)
                    - b.getDistance() - HeuristicFunction.Manhattan(b, dest);
        }
    }

    public static void AStar(Graph g, Node start, Node end) {
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

    /**
     * Special Heuristic Function
     */

    public static Map<Node, Map<Node, Integer>> HMatrix;

    private static void constructHMatrix(Graph g) {
        HMatrix = new HashMap<>(g.getAllNodes().size());
        int destNum = g.getDests().size();
        for (Node nd : g.getAllNodes()) {
            HMatrix.put(nd, new HashMap<>(destNum));
            BFS.BFSall(g, nd);
            for (Node d : g.getDests()) {
                HMatrix.get(nd).put(d, d.getDistance());
            }
        }
    }

    private static class Entry implements Comparable<Entry> {
        private Node nd;
        private int distance;
        private double rank;
        private List<Node> path;
        private Set<Node> dests;
        private Set<Node> visited;

        Entry(Node n) {
            nd = n;
            distance = Integer.MAX_VALUE;
            rank = Integer.MAX_VALUE;
            path = new LinkedList<>();
            dests = new HashSet<>();
            visited = new HashSet<>();
        }

        public int compareTo(Entry other) {
            double EPSILON = 0.0000001;
            if (Math.abs(this.rank - other.rank) < EPSILON) return 0;
            else if (this.rank - other.rank < 0) return -1;
            else return 1;
        }

    }


    public static void multiAStar(Graph g) {
        /**
         * build HMatrix
         */
        constructHMatrix(g);

        int curBestLen = 1;

        Queue<Entry> frontier = new PriorityQueue<>();
        //HashMap<Node, Entry> validEntry = new HashMap<>();

        Entry start = new Entry(g.getStart());
        start.distance = 0;
        start.dests = new HashSet<>(g.getDests());
        start.path.add(g.getStart());
        frontier.add(start);
        //validEntry.put(g.getStart(), start);

        long time0 = System.currentTimeMillis();
        long time1 = System.currentTimeMillis();

        while (!frontier.isEmpty()) {
            Entry curEntry = frontier.remove();
            Node curNode = curEntry.nd;
            g.expandNodeNum++;

            /**
             *  ----------------------------------
             */
            /*
            if (validEntry.get(curNode) == curEntry) {
                validEntry.remove(curNode);
            } else continue;
            */

            curEntry.visited.add(curNode);
            // check goal state
            if (curEntry.dests.contains(curNode)) {
                curEntry.dests.remove(curNode);
                curEntry.path.add(curNode);
                curEntry.visited = new HashSet<>();
                curEntry.visited.add(curNode);
                if (curEntry.path.size() > curBestLen) {
                    curBestLen = curEntry.path.size();
                    time1 = System.currentTimeMillis();
                    System.out.printf("Find current longest path of length %d [%dms]\n", curBestLen, time1 - time0);
                    System.out.printf("\tcurrent expand node num = %d, frontier size = %d\n", g.expandNodeNum, frontier.size());
                    time0 = System.currentTimeMillis();
                }
            }
            if (curEntry.dests.size() < 1) {
                g.visitOrder = new LinkedList<>(curEntry.path);
                g.pathLen = curEntry.distance;
                return;
            }

            int dist = curEntry.distance + 1;
            if (g.getNeighbors(curNode).length < 1) g.expandNodeNum--;
            for (Edge e : g.getNeighbors(curNode)) {
                Node nd = e.getDestination();
                if (!curEntry.visited.contains(nd)) {
                    /**
                     * this is the place to change Heuristic Function usage
                     */
                    double estimate = HeuristicFunction.minManhattanPlus(nd, curEntry.dests);

                    Entry next = new Entry(nd);
                    next.distance = dist;
                    next.rank = dist + estimate;
                    next.dests = new HashSet<>(curEntry.dests);
                    next.path = new LinkedList<>(curEntry.path);
                    next.visited = new HashSet<>(curEntry.visited);

                    frontier.add(next);
                    /*
                    if (!validEntry.containsKey(nd) || validEntry.get(nd).rank > next.rank) {
                        validEntry.put(nd, next);

                    }
                    */
                }
            }
        }
    }

    public static HashMap<Node, Integer> m;
    public static int[][] pathLenth;

    /**
     * -------------- why this is slow?
     * @param g
     */

    public static void multiAStar2(Graph g) {

        int nodeNum = g.getDests().size() + 1;
        m = new HashMap<>(nodeNum);
        pathLenth = new int[nodeNum][nodeNum];

        int i = 0;
        m.put(g.getStart(), i++);
        for (Node n : g.getDests()) m.put(n, i++);

        for (Node nd1 : m.keySet()) {
            BFS.BFSall(g, nd1);
            int l = m.get(nd1);
            for (Node nd2 : m.keySet()) {
                int k = m.get(nd2);
                pathLenth[l][k] = nd2.getDistance();
                pathLenth[k][l] = nd2.getDistance();
            }
        }

        int curBestLen = 1;

        Queue<Entry> frontier = new PriorityQueue<>();

        Entry start = new Entry(g.getStart());
        start.distance = 0;
        start.dests = new HashSet<>(g.getDests());
        frontier.add(start);

        while (!frontier.isEmpty()) {
            Entry curEntry = frontier.remove();
            Node curNode = curEntry.nd;
            g.expandNodeNum++;

            curEntry.visited.add(curNode);
            curEntry.dests.remove(curNode);
            curEntry.path.add(curNode);

            if (curEntry.path.size() > curBestLen) {
                curBestLen = curEntry.path.size();
                System.out.printf("Find current longest path of length %d\n", curBestLen);
            }
            // check goal state
            if (curEntry.dests.size() < 1) {
                g.visitOrder = new LinkedList<>(curEntry.path);
                g.pathLen = curEntry.distance;
                return;
            }

            for (Node nd : curEntry.dests) {
                if (!curEntry.visited.contains(nd)) {
                    /**
                     * this is the place to change Heuristic Function usage
                     */
                    double estimate = HeuristicFunction.twoFurthestDest2(nd, curEntry.dests);

                    int dist = curEntry.distance + pathLenth[m.get(curNode)][m.get(nd)];

                    Entry next = new Entry(nd);
                    next.distance = dist;
                    next.rank = dist + estimate;
                    next.dests = new HashSet<>(curEntry.dests);
                    next.path = new LinkedList<>(curEntry.path);
                    next.visited = new HashSet<>(curEntry.visited);

                    frontier.add(next);
                }
            }
        }
    }
}
