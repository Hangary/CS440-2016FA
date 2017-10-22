/**
 * Created by youwu on 9/16/16.
 */
//package cubeSolver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Cube {
public char [] state;
public final static String GOAL="rrbbggrrbbggooooyyyypppp";
public final static int[][] CORNERS ={
		{1,2,22},
		{3,4,23},
		{7,8,12},
		{9,10,13},
		{6,14,16},
		{11,15,17},
		{0,18,20},
		{5,19,21}
    };
public final static HashMap<Character, int[]> SIDES = initSides();

public final static HashMap<String, Integer> GOALCORNERS = initGoalCorners();

public final static HashMap<Character, int[]> FACES = initFaces();

public Cube(){
	this.state= new char [24];
}

public Cube(String filename){
	this.state= readTextFile(filename);
}

public Cube(char[] state){
	this.state= state;
}

private static HashMap<Character, int[]> initFaces(){
	HashMap<Character, int[]> faces=new HashMap<Character, int[]>();
	int [] face= new int[4];
	face[0] = 0;
	face[1] = 1;
	face[2] = 7;
	face[3] = 6;
	faces.put("L".charAt(0), face);
	face= new int[4];
	face[0] = 2;
	face[1] = 3;
	face[2] = 9;
	face[3] = 8;
	faces.put("U".charAt(0), face);
	face= new int[4];
	face[0] = 4;
	face[1] = 5;
	face[2] = 11;
	face[3] = 10;
	faces.put("R".charAt(0), face);
	face= new int[4];
	face[0] = 12;
	face[1] = 13;
	face[2] = 15;
	face[3] = 14;
	faces.put("F".charAt(0), face);
	face= new int[4];
	face[0] = 16;
	face[1] = 17;
	face[2] = 19;
	face[3] = 18;
	faces.put("D".charAt(0), face);
	face= new int[4];
	face[0] = 20;
	face[1] = 21;
	face[2] = 23;
	face[3] = 22;
	faces.put("B".charAt(0), face);
	return faces;
}
private static HashMap<Character, int[]> initSides() {
	HashMap<Character, int[]> sides = new HashMap<Character, int[]>();
	int[] side = new int[8];
    side[0]=20;
    side[1]=22;
    side[2]=2;
    side[3]=8;
    side[4]=12;
    side[5]=14;
    side[6]=16;
    side[7]=18;
    sides.put("L".charAt(0), side);
	side = new int[8];
    side[0]=22;
    side[1]=23;
    side[2]=4;
    side[3]=10;
    side[4]=13;
    side[5]=12;
    side[6]=7;
    side[7]=1;
    sides.put("U".charAt(0), side);   
    side = new int[8];
    side[0]=23;
    side[1]=21;
    side[2]=19;
    side[3]=17;
    side[4]=15;
    side[5]=13;
    side[6]=9;
    side[7]=3;
    sides.put("R".charAt(0), side);   
    side = new int[8];
    side[0]=8;
    side[1]=9;
    side[2]=10;
    side[3]=11;
    side[4]=17;
    side[5]=16;
    side[6]=6;
    side[7]=7;
    sides.put("F".charAt(0), side);   
    side = new int[8];
    side[0]=14;
    side[1]=15;
    side[2]=11;
    side[3]=5;
    side[4]=21;
    side[5]=20;
    side[6]=0;
    side[7]=6;
    sides.put("D".charAt(0), side);  
    side = new int[8];
    side[0]=18;
    side[1]=19;
    side[2]=5;
    side[3]=4;
    side[4]=3;
    side[5]=2;
    side[6]=1;
    side[7]=0;
    sides.put("B".charAt(0), side);
    side = new int[8];
   	return sides;
}

private static HashMap<String,Integer> initGoalCorners(){
	HashMap<String, Integer> result = new HashMap<String, Integer>();
	result.put("bpr", 0);
	result.put("bgp", 1);
	result.put("bor", 2);
	result.put("bgo", 3);
	result.put("ory", 4);
	result.put("goy", 5);
	result.put("pry", 6);
	result.put("gpy", 7);
	return result;
}

public char[] readTextFile(String fileName) {
	String returnValue = "";
	String line;
	try {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		while ((line = reader.readLine()) != null) {
			returnValue += line.replace(" ", ""); 
		}
		reader.close();
	} catch (FileNotFoundException e) {
		throw new RuntimeException("File not found");
	} catch (IOException e) {
		throw new RuntimeException("IO error occurred");
	} 	
	 return returnValue.trim().toCharArray();
}

public static char[] couterRotate(char[] state, Character face, int turns) {

	if (turns % 4 == 0) {
		return state;
	}
	int[] thisFace = FACES.get(face);
	int[] theSides = Cube.SIDES.get(face);
	
	if (thisFace == null || theSides == null) {
		return state;
	}

	
	char[] newStateArray = state.clone();

	crotateFace(state, thisFace, turns, newStateArray);
	crotateSide(state, theSides, turns, newStateArray);
	state = newStateArray;
	return state;
}
private static void crotateFace(char[] state, int[] thisFace, int turns, char[] newStateArray) {
	for (int i = 0; i < thisFace.length; i++) {
		newStateArray[thisFace[(i + (3 * turns)) % 4]] = state[thisFace[i]];
	}
}

private static void crotateSide(char[] state, int[] theSides, int turns, char[] newStateArray) {
	for (int i = 0; i < theSides.length; i++) {
		int moveInt = theSides[(i + (6* turns)) % theSides.length];
		newStateArray[moveInt] = state[theSides[i]];
	}
}

public static char[] rotate(char[] state, Character face, int turns) {

	if (turns % 4 == 0) {
		return state;
	}
	int[] thisFace = FACES.get(face);
	int[] theSides = Cube.SIDES.get(face);
	
	if (thisFace == null || theSides == null) {
		return state;
	}

	
	char[] newStateArray = state.clone();

	rotateFace(state, thisFace, turns, newStateArray);
	rotateSide(state, theSides, turns, newStateArray);
	state = newStateArray;
	return state;
}
private static void rotateFace(char[] state, int[] thisFace, int turns, char[] newStateArray) {
	for (int i = 0; i < thisFace.length; i++) {
		newStateArray[thisFace[(i + (1 * turns)) % 4]] = state[thisFace[i]];
	}
}

private static void rotateSide(char[] state, int[] theSides, int turns, char[] newStateArray) {
	for (int i = 0; i < theSides.length; i++) {
		int moveInt = theSides[(i + (2 * turns)) % theSides.length];
		newStateArray[moveInt] = state[theSides[i]];
	}
}
public static String originState(char[] state) {
    Cube cube= new Cube(state);
	ArrayList<String> dodo= new ArrayList<String>();
	  for(int i=0;i<Cube.changef(cube).size();i++){
		  dodo.add(Cube.changef(cube).get(i));
	  }
	  	Collections.sort(dodo);	  	
	String originState=dodo.get(0);
	return originState;
}
private  static ArrayList<String> changef(Cube n) {
	ArrayList<String> siblings= new ArrayList<String>();
	for(int i=0;i<6;i++){
	    char[] r7=new char[24];
		switch(i){
		case 0:{ r7=n.state;break;}
		case 1:{char[] r9=Cube.rotate(n.state, "U".charAt(0), 1);
                char[] r10=Cube.couterRotate(r9, "D".charAt(0), 1);
                  r7=r10; break;}
		case 2:{ char[] r11=Cube.rotate(n.state, "D".charAt(0), 1);
                 char[] r12=Cube.couterRotate(r11, "U".charAt(0), 1);
                 r7=r12; break;}
		case 3:{ char[] r13=Cube.rotate(n.state, "L".charAt(0), 1);
                 char[] r14=Cube.couterRotate(r13, "R".charAt(0), 1);
                 r7=r14; break;}
		case 4:{ char[] r15=Cube.rotate(n.state, "R".charAt(0), 1);
                 char[] r16=Cube.couterRotate(r15, "L".charAt(0), 1);
                  r7=r16; break;}
		case 5:{ char[] r17=Cube.rotate(n.state, "L".charAt(0), 2);
                 char[] r18=Cube.couterRotate(r17, "R".charAt(0), 2);
                  r7=r18; break;}
		
		default: break;
		}	
	char[] r1=Cube.rotate(r7, "F".charAt(0), 1);
	char[] r2=Cube.couterRotate(r1, "B".charAt(0), 1);
	char[] r3=Cube.rotate(r2, "F".charAt(0), 1);
	char[] r4=Cube.couterRotate(r3, "B".charAt(0), 1);
	char[] r5=Cube.couterRotate(r7, "F".charAt(0), 1);
	char[] r6=Cube.rotate(r5, "B".charAt(0), 1);
	char[] r8=n.state;
	siblings.add(new String(r2));
	siblings.add(new String(r4));
	siblings.add(new String(r6));
	siblings.add(new String(r8));
	}
	return siblings;
}


/**
 * Make a readable output of cube
 */


public String toString() {
	String result = "";

	for (int i = 0; i < this.state.length; i++) {
		if (i < 12 ) {
			if (i % 6 == 5) {
				result += this.state[i] + "\n";
			} else {
				result += this.state[i];
			}
		} else{
			if(i%2==0){
				result += "  " + this.state[i];
			}
			else {
				result += this.state[i]+"\n";
			}
		}	
	} 	
	return result;
}

}
