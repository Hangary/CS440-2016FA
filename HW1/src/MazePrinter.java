
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * Created by qixinzhu on 9/12/16.
 */
public class MazePrinter {
    public static void PrintSingleDestMaze(Graph g, String outputFileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);

        String[] tmp = g.getOriginalMaze();
        char[][] maze = new char[tmp.length][tmp[0].length()];
        for (int i = 0; i < tmp.length; i++) {
            maze[i] = tmp[i].toCharArray();
        }

        Node cur = g.getDests().get(0);
        while (cur.getParent() != null) {
            int r = cur.getParent().getRow();
            int c = cur.getParent().getCol();
            if (maze[r][c] != 'P') maze[r][c] = '.';
            cur = cur.getParent();
        }


        for (int i = 0; i < maze.length; i++) {
            out.println(maze[i]);
        }

        Node n = g.getDests().get(0);
        out.printf("\nDest_Node\t%s\n", n.toString());
        out.printf("Path length = %d\n", n.getDistance());
        out.printf("Number of nodes expanded: %d\n", g.expandNodeNum);

        out.close();
    }

    public static void PrintMultiDestMaze(Graph g, String outputFileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);

        String[] tmp = g.getOriginalMaze();
        char[][] maze = new char[tmp.length][tmp[0].length()];
        for (int i = 0; i < tmp.length; i++) {
            maze[i] = tmp[i].toCharArray();
        }

        for (int i=0;i<g.visitOrder.size();i++) {
            Node nd = g.visitOrder.get(i);
            char c;
            if (i<10) c = (char) ('0'+i);
            else if (i<36) c = (char) ('a' + i - 10);
            else c = (char) ('A' + i - 36);

            if (maze[nd.getRow()][nd.getCol()] != 'P')
                maze[nd.getRow()][nd.getCol()] = c;
        }

        for (int i = 0; i < maze.length; i++) {
            out.println(maze[i]);
        }

        out.printf("\nPath length = %d\n", g.pathLen);
        out.printf("Number of nodes expanded: %d\n", g.expandNodeNum);

        out.close();
    }

}
