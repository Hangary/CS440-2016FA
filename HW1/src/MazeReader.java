import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by qixinzhu on 9/12/16.
 */
public class MazeReader {
    public static Graph ReadMazeAndBuildGraph(String fileName)  throws FileNotFoundException {
        File inputFile = new File(fileName);
        Scanner in = new Scanner(inputFile);

        int nodeCount = 0;
        int row = 0;
        int col = 0;
        Node[][] nodeRecorder;
        Node dummy = new Node("%");
        String str = in.nextLine();
        col = str.length();
        nodeRecorder = new Node[str.length()][str.length()];
        List<String> maze = new ArrayList<>();


        // read maze and create useful nodes
        do {
            maze.add(str);
            for (int k=0; k<str.length(); k++) {
                if (str.charAt(k) == '%') nodeRecorder[row][k] = dummy;
                else {
                    nodeCount++;
                    Node n;
                    if (str.charAt(k) == '.') n = new Node(".", row, k);
                    else if (str.charAt(k) == 'P') n = new Node("P", row, k);
                    else n = new Node(" ", row, k);
                    nodeRecorder[row][k] = n;
                }
            }
            row++;

        } while (in.hasNextLine() && (str = in.nextLine()).length() > 0);


        System.out.printf("row = %d, col = %d\n",row, col);

        Graph g = new Graph(nodeCount);
        g.saveOriginalMaze(maze.toArray(new String[maze.size()]));

        // add all non-dummy nodes into graph
        for (int i=0; i<row;i++) {
            for (int j=0;j<col;j++) {
                if (nodeRecorder[i][j] != dummy) {
                    g.addNode(nodeRecorder[i][j]);
                }
            }
        }

        // add all edges
        for (int i=1; i<row-1;i++) {
            for (int j=1;j<col-1;j++) {
                if (nodeRecorder[i][j] != dummy) {
                    if (nodeRecorder[i+1][j] != dummy) {
                        g.addEdge(new Edge(nodeRecorder[i][j], nodeRecorder[i+1][j]));
                    }
                    if (nodeRecorder[i][j+1] != dummy) {
                        g.addEdge(new Edge(nodeRecorder[i][j], nodeRecorder[i][j+1]));
                    }
                }
            }
        }
        return g;
    }
}
