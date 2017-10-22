
//package cubeSolver;
/**
 * Created by youwu on 9/16/16.
 */
public class test {
	public static void main(String[] args) {
        Cube cube = new Cube("cube2_2.txt");
        System.out.println("Input:   "+"\n"+cube.toString());
        long startTime=System.currentTimeMillis(); 
        Cube goalcube = new Cube(Cube.GOAL.toCharArray());
        String path= CubeAStar.AStar(new CubeNode(cube.state, 0), new CubeNode(goalcube.state, 0));
        long endTime=System.currentTimeMillis();
        System.out.println("This is the path: "+path+"\n"+"Runtime: "+(endTime-startTime)+"ms");
        System.out.println(goalcube.toString());
        
}
}
//247302
//329465