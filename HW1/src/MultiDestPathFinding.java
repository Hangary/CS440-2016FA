import java.io.FileNotFoundException;

/**
 * Created by qixinzhu on 9/17/16.
 */
public class MultiDestPathFinding {
    public static void main(String[] args) throws FileNotFoundException {

        String[] toSolve = {"tinySearch.txt", "smallSearch.txt", "mediumSearch.txt"};// {"testGraph.txt"};//
        // {"bigDots.txt"};//{"mediumSearch.txt"}; //

        for (int i=0; i<toSolve.length; i++) {
            String fileName = toSolve[i];
            Graph g = MazeReader.ReadMazeAndBuildGraph(fileName);
            for (String s : g.getOriginalMaze()) {
                System.out.println(s);
            }
            //System.out.println(g);
            System.out.printf("Number of destinations: %d\n\n", g.getDests().size());


            runSearch("AStar", g);


            MazePrinter.PrintMultiDestMaze(g, fileName.substring(0,fileName.indexOf('.')) + "_output.txt");
        }
    }

    private static void runSearch(String Algo, Graph g) {
        long time0 = System.currentTimeMillis();
        if (Algo.equals("BFS")) BFS.multiBFS(g);
        else if (Algo.equals("AStar")) rewriteAStar.multiAStr(g);
        long time1 = System.currentTimeMillis();
        System.out.printf("Total search time is %d ms\n", time1 - time0);
    }
}
