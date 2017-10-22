import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by qixinzhu on 9/11/16.
 */
public class SingleDestPathFinding {
    public static void main(String[] args) throws FileNotFoundException {

        String[] toSolve = {"mediumMaze.txt", "bigMaze.txt", "openMaze.txt"};

        for (int i=0; i<toSolve.length; i++) {
            String fileName = toSolve[i];
            Graph g = MazeReader.ReadMazeAndBuildGraph(fileName);
            for (String s : g.getOriginalMaze()) {
                System.out.println(s);
            }
            //System.out.println(g);

            List<Node> dests = g.getDests();
            if (dests.size() < 1) {
                System.out.println("Error: no destination node found!");
                System.exit(-1);
            }
            Node d = dests.get(0);

            runSearch("AStar", g, g.getStart(), d);

            System.out.println();

            MazePrinter.PrintSingleDestMaze(g, fileName.substring(0,fileName.indexOf('.')) + "_output.txt");
        }


    }

    private static void runSearch(String Algo, Graph g, Node start, Node dest) {
        long time0 = System.currentTimeMillis();
        if (Algo.equals("BFS")) BFS.BFS(g, start, dest);
        else if (Algo.equals("DFS")) DFS.DFS(g, start, dest);
        else if (Algo.equals("IDS")) DFS.IDS(g, start, dest);
        else if (Algo.equals("Greedy")) Greedy.Greedy(g, start, dest);
        else if (Algo.equals("AStar")) AStar.AStar(g, start, dest);
        long time1 = System.currentTimeMillis();
        System.out.printf("Total search time is %d ms\n", time1 - time0);
    }
}
