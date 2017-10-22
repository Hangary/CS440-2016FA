import java.util.*;

/**
 * Created by qixinzhu on 9/16/16.
 */
public class HeuristicFunction {
    public static double dummy(Node nd, Node end) {
        return 0;
    }

    public static int Manhattan(Node nd, Node end) {
        return Math.abs(nd.getRow() - end.getRow()) + Math.abs(nd.getCol() - end.getCol());
    }

    /**
     * --- H1
     * Heuristic: Manhattan distance to the closest destination
     * Admissibility: Yes
     *
     * @param nd
     * @param dests
     * @return
     */
    public static double minManhattan(Node nd, Set<Node> dests) {
        int minDist = Integer.MAX_VALUE;
        for (Node nd2 : dests) {
            int dist = Manhattan(nd, nd2);
            if (dist < minDist) minDist = dist;
        }
        return minDist;
    }

    /**
     * --- H2
     * Heuristic: Manhattan distance to the closest destination + number of destination left
     * Admissibility: min_Manhattan + 1 * num_Dest < true path length
     *
     * @param nd
     * @param dests
     * @return
     */
    public static double minManhattanPlus(Node nd, Set<Node> dests) {
        int minDist = Integer.MAX_VALUE;
        for (Node nd2 : dests) {
            int dist = Manhattan(nd, nd2);
            if (dist < minDist) minDist = dist;
        }
        return minDist + dests.size();
    }

    /**
     * --- H3
     * Heuristic: actual shortest path length to the nearest destination + number of destinations left
     * Admissibility: Yes
     *
     * @param nd
     * @param dests
     * @return
     */
    public static double HMatrixFunc(Node nd, Set<Node> dests) {
        int minDist = Integer.MAX_VALUE;
        for (Node nd2 : dests) {
            int dist = AStar.HMatrix.get(nd).get(nd2);
            if (dist < minDist) minDist = dist;
        }
        return minDist + dests.size();
    }

    public static double HMatrixFunc2(Node nd, Set<Node> dests) {

        if (dests.size() == 1 && dests.contains(nd)) return 0;

        int minDist = Integer.MAX_VALUE;
        for (Node nd2 : dests) {
            if (!nd.equals(nd2)) {
                int l = AStar.m.get(nd);
                int k = AStar.m.get(nd2);
                int dist = AStar.pathLenth[l][k];
                if (dist < minDist) minDist = dist;
            }
        }
        return minDist + dests.size();
    }

    /**
     * --- H5
     * Heuristic: Manhattan distance to the closest destination + 2.0 * number of destination left
     * Admissibility: NO!
     *
     * @param nd
     * @param dests
     * @return
     */
    public static double minManhattanPlus2(Node nd, Set<Node> dests) {
        double minDist = Integer.MAX_VALUE;
        for (Node nd2 : dests) {
            double dist = Manhattan(nd, nd2);
            if (dist < minDist) minDist = dist;
        }
        return minDist + 2.0 * dests.size();
    }

    /**
     * --- H6
     * Heuristic:   (1) the distance between 2 furthest destination left +
     *              (2) the distance from here to the closest one of two above
     * Admissibility: Yes, you need to take at least (2) to get to them and than (1) to visit both of them
     *
     * @param nd
     * @param dests
     * @return
     */
    public static double twoFurthestDest(Node nd, Set<Node> dests) {
        Node far1 = null;
        Node far2 = null;
        int maxDist = -1;
        for (Node nd1 : dests) {
            for (Node nd2: dests) {
                int curDist = AStar.HMatrix.get(nd1).get(nd2);
                if (curDist > maxDist) {
                    maxDist = curDist;
                    far1 = nd1;
                    far2 = nd2;
                }
            }
        }
        int hereToClosest = Math.min(AStar.HMatrix.get(nd).get(far1), AStar.HMatrix.get(nd).get(far2));
        return maxDist + hereToClosest;
    }

    public static double twoFurthestDest2(Node nd, Set<Node> dests) {
        Node far1 = null;
        Node far2 = null;
        int maxDist = -1;
        for (Node nd1 : dests) {
            int l = AStar.m.get(nd1);
            for (Node nd2: dests) {
                int k = AStar.m.get(nd2);
                int curDist = AStar.pathLenth[l][k];
                if (curDist > maxDist) {
                    maxDist = curDist;
                    far1 = nd1;
                    far2 = nd2;
                }
            }
        }
        int hereToClosest = Math.min(AStar.pathLenth[AStar.m.get(nd)][AStar.m.get(far1)],
                AStar.pathLenth[AStar.m.get(nd)][AStar.m.get(far2)]);
        return maxDist + hereToClosest;
    }

}

