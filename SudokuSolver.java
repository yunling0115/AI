import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;


public class SudokuSolver {

	public static void main(String[] args) throws IOException {
		char[][] board = SudokuRead(args[0]);
		SudokuSolve(board);
		SudokuWrite(args[1],board);
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

	public static boolean SudokuSolve(char[][] board) {
		for (int i=0;i<9;++i) {
			for (int j=0;j<9;++j) {
				if (board[i][j]=='-') {
					for (int n=1;n<10;++n) {
						board[i][j] = (char) (n+'0');
						if (Compatible(i,j,board) && SudokuSolve(board)) return true;	
					}
					board[i][j] = '-';
					return false;
				}
			}
		}
		return true;
	}

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

