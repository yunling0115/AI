import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SATSolver_d {
	
	static int M;
	static int N;
	static int P;
	static int Q;
	static String input;
	static String output;
	static double p;
	static int max_flips;
	static int limit = 1000;
	
	/* construct */
	public SATSolver_d(int Ms,int Ns,String inputs,String outputs,double p1,int max_flips1) throws IOException {
		M = Ms; N = Ns; input = inputs; output = outputs; p = p1; max_flips = max_flips1;
		int[][] C = Readfile(input);
	}
	
	/* Size */
	public static int Size() throws IOException {
		int[][] C = Readfile(input);
		boolean[][] cnf = CNFconverter(C,M,N,P,Q);
		return cnf.length;
	}
	
	/* solve */
	public static boolean Solve() throws IOException {
		int[][] C = Readfile(input);
		boolean[][] cnf = CNFconverter(C,M,N,P,Q);
		boolean res = PLResolution(cnf,M,N);
		//System.out.println("PL Resolution: "+res);
		int[][] S = new int[M][N];
		if (res==true) {
			S = WalkSAT(cnf,M,N,p,max_flips);
			if (S[0][0]==-1) {
				System.out.println("\nWalkSAT: unresolved");
				Writefile(0,S,output);
				return false;
			}
			else {
				Writefile(1,S,output);
				System.out.println("\nWalkSAT: resolved");
				return true;
			}
		}
		else {
			System.out.println("\nWalkSAT: unresolved");
			Writefile(0,S,output);
			return false;
		}
	}
	
	/* Solve It */
	public static int SolveItPL() throws IOException {
		int steps;
		int[][] C = Readfile(input);
		boolean[][] cnf = CNFconverter(C,M,N,P,Q);
		boolean res = PLResolution(cnf,M,N);
		//System.out.println("PL Resolution: "+res);
		if (res==true) {
			steps = PLResolutionIt(cnf,M,N,max_flips);
			if (steps==-1) return -1;
			else return steps;
		}
		else return -1;
	}

	public static int SolveItWalkSAT() throws IOException {
		int steps;
		int[][] C = Readfile(input);
		boolean[][] cnf = CNFconverter(C,M,N,P,Q);
		boolean res = PLResolution(cnf,M,N);
		//System.out.println("PL Resolution: "+res);
		if (res==true) {
			steps = WalkSATIt(cnf,M,N,p,max_flips);
			if (steps==-1) return -1;
			else return steps;
		}
		else return -1;
	}
	
	/* 1. read */
	public static int[][] Readfile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;
		// first line
		line = reader.readLine();
		String[] data = line.split(" ");
		N = Integer.parseInt(data[0]); 
		M = Integer.parseInt(data[1]);
		int yes = 0;
		int no = 0;
		// second line
		int[][] C = new int[N][N];
		int i = 0;
		while ((line=reader.readLine())!=null) {
			data = line.split(" ");
			for (int j=0;j<N;++j) {
				C[i][j] = Integer.parseInt(data[j]);
				if (Integer.parseInt(data[j])==1) yes++;
				if (Integer.parseInt(data[j])==-1) no++;
			}
			i++;
		}
		reader.close();
		//System.out.println(P);
		//System.out.println(Q);
		P = (int) (yes/2);
		Q = (int) (no/2);
		return C;
	}
	
	/* 2. Transform to CNF */
	public static boolean[][] CNFconverter(int[][] C, int M, int N, int P, int Q) {
		boolean[][] cnf = new boolean[(int) (N+Q*M+(N+2*P)*M*(M-1)/2)][2*M*N];
		// Generate yes and no list
		int[][] yes = new int[P][2];
		int[][] no = new int[Q][2];
		int y=0; int n=0;
		for (int i1=0;i1<N;i1++) for (int i2=i1+1;i2<N;i2++) {
			// non-duplicate pair
			if (C[i1][i2]==-1) {
				no[n][0]=i1; no[n][1]=i2; 
				n++;
			}
			if (C[i1][i2]==1) {
				yes[y][0]=i1; yes[y][1]=i2; 
				y++;
			}
		}
		// NM ordering: X_ij -> i*M+j, neg X_ij -> MN+i*M+j
		int row = 0; // row++ if jump to the next disjunctive sub-sentence
		// (1) for each chemical in one container
		// (1a) at least in one container - N conditions
		for (int i=0;i<N;i++) {	
			for (int j=0;j<M;j++) {
				cnf[row][i*M+j] = true; 
			}
			row++;
		}
		// (1b) not in two different containers - N*M*(M-1)/2 conditions
		for (int i=0;i<N;i++) {
			for (int j1=0;j1<M;j1++) {
				for (int j2=j1+1;j2<M;j2++) {
					cnf[row][M*N+i*M+j1]=true;
					cnf[row][M*N+i*M+j2]=true;
					row++;
				}
			}
		}
		// (2) for each yes pair - P*M*(M-1) conditions
		int i1; int i2;
		for (int i=0;i<P;i++) {
			i1 = yes[i][0];
			i2 = yes[i][1];
			for (int j1=0;j1<M;j1++) {
				for (int j2=0;j2<M;j2++) {
					if (j2!=j1) {
					cnf[row][M*N+i1*M+j1]=true;
					cnf[row][M*N+i2*M+j2]=true;
					row++;
					}
				}
			}
		}
		// (3) for each no pair - Q*M conditions
		for (int i=0;i<Q;i++) {
			i1 = no[i][0];
			i2 = no[i][1];
			for (int j=0;j<M;j++) {
				cnf[row][M*N+i1*M+j]=true;
				cnf[row][M*N+i2*M+j]=true;
				row++;
			}
		}
		return cnf;
	}
	
	/* 3. PL-Resoution */
	/*
	find a sub-sentence containing the opposite 
	if:  not find a sub-sentence containing the opposite, return true
	check: if results empty, return false
	*/
	public static boolean PLResolution(boolean[][] cnf, int M, int N) throws IOException {
		int s1 = cnf.length; 
		ArrayList <boolean[]> clause = new ArrayList();
		for (int i=0;i<s1;i++) {
			boolean[] sentence = new boolean[2*M*N];
			for(int j=0;j<2*M*N;j++) {
				sentence[j] = cnf[i][j];
			}
			clause.add(sentence);
		}
		ArrayList<boolean[]> queue = new ArrayList();
		for (int i=0;i<s1;i++) {
			queue.add(clause.get(i));
		}
		int aa=0;
		while (true) {
			s1 = clause.size();
			aa++;
			ArrayList <boolean[]> newset = new ArrayList();
			for (int i1=0;i1<s1;i1++) for (int i2=0;i2<s1;i2++) {
				if (i2==i1) continue;
				boolean[] d1 = clause.get(i1);
				boolean[] d2 = clause.get(i2);
				for (int k=0;k<M*N;k++) {
					if (d1[k]==true && d2[k+M*N]==true) {
						boolean[] res = new boolean[2*M*N];
						d1[k]=false; d2[k+M*N]=false;
						for (int k2=0;k2<2*M*N;k2++) {
							res[k2]=d1[k2]||d2[k2];
						}
						d1[k]=true; d2[k+M*N]=true;
						// empty? then stop return false
						if (Empty(res)==true) {
							return false;
						}
						newset.add(res);
						break;
					}
				}
			}
			// otherwise clause = clause + new/
			int size_old = clause.size();
			clause = Add(clause,Truncate(newset,limit));
			int size_new = clause.size();
			if (size_new>limit) {
				clause = Truncate(clause,limit);
				size_new = clause.size();
			}
			if (size_old==size_new) return true;
		}
	}
	
	public static boolean PLResolutionN(boolean[][] cnf, int M, int N) throws IOException {
		int s1 = cnf.length; 
		ArrayList <boolean[]> clause = new ArrayList();
		for (int i=0;i<s1;i++) {
			boolean[] sentence = new boolean[2*M*N];
			for(int j=0;j<2*M*N;j++) {
				sentence[j] = cnf[i][j];
			}
			clause.add(sentence);
		}
		ArrayList<boolean[]> queue = new ArrayList();
		for (int i=0;i<s1;i++) {
			queue.add(clause.get(i));
		}
		int aa=0;
		while (true) {
			s1 = clause.size();
			aa++;
			ArrayList <boolean[]> newset = new ArrayList();
			for (int i1=0;i1<s1;i1++) for (int i2=0;i2<s1;i2++) {
				if (i2==i1) continue;
				boolean[] d1 = clause.get(i1);
				boolean[] d2 = clause.get(i2);
				for (int k=0;k<M*N;k++) {
					if (d1[k]==true && d2[k+M*N]==true) {
						boolean[] res = new boolean[2*M*N];
						d1[k]=false; d2[k+M*N]=false;
						for (int k2=0;k2<2*M*N;k2++) {
							res[k2]=d1[k2]||d2[k2];
						}
						d1[k]=true; d2[k+M*N]=true;
						// empty? then stop return false
						if (Empty(res)==true) {
							return false;
						}
						newset.add(res);
						break;
					}
				}
			}
			// otherwise clause = clause + new/
			int size_old = clause.size();
			clause = Add(clause,Truncate(newset,limit));
			int size_new = clause.size();
			if (size_new>limit) {
				clause = Truncate(clause,limit);
				size_new = clause.size();
			}
			if (size_old==size_new) return true;
		}
	}
	
	/* 3. PLResolutionIt */
	public static int PLResolutionIt(boolean[][] cnf, int M, int N, int max_flips) throws IOException {
		int s1 = cnf.length; 
		ArrayList <boolean[]> clause = new ArrayList();
		for (int i=0;i<s1;i++) {
			boolean[] sentence = new boolean[2*M*N];
			for(int j=0;j<2*M*N;j++) {
				sentence[j] = cnf[i][j];
			}
			clause.add(sentence);
		}
		ArrayList<boolean[]> queue = new ArrayList();
		for (int i=0;i<s1;i++) {
			queue.add(clause.get(i));
		}
		int aa=0;
		int Iter = 0;
		while (true) {
			s1 = clause.size();
			aa++;
			ArrayList <boolean[]> newset = new ArrayList();
			for (int i1=0;i1<s1;i1++) for (int i2=0;i2<s1;i2++) {
				if (i2==i1) continue;
				boolean[] d1 = clause.get(i1);
				boolean[] d2 = clause.get(i2);
				for (int k=0;k<M*N;k++) {
					if (d1[k]==true && d2[k+M*N]==true) {
						boolean[] res = new boolean[2*M*N];
						d1[k]=false; d2[k+M*N]=false;
						for (int k2=0;k2<2*M*N;k2++) {
							res[k2]=d1[k2]||d2[k2];
						}
						d1[k]=true; d2[k+M*N]=true;
						// empty? then stop return false
						if (Empty(res)==true) {
							return -1;
						}
						newset.add(res);
						break;
					}
				}
			}
			Iter++;
			// otherwise clause = clause + new/
			int size_old = clause.size();
			clause = Add(clause,Truncate(newset,limit));
			int size_new = clause.size();
			if (size_new>limit) {
				clause = Truncate(clause,limit);
				size_new = clause.size();
			}
			if (size_old==size_new) return Iter;
		}
	}
	
	/* 3a - Empty */
	public static boolean Empty(boolean[] s) {
		int count_true = 0;
		for (int i=0;i<s.length;i++) {
			if (s[i]==true) count_true++;
		}
		if (count_true==0) return true;
		else return false;
	}
	
	/* 3b - Equal */
	public static boolean Equal(boolean[] s1, boolean[] s2) {
		int n = s1.length;
		for (int i=0;i<n;i++) {
			if (s1[i]!=s2[i]) return false;
		}
		return true;
	}
	
	/* 3c - Truncate */
	public static ArrayList<boolean[]> Truncate(ArrayList<boolean[]> clause, int limit) {
		if (limit>=clause.size()) return clause;
		else {
			while(clause.size()>limit) {
				clause.remove(clause.size()-1);
			}
			return clause;
		}
	}	
	/* 3c - Adding non-duplicated new to clause and return clause */
	public static ArrayList <boolean[]> Add(ArrayList <boolean[]> clause, ArrayList <boolean[]> newset) {
		int s2 = newset.size();
		int s3 = clause.get(0).length;
		for (int i2=0;i2<s2;i2++) {
			boolean[] d2 = newset.get(i2);
			boolean find_equal = false;
			int s1 = clause.size(); 
			for (int i1=0;i1<s1;i1++) {
				boolean[] d1 = clause.get(i1);
				if (Equal(d1,d2)==true) find_equal = true;
			}
			if (find_equal==false) clause.add(d2);
		}
		return clause;
	}
	
	/* 4. WalkSAT */
	/* Picks a clause which is unsatisfied by the current assignment then flips a variable within that clause. 
	The clause is generally picked at random among unsatisfied clauses. The variable is generally picked that will 
	(1) result in the fewest previously satisfied clauses becoming unsatisfied, 
	(2) with some probability of picking one of the variables at random.
	*/
	public static int[][] WalkSAT(boolean[][] cnf, int M, int N, double p, int max_flips) {
		System.out.println("WalkSAT:");
		int s1 = cnf.length;
		boolean resolved = false;
		int[][] S = new int[M][N]; 
		// randomly initalize (need to transpose)
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++) {
				double random = Math.random();
				if (random<0.5) S[j][i]=1;
				else S[j][i]=0;
			}
		}
		// iterate
		for (int iter=0;iter<max_flips;iter++) {
			// evaluate the assignment of S according to cnf
			ArrayList<Integer> eval = Evaluate(S,cnf,M,N);
			int number_false = eval.size();
			// 1. true: break
			if (number_false==0) {
				resolved=true; 
				return S;
			}
			// 2. false: randomly pick unsatisfiable clause
			else {
				double random = Math.random();
				int selected = (int) Math.floor(random*number_false);
				boolean[] sentence = cnf[eval.get((int) selected)];
				random = Math.random();
				// (a) with probability p, randomly pick a symbol and flip
				if (random<p) {
					S = randomflip(S,sentence,M,N);
				}
				// (b) with probability 1-p, pick the symbol maximizes the number of satisfied clauses and flip
				else {
					S = optimalflip(S,sentence,cnf,M,N);
				}
			}
		}
		// output 
		if (resolved==false) S[0][0]=-1;
		return S;
	}
	
	/* 4. WalkSATIt */
	public static int WalkSATIt(boolean[][] cnf, int M, int N, double p, int max_flips) {
		int s1 = cnf.length;
		boolean resolved = false;
		int[][] S = new int[M][N]; 
		// randomly initalize (need to transpose)
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++) {
				double random = Math.random();
				if (random<0.5) S[j][i]=1;
				else S[j][i]=0;
			}
		}
		// iterate
		int Iter = 0;
		for (int iter=0;iter<max_flips;iter++) {
			// evaluate the assignment of S according to cnf
			ArrayList<Integer> eval = Evaluate(S,cnf,M,N);
			int number_false = eval.size();
			// 1. true: break
			if (number_false==0) {
				resolved=true; 
				return iter;
			}
			// 2. false: randomly pick unsatisfiable clause
			else {
				double random = Math.random();
				int selected = (int) Math.floor(random*number_false);
				boolean[] sentence = cnf[eval.get((int) selected)];
				random = Math.random();
				// (a) with probability p, randomly pick a symbol and flip
				if (random<p) {
					S = randomflip(S,sentence,M,N);
				}
				// (b) with probability 1-p, pick the symbol maximizes the number of satisfied clauses and flip
				else {
					S = optimalflip(S,sentence,cnf,M,N);
				}
			}
			Iter++;
		}
		// output 
		if (resolved==false) return -1;
		else return Iter;
	}	
	
	/* 4a. Evaluate a cnf and return the index of unsatisfied disjunctive forms */
	public static ArrayList<Integer> Evaluate(int[][] S, boolean[][] cnf, int M, int N) {
		int s1 = cnf.length;
		ArrayList<Integer> eval = new ArrayList();
		for (int k=0;k<s1;k++) {
			boolean m = false;
			for (int j=0;j<M;j++) for (int i=0;i<N;i++) {
				if ((cnf[k][i*M+j]==true && S[j][i]==1) || (cnf[k][M*N+i*M+j]==true && S[j][i]==0)) {
					m = true;
					break;
				}
			}
			if (m==false) {
				eval.add(k);
			}
		}
		return eval;
	}
	
	/* 4b. Random flip */
	public static int[][] randomflip(int[][] S, boolean[] sentence, int M, int N) {
		ArrayList<Integer> involved = new ArrayList();
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++) {
				if (sentence[i*M+j]==true || sentence[M*N+i*M+j]==true) {
					involved.add(i*M+j);
				}
			}
		}
		double random = Math.random();
		int selected_index = (int) Math.floor(random* involved.size());
		int selected = involved.get(selected_index);
		int i = (int) (selected/M); int j = selected-i*M;
		S[j][i] = (int) (1-S[j][i]);
		return S;
	}
	
	
	/* 4c. Optimal flip */
	public static int[][] optimalflip(int[][] S, boolean[] sentence, boolean[][] cnf, int M, int N) {
		ArrayList<Integer> involved = new ArrayList();
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++) {
				if (sentence[i*M+j]==true || sentence[M*N+i*M+j]==true) {
					involved.add(i*M+j);
				}
			}
		}
		// calculate for each flip and select the optimal one
		ArrayList<Integer> eval = Evaluate(S,cnf,M,N);
		int selected_index = 0;
		int min = eval.size();
		int selected = involved.get(selected_index);
		int i = (int) (selected/M); int j = selected-i*M;
		S[j][i] = (int) (1-S[j][i]);
		return S;
	}
	
	/* 5. write */
	public static void Writefile(int r, int[][] S, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(r+"\n");
		if (r==0) writer.close();
		if (r==1) {
			for (int i=0;i<M;++i) {
				for (int j=0;j<N;++j) {
					writer.write(S[i][j]+" ");	
				}
				writer.write("\n");
			}		
			writer.close();
		}
	}
	
}
