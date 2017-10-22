/**
 * Created by youwu on 9/16/16.
 */
//package cubeSolver;
import java.util.*;

public class CubeAStar {
    private static Set<CubeNode> visited;
    public static String AStar(CubeNode start, CubeNode goal) {
        visited = new HashSet<>();
        CubeNode mb=new CubeNode(Cube.GOAL.toCharArray(),0);
        Queue<CubeNode> toVisit = new PriorityQueue<>();
        long expandNodeNum = 0;
        start.g = 0;
        toVisit.add(start);
        while (!toVisit.isEmpty()) {
            CubeNode curNode = toVisit.remove();
            if (visited.contains(curNode)) continue;
            visited.add(curNode);
            expandNodeNum++;
            for (CubeNode nd : CubeNode.getSuccessors(curNode)) {
                nd.g = curNode.g + 1;
                /**
                 * replace the following line with function call:
                 *  nd.heuristic =  calculateHeuristic(nd, goal);
                 */
                //nd.heuristic=(double)0;
                nd.heuristic=CubeNode.hfunction(nd,mb);
                toVisit.add(nd);
                // find target
                if (nd.equals(goal)) {
                    System.out.printf("Number of expanded node is: %d\n", expandNodeNum);
                    return nd.path;
                    //return expandNodeNum;
                }
            }
        }
        return null;
    }
}