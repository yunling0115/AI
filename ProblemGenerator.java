import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

public class ProblemGenerator {
	
	static String path8 = "D:\\Dropbox\\shitty courses\\561 new\\HW2P\\Q8\\";
	static String path9 = "D:\\Dropbox\\shitty courses\\561 new\\HW2P\\Q9\\";
	static String path10 = "D:\\Dropbox\\shitty courses\\561 new\\HW2P\\Q10\\";
	static String prefix = "Q";
	static String prefix2 = "A";

	public static void main(String[] args) throws IOException {
		int N;
		int M;
		double y; // two needs to be together
		double n; // two can be stored together
		String input;
		String output;
		int rep;
		double p;
		int max_flips;

		/* Q8 */
		/*
		p = 0.5; max_flips = 100; rep = 50; N = 16; M = 2; y = 0.0;
		int[][] Q8_WalkSAT = new int[10][rep];
		int[][] Q8_PL = new int[10][rep];
		for (int i=0;i<10;i++) {
			n = 0.02*(i+1);
			Generate(i,M,N,y,n,rep,prefix,path8);
		}
		for (int i=0;i<10;i++) {
			System.out.println("i: "+String.valueOf(i));
			for (int r=0;r<rep;r++) {
				System.out.println("	r: "+String.valueOf(r));
				String f1 = String.valueOf(i)+"_";
				String f2 = String.valueOf(r)+".txt";
				input = path8+prefix+"_"+f1+f2;
				output = path8+prefix2+"_"+f1+f2;
				SATSolver_d d = new SATSolver_d(M,N,input,output,p,max_flips);
				Q8_WalkSAT [i][r] = d.SolveItWalkSAT();
				Q8_PL[i][r] = d.SolveItPL();
			}
		}
		// WalkSAT
		BufferedWriter writer = new BufferedWriter(new FileWriter(path8+"Q8_WalkSAT.csv"));
		writer.write("n"); for (int r=0;r<rep;r++) writer.write(",rep"+String.valueOf(r)); writer.write("\n");
		for (int i=0;i<10;i++) {
			n = 0.02*(i+1);
			writer.write(String.valueOf(n));
			for (int r=0;r<rep;r++) {
				writer.write(","+String.valueOf(Q8_WalkSAT[i][r]));
			}
			writer.write("\n");
		}
		writer.close();	
		// PL Resolution
		writer = new BufferedWriter(new FileWriter(path8+"Q8_PL.csv"));
		writer.write("n"); for (int r=0;r<rep;r++) writer.write(",rep"+String.valueOf(r)); writer.write("\n");
		for (int i=0;i<10;i++) {
			n = 0.02*(i+1);
			writer.write(String.valueOf(n));
			for (int r=0;r<rep;r++) {
				writer.write(","+String.valueOf(Q8_PL[i][r]));
			}
			writer.write("\n");
		}
		writer.close();	
		*/
		
		/* Q9 */
		/*
		p = 0.5; max_flips = 1000; rep = 100; N = 16; M = 2; n = 0.05;
		int[][] Q9 = new int[10][rep];
		for (int i=0;i<10;i++) {
			y = 0.02*(i+1);
			Generate(i,M,N,y,n,rep,prefix,path9);
		}
		for (int i=0;i<10;i++) {
			System.out.println("i: "+String.valueOf(i));
			for (int r=0;r<rep;r++) {
				System.out.println("	r: "+String.valueOf(r));
				String f1 = String.valueOf(i)+"_";
				String f2 = String.valueOf(r)+".txt";
				input = path9+prefix+"_"+f1+f2;
				output = path9+prefix2+"_"+f1+f2;
				SATSolver_d d = new SATSolver_d(M,N,input,output,p,max_flips);
				Q9[i][r] = d.SolveItWalkSAT();
			}
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path9+"Q9.csv"));
		writer.write("y"); for (int r=0;r<rep;r++) writer.write(",rep"+String.valueOf(r)); writer.write("\n");
		for (int i=0;i<10;i++) {
			n = 0.02*(i+1);
			writer.write(String.valueOf(n));
			for (int r=0;r<rep;r++) {
				if (Q9[i][r]>0) writer.write(",1");
				else writer.write(",0");
			}
			writer.write("\n");
		}
		writer.close();	
		*/
		
		/* Q10 */
		p = 0.5; max_flips = 1000; rep = 20; y = 0.02; n = 0.02;
		int N1 = 16; int N2 = 24; int N3 = 32; int N4 = 40; int N5 = 48;
		int M1 = 2; int M2 = 3; int M3 = 4; int M4 = 5; int M5 = 6;
		ArrayList<Integer> Q10_steps1 = new ArrayList();
		ArrayList<Integer> Q10_steps2 = new ArrayList();
		ArrayList<Integer> Q10_steps3 = new ArrayList();
		ArrayList<Integer> Q10_steps4 = new ArrayList();
		ArrayList<Integer> Q10_steps5 = new ArrayList();
		ArrayList<Integer> Q10_size1 = new ArrayList();
		ArrayList<Integer> Q10_size2 = new ArrayList();
		ArrayList<Integer> Q10_size3 = new ArrayList();
		ArrayList<Integer> Q10_size4 = new ArrayList();
		ArrayList<Integer> Q10_size5 = new ArrayList();
		// (1)
		/*
		int iter = 0;
		int r = 0;
		while (r<rep) {
			iter++;
			Generate(1,M1,N1,y,n,1,prefix,path10);
			input = path10+prefix+"_1_0.txt";
			output = path10+prefix2+"_1_0.txt";
			SATSolver_d d = new SATSolver_d(M1,N1,input,output,p,max_flips);
			int a = d.SolveItWalkSAT(); int b = d.Size();
			Q10_steps1.add(a);
			Q10_size1.add(b);
			if (a>0) r++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path10+"Q10_1.csv"));
		writer.write("iter,size\n");
		for (r=0;r<iter;r++) {
			writer.write(String.valueOf(Q10_steps1.get(r))+","+String.valueOf(Q10_size1.get(r))+"\n");
		}
		writer.close();	
		*/
		// (2)
		/*
		int iter = 0;
		int r = 0;
		while (r<rep) {
			iter++;
			Generate(2,M2,N2,y,n,1,prefix,path10);
			input = path10+prefix+"_2_0.txt";
			output = path10+prefix2+"_2_0.txt";
			SATSolver_d d = new SATSolver_d(M2,N2,input,output,p,max_flips);
			int a = d.SolveItWalkSAT(); int b = d.Size();
			Q10_steps2.add(a);
			Q10_size2.add(b);
			if (a>0) r++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path10+"Q10_2.csv"));
		writer.write("iter,size\n");
		for (r=0;r<iter;r++) {
			writer.write(String.valueOf(Q10_steps2.get(r))+","+String.valueOf(Q10_size2.get(r))+"\n");
		}
		writer.close();	
		*/
		// (3)
		/*
		int iter = 0;
		int r = 0;
		while (r<rep) {
			iter++;
			Generate(3,M3,N3,y,n,1,prefix,path10);
			input = path10+prefix+"_3_0.txt";
			output = path10+prefix2+"_3_0.txt";
			SATSolver_d d = new SATSolver_d(M3,N3,input,output,p,max_flips);
			int a = d.SolveItWalkSAT(); int b = d.Size();
			Q10_steps3.add(a);
			Q10_size3.add(b);
			if (a>0) r++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path10+"Q10_3.csv"));
		writer.write("iter,size\n");
		for (r=0;r<iter;r++) {
			writer.write(String.valueOf(Q10_steps3.get(r))+","+String.valueOf(Q10_size3.get(r))+"\n");
		}
		writer.close();	
		*/
		// (4)
		/*
		int iter = 0;
		int r = 0;
		while (r<rep) {
			iter++;
			Generate(4,M4,N4,y,n,1,prefix,path10);
			input = path10+prefix+"_4_0.txt";
			output = path10+prefix2+"_4_0.txt";
			SATSolver_d d = new SATSolver_d(M4,N4,input,output,p,max_flips);
			int a = d.SolveItWalkSAT(); int b = d.Size();
			Q10_steps4.add(a);
			Q10_size4.add(b);
			if (a>0) r++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path10+"Q10_4.csv"));
		writer.write("iter,size\n");
		for (r=0;r<iter;r++) {
			writer.write(String.valueOf(Q10_steps4.get(r))+","+String.valueOf(Q10_size4.get(r))+"\n");
		}
		writer.close();	
		*/
		// (5)
		int iter = 0;
		int r = 0;
		while (r<rep) {
			iter++;
			Generate(5,M5,N5,y,n,1,prefix,path10);
			input = path10+prefix+"_5_0.txt";
			output = path10+prefix2+"_5_0.txt";
			SATSolver_d d = new SATSolver_d(M5,N5,input,output,p,max_flips);
			int a = d.SolveItWalkSAT(); int b = d.Size();
			Q10_steps5.add(a);
			Q10_size5.add(b);
			if (a>0) r++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(path10+"Q10_5.csv"));
		writer.write("iter,size\n");
		for (r=0;r<iter;r++) {
			writer.write(String.valueOf(Q10_steps5.get(r))+","+String.valueOf(Q10_size5.get(r))+"\n");
		}
		writer.close();	
	}
	
	public static void Generate(int i1,int M, int N, double y, double n, int rep, String prefix, String path) throws IOException {
		for (int r=0;r<rep;r++) {
			// generate - multinomial (y,n,1-y-n)
			int[][] C = new int[N][N];
			double random;
			for (int i=0;i<N;i++) {
				C[i][i]=0;
				for (int j=i+1;j<N;j++) {
					random = Math.random();
					if (random<y) C[i][j]=1;
					else if (random>1-n) C[i][j]=-1;
					else C[i][j]=0;
				}
				for (int j=0;j<i;j++) {
					C[i][j] = C[j][i];
				}
			}
			// write to file
			String f1 = String.valueOf(i1)+"_";
			String f2 = String.valueOf(r)+".txt";
			String filename = path+prefix+"_"+f1+f2;
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(String.valueOf(N)); writer.write(" ");
			writer.write(String.valueOf(M)); writer.write("\n");
			for (int i=0;i<N;i++) {
				for (int j=0;j<N-1;j++) {
					writer.write(String.valueOf(C[i][j])+" ");
				}
				writer.write(String.valueOf(C[i][N-1])+"\n");
			}
			writer.close();
		}
	}
	
	public static void printout2(boolean[][] b) {
		String s = "";
		int n1 = b.length; int n2 = b[0].length;
		for (int i=0;i<n1;i++) {
			String k = "";
			for (int j=0;j<n2;j++) {
				if (b[i][j]==true) k+=",1";
				else k+=",0";
			}
			s+="  ("+k.substring(1)+")\n";
		}
		System.out.println("cnf: \n"+s);
	}

}


