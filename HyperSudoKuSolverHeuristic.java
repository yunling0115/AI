import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;


public class HyperSudokuSolverHeuristic {


	public static void main(String[] args) throws IOException {
		char[][] board = SudokuRead(args[0]);
		SudokuSolve(board);
		SudokuWrite(args[1],board);
	}
	
	/* Apply Minimum-Value-Heuristics:
	 * choose the variable with the fewest remaining values (also called the most constrained variable or first-constrained heuristics) */
	
	/* 0. get minimum remaining value */
	public static int[] getMRV(char[][] board) {
		int[] position = new int[3];
		position[0] = -1; position[1] = -1; position[2] = 10;
		for (int i=0;i<9;++i) {
			for (int j=0;j<9;++j) {
				if (board[i][j]=='-') {
					ArrayList N = new ArrayList();
					for (int k=0;k<9;++k) {
						char n = (char) (k+'0');
						N.add(n);
					}
					for (int k1=0;k1<9;++k1) {
						for (int k2=0;k2<9;++k2) {
							if (k1==i && k2==j) continue;
							int l = i/3*3; int u = j/3*3;
							if ((N.contains(board[k1][k2])) && (k1==i || k2==j)) {N.remove(N.indexOf(board[k1][k2])); continue;}
							if ((N.contains(board[k1][k2])) && (l<=k1 && k1<=l+2 && u<=k2 && k2<=u+2)) {N.remove(N.indexOf(board[k1][k2])); continue;}
							l = i/4*4+1; u = j/4*4+1;
							if ((N.contains(board[k1][k2])) && (i%4!=0 && j%4!=0) && (l<=k1 && k1<=l+2 && u<=k2 && k2<=u+2)) {N.remove(N.indexOf(board[k1][k2])); continue;}
						}
					}
					if (N.size()<position[2]) {position[0]=i; position[1]=j; position[2]=N.size();}
				}
			}
		}
		return position;
	}
	
	/* 1. read */
	public static char[][] SudokuRead(String filename) throws IOException {
		char[][] board = new char[9][9];
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		int i = 0;
		while ((line=reader.readLine())!=null) {
			String[] data = line.split(" ");
			for (int j=0;j<9;++j) {
				board[i][j] = data[j].charAt(0);
			}
			++i;
		}
		reader.close();
		return board;
	}
	
	/* 2. solve */
	static int k=0;
	
	/* a. using MRV heursitics */
	public static boolean SudokuSolve(char[][] board) {
		k++; System.out.println(k);
		int[] position = getMRV(board);
		if (position[2]>9) return true;
		else {
			int i = position[0];
			int j = position[1];
			for (int n=1;n<10;++n) {
				board[i][j] = (char) (n+'0');
				if (Compatible(i,j,board) && SudokuSolve(board)) return true;	
			}
			board[i][j] = '-';
			return false;
		}
	}
	
	/* b. using original backtracking search */
	/*
	public static boolean SudokuSolve(char[][] board) {
		k++; System.out.println(k);
		for (int i=0;i<9;++i) {
			for (int j=0;j<9;++j) {
				if (board[i][j]=='-') {
					for (int n=1;n<10;++n) {
						board[i][j] = (char) (n+'0');
						if (Compatible(i,j,board) && SudokuSolve(board) && (i%4!=0 && j%4!=0)) return true;	
					}
					board[i][j] = '-';
					return false;
				}
			}
		}
		return true;
	}
	*/

	/* 3. check constraints */
	public static boolean Compatible(int i, int j, char[][] board) {
		char number = board[i][j];
		/* horizontal and vertical distinct */
		for (int k=0;k<9;++k) {
			if ((board[i][k]==number && k!=j) || (board[k][j]==number && k!=i)) {
				return false;
			}
		}		
		/* distinct in the nine units board */
		int l = i/3*3; int u = j/3*3;
		for (int k1=0;k1<3;++k1) {
			for (int k2=0;k2<3;++k2) {
				if (l+k1==i && u+k2==j) continue;
				if (board[l+k1][u+k2]==number) return false;
			}
		}
		/* more constraints */
		l = i/4*4+1; u = j/4*4+1;
		if (i%4!=0 && j%4!=0) {
			for (int k1=0;k1<3;++k1) {
				for (int k2=0;k2<3;++k2) {
					if (l+k1==i && u+k2==j) continue;
					if (board[l+k1][u+k2]==number) return false;
				}
			}
		}
		return true;		
	}

	/* 4. write */
	public static void SudokuWrite(String filename, char[][] board) throws IOException {
		System.out.println(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (int i=0;i<9;++i) {
			for (int j=0;j<9;++j) {
				writer.write(board[i][j]+" ");	
			}
			writer.write("\n");
		}		
		writer.close();
	}
	


}
