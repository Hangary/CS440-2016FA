import java.util.*;

/**
 * Created by qixinzhu on 9/23/16.
 */
public class CubeSolver {

    private static long expandNodeNum;
    private static Cube c;

    public static CubeNode CubeBFS(CubeNode start) {
        int maxPathLen = 0;
        Set<CubeNode> visited = new HashSet<>();
        Queue<CubeNode> toVisit = new LinkedList<>();
        visited.add(start);
        toVisit.add(start);
        while (!toVisit.isEmpty()) {
            CubeNode curNode = toVisit.remove();
            expandNodeNum++;
            if (curNode.isGoal(c)) return curNode;
            /*
            if (curNode.path.length() > maxPathLen) {
                maxPathLen = curNode.path.length();
                System.out.printf("[%d] Current path length is: %d\n", expandNodeNum, maxPathLen);
            }
            */
            for (CubeNode nd : CubeNode.getSuccessors(curNode)) {
                if (!visited.contains(nd)) {
                    visited.add(nd);
                    toVisit.add(nd);
                }

            }
        }
        System.out.println("Path doesn't exist!");
        return null;
    }

    public static void main(String[] args) {
        /*
        char[] cube1 = {'y', 'o', 'r', 'r', 'p', 'b', 'y', 'g', 'o', 'o', 'r', 'b', 'b', 'b',
                'o', 'g', 'g', 'p', 'g', 'p', 'p', 'r', 'y', 'y'};
        c = new Cube(cube1);
        */
        c = new Cube("cube1_1.txt");
        //System.out.printf("%s\n", c);
        /*
        for (CubeNode nd : CubeNode.getSuccessors(new CubeNode(c.state, 0))) {
            System.out.printf("%s\n", nd);
        }
        */

        long time0 = System.currentTimeMillis();
        CubeNode dest = CubeBFS(new CubeNode(c.state, 0));

        System.out.println(dest.path);
        System.out.println(expandNodeNum);

        long time1 = System.currentTimeMillis();
        System.out.printf("Total search time is %d ms\n", time1 - time0);
    }
}
