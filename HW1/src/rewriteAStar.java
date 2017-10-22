import java.util.*;

/**
 * Created by qixinzhu on 9/20/16.
 */
public class rewriteAStar {
    /**
     * internal use static data
     */
    public static int[][] dMatrix;
    public static Graph mstG;
    public static int goalNum;

    private static void constructMatrix(Graph g) {
        int n = g.getAllNodes().size();
        int d = g.getDests().size();
        goalNum = d;
        int i = 0;
        for (Node nd : g.getDests()) nd.nodeNum = i++;  // goals number from 0 ~ (goalNum - 1)
        g.getStart().nodeNum = i++;
        for (Node nd : g.getAllNodes()) if (nd.getName().equals(" ")) nd.nodeNum = i++;
        // assert(i == n);

        dMatrix = new int[n][d];
        for (Node nd : g.getAllNodes()) {
            BFS.BFSall(g, nd);
            for (Node nd2 : g.getDests()) {
                dMatrix[nd.nodeNum][nd2.nodeNum] = nd2.getDistance();
            }
        }
    }

    private static void constructMstG(Graph g) {
        mstG = new Graph(g.getDests().size());
        for (Node nd2 : g.getDests()) {
            mstG.addNode(new Node(nd2));
        }
        for (Node nd2 : mstG.getAllNodes()) {
            for (Node nd3 : mstG.getAllNodes()) {
                if (nd3.nodeNum > nd2.nodeNum)
                    mstG.addEdge(new Edge(nd2, nd3, dMatrix[nd2.nodeNum][nd3.nodeNum]));
            }
        }
    }

    /**
     * State = current nd position + goals to be explored
     */
    private static class State implements Comparable<State> {
        private Node nd;
        private int distance;
        private double rank;
        private List<Node> path;
        private boolean[] goals;
        private int remainGoals;
        private Node lastStep;

        State(Node n) {
            nd = n;
            distance = 0;
            rank = Integer.MAX_VALUE;
            path = new LinkedList<>();
            goals = new boolean[goalNum];
            Arrays.fill(goals, Boolean.TRUE);
            remainGoals = goalNum;
            lastStep = null;
        }

        State(State other) {
            this.nd = other.nd;
            this.distance = other.distance;
            this.rank = other.rank;
            path = new LinkedList<>(other.path);
            goals = Arrays.copyOf(other.goals, other.goals.length);
            remainGoals = other.remainGoals;
            lastStep = other.nd;
        }

        public int compareTo(State other) {
            double EPSILON = 0.0000001;
            if (Math.abs(this.rank - other.rank) < EPSILON) return 0;
            else if (this.rank - other.rank < 0) return -1;
            else return 1;
        }

        public int hashCode() {
            int h = nd.hashCode();
            for (int i = 0; i < goals.length; i++) {
                h += (goals[i]) ? 1 << i : 0;
            }
            return h;
        }

        public boolean equals(Object other) {
            if (!(other instanceof State)) return false;

            State o = (State) other;
            if (!this.nd.equals(o.nd)) return false;
            if (this.remainGoals != o.remainGoals) return false;
            for (int i = 0; i < this.goals.length; i++) {
                if (this.goals[i] != o.goals[i]) return false;
            }
            return true;
        }

    }


    /**
     * find Minimum Spanning Tree using Prim's algorithm
     * @param g
     * @param goals
     * @return
     */
    private static int MSTPrim(Graph g, boolean[] goals) {
        int treeCost = 0;

        Set<Node> remainGoals = new HashSet<>();   // MSTree only expand remainGoals

        Set<Node> visited = new HashSet<>();
        for (Node n : g.getAllNodes()) {
            if (goals[n.nodeNum]) remainGoals.add(n);
            else visited.add(n);
        }
        PriorityQueue<Edge> availableEdges = new PriorityQueue<>();
        List<Node> MST = new ArrayList<>();

        Node curNode = (remainGoals.toArray(new Node[0]))[new Random().nextInt(remainGoals.size())];     // start node
        MST.add(curNode);
        while (MST.size() < remainGoals.size()) {
            visited.add(curNode);
            for (Edge e : g.getNeighbors(curNode)) {
                if (!visited.contains(e.getDestination())) {
                    availableEdges.add(e);
                }
            }
            Edge addToTree;
            do {
                addToTree = availableEdges.poll();
            }
            while (visited.contains(addToTree.getDestination()));   // don't pick this edge if the dest is already in MST
            treeCost += addToTree.getWeight();
            curNode = addToTree.getDestination();
            MST.add(curNode);
        }
        return treeCost;
    }


    /**
     * --- H7
     * Heuristic:   (1) the Minimum Spanning Tree of all destination left +
     * (2) the distance from here to the closest destination
     * Admissibility: Yes, you need to take at least (2) to get to them and than (1) to visit both of them
     *
     * @param nd
     * @param goals
     * @return
     */
    public static int heuristicMST(Node nd, boolean[] goals) {
        int closestDist = Integer.MAX_VALUE;
        for (int i=0; i<goals.length; i++) {
            int dist = dMatrix[nd.nodeNum][i];
            if (dist < closestDist && goals[i]) closestDist = dist;
        }
        int mstCost = MSTPrim(mstG, goals);
        return closestDist + mstCost;
    }

    public static int maxOfTwoH(Node nd, boolean[] goals) {
        int closestDist = Integer.MAX_VALUE;
        for (int i=0; i<goals.length; i++) {
            int dist = dMatrix[nd.nodeNum][i];
            if (dist < closestDist && goals[i]) closestDist = dist;
        }
        int mstCost = MSTPrim(mstG, goals);

        int far1 = 0;
        int far2 = 0;
        int maxDist = -1;
        for (int i=0; i<goals.length; i++) {
            for (int j=i; j<goals.length; j++) {
                int curDist = dMatrix[i][j];
                if (curDist > maxDist && goals[i] && goals[j]) {
                    maxDist = curDist;
                    far1 = i;
                    far2 = j;
                }
            }
        }
        int hereToClosest = Math.min(dMatrix[nd.nodeNum][far1], dMatrix[nd.nodeNum][far2]);
        return Math.max(closestDist + mstCost, maxDist + hereToClosest);
    }

    /**
     * for suboptimum trial -- not admissible
     * @param nd
     * @param goals
     * @return
     */
    public static double maxOfTwoH2(Node nd, boolean[] goals) {
        int closestDist = Integer.MAX_VALUE;
        for (int i=0; i<goals.length; i++) {
            int dist = dMatrix[nd.nodeNum][i];
            if (dist < closestDist && goals[i]) closestDist = dist;
        }
        int mstCost = MSTPrim(mstG, goals);

        int far1 = 0;
        int far2 = 0;
        int maxDist = -1;
        for (int i=0; i<goals.length; i++) {
            for (int j=i; j<goals.length; j++) {
                int curDist = dMatrix[i][j];
                if (curDist > maxDist && goals[i] && goals[j]) {
                    maxDist = curDist;
                    far1 = i;
                    far2 = j;
                }
            }
        }
        int hereToClosest = Math.min(dMatrix[nd.nodeNum][far1], dMatrix[nd.nodeNum][far2]);
        return 1.25 * Math.max(closestDist + mstCost, maxDist + hereToClosest);
    }


    /**
     * Rewrite multiAStar
     */
    public static void multiAStr(Graph g) {
        constructMatrix(g);
        constructMstG(g);
        g.expandNodeNum = 0;

        int curBestLen = 1;
        Queue<State> frontier = new PriorityQueue<>();
        Map<State, Double> exploredSet = new HashMap<>();

        State curState = new State(g.getStart());
        curState.path.add(g.getStart());
        frontier.add(curState);

        long time0 = System.currentTimeMillis();
        long time1 = System.currentTimeMillis();

        while (!frontier.isEmpty()) {
            curState = frontier.remove();
            // check if a better state has been explored
            if (!exploredSet.containsKey(curState) || exploredSet.get(curState) > curState.rank) {
                Node curNode = curState.nd;
                g.expandNodeNum++;
                exploredSet.put(curState, curState.rank);

                // find a goal, remove from goals[]
                if (curNode.nodeNum < goalNum && curState.goals[curNode.nodeNum]) {
                    curState.goals[curNode.nodeNum] = false;
                    curState.remainGoals--;
                    curState.path.add(curNode);
                    curState.lastStep = curNode;
                    /*
                    if (curState.path.size() > curBestLen) {
                        curBestLen = curState.path.size();
                        time1 = System.currentTimeMillis();
                        System.out.printf("Current best path length = %d [%dms]\n", curBestLen, time1 - time0);
                        System.out.printf("\tcurrent expandNodeNum = %d, frontier size = %d\n", g.expandNodeNum, frontier.size());
                        time0 = System.currentTimeMillis();
                    }
                    */
                }

                // goal state: all goals are explored
                if (curState.remainGoals < 1) {
                    g.visitOrder = new LinkedList<>(curState.path);
                    g.pathLen = curState.distance;
                    return;
                }

                int dist = curState.distance + 1;
                if (g.getNeighbors(curNode).length < 1) g.expandNodeNum--;
                for (Edge e : g.getNeighbors(curNode)) {
                    Node node = e.getDestination();
                    if (node.equals(curState.lastStep)) continue;
                    /**
                     * Heuristic function
                     */
                    double estimate = maxOfTwoH(node, curState.goals);
                    State next = new State(curState);
                    next.nd = node;
                    next.distance = dist;
                    next.rank = dist + estimate;
                    frontier.add(next);
                }
            }
        }
    }
}

// rewriteAStar.multiAStr(g);
