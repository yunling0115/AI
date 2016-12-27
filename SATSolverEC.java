import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SATSolverEC {

	static int M;
	static int N;
	static int P; //yes
	static int Q; //no
	static int K; //no more than K
	
	public static void main(String[] args) throws IOException {
		
		String input = args[0];
		String output = args[1];
		double p = Double.parseDouble(args[2]);
		int max_flips = Integer.parseInt(args[3]);

		//String input = "D:\\Dropbox\\shitty courses\\561 new\\HW2P\\input2_2.txt";
		//String output = "D:\\Dropbox\\shitty courses\\561 new\\HW2P\\output2_2.txt";
		//double p = 0.7;
		//int max_flips = 100;
		
		int[][] C = Readfile(input);
		int[][] S = new int[M][N];
		if (K==0 || K>N) {
			System.out.println("unresolved");
			Writefile(0,S,output);
		}
		boolean[][] cnf = CNFconverter(C,M,N,P,Q);
		printout2(cnf);
		boolean res = PLResolution(cnf,M,N);
		if (res==true) {
			S = WalkSAT(cnf,M,N,p,max_flips);
			if (S[0][0]==-1) {
				System.out.println("unresolved");
				Writefile(0,S,output);
			}
			else {
				Writefile(1,S,output);
				System.out.println("resolved");
			}
		}
		else {
			System.out.println("unresolved");
			Writefile(0,S,output);
		}
		
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
		K = Integer.parseInt(data[2]);
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
		P = (int) (yes/2);
		Q = (int) (no/2);
		/*
		System.out.println("P="+P+",Q="+Q);
		for (int i1=0;i1<N;i1++) {
			String s = "";
			for (int i2=0;i2<N;i2++) {
				s+=","+(String) (C[i1][i2]+" ");
			}
			System.out.println(s.substring(1));
		}
		*/
		return C;
	}
	
	/* 2. Transform to CNF */
	public static boolean[][] CNFconverter(int[][] C, int M, int N, int P, int Q) {
		ArrayList<int[]> CO = Comb(N,K+1);
		int CO_number = CO.size();
		System.out.println("CO_number: "+CO_number+"\n");
		boolean[][] cnf = new boolean[(int) (N+Q*M+CO_number*M+(N+2*P)*M*(M-1)/2)][2*M*N];
		// Generate yes and no list
		int[][] yes = new int[P][2];
		int[][] no = new int[Q][2];
		int y=0; int n=0;
		for (int i1=0;i1<N;i1++) for (int i2=i1+1;i2<N;i2++) {
			// non-duplicate pair
			if (C[i1][i2]==-1) {
				no[n][0]=i1; no[n][1]=i2; 
				System.out.println("no list: ("+i1+","+i2+")\n"); 
				n++;
			}
			if (C[i1][i2]==1) {
				yes[y][0]=i1; yes[y][1]=i2; 
				System.out.println("yes list: ("+i1+","+i2+")\n"); 
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
			//printout(cnf[row]);
			row++;
		}
		// (1b) not in two different containers - N*M*(M-1)/2 conditions
		for (int i=0;i<N;i++) {
			for (int j1=0;j1<M;j1++) {
				for (int j2=j1+1;j2<M;j2++) {
					cnf[row][M*N+i*M+j1]=true;
					cnf[row][M*N+i*M+j2]=true;
					//printout(cnf[row]);
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
					//printout(cnf[row]);
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
				//printout(cnf[row]);
				row++;
			}
		}
		/*
		for (int i=0;i<(int) (N+Q*M+(N+P)*M*(M-1)/2);i++) {
			for (int j=0;j<2*M*N;j++) {
				System.out.println(cnf[i][j]);
			}
		}
		*/
		// (4) no K+1 distinct chemicals are in one container
		for (int j=0;j<M;j++) {
			for (int s=0;s<CO_number;s++) {
				for (int k=0;k<K+1;k++) {
					int i = CO.get(s)[k];
					cnf[row][M*N+i*M+j]=true;
				}
				row++;
			}
		}
		return cnf;
	}
	
	/* 2a. (N, K) return 2-dim int array with each array indicating the K selected indicies, by default the N list is [0,...N-1] */
	public static ArrayList<int[]> Comb(int N, int K) {
		ArrayList<int[]> comb = new ArrayList();
		if ((K>N)||(K==0)) return comb;
		else if (K==1) {
			for (int i=0;i<N;i++) {
				int[] temp = new int[1];
				temp[0] = i;
				comb.add(temp);
			}
			return comb;
		}
		else if (K==N) {
			int[] list = new int[K];
			for (int i=0;i<N;i++) {
				list[i] = i;
			}
			comb.add(list);
			return comb;
		}
		else {
			comb = Comb(N-1,K);
			ArrayList<int[]> comb2 = Comb(N-1,K-1);
			for (int i=0;i<comb2.size();i++) {
				int[] temp2 = comb2.get(i);
				int[] temp1 = new int[K];
				for (int k=0;k<K-1;k++) {
					temp1[k] = temp2[k];
				}
				temp1[K-1] = N-1;
				comb.add(temp1);
			}
			return comb;
		}
	}
	
	/* 3. PL-Resoution */
	/*
	find a sub-sentence containing the opposite 
	if:  not find a sub-sentence containing the opposite, return true
	check: if results empty, return false
	*/
	public static boolean PLResolutionLimit(boolean[][] cnf, int M, int N, int limit) throws IOException {
		// add a limit to the size of the ArrayList
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
			// delete containing only one symbol
			clause = deleteOne(clause);
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
						// flag to indicate need to add to newset or not
						int flag = 0;
						for (int k2=0;k2<M*N;k2++) {
							if (res[k2]==true && res[k2+M*N]==true) {flag=1; break;}
						}
						if (flag==0) newset.add(res);
						break;
					}
				}
			}
			// otherwise clause = clause + new
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
						// flag to indicate need to add to newset or not
						int flag = 0;
						for (int k2=0;k2<M*N;k2++) {
							if (res[k2]==true && res[k2+M*N]==true) {flag=1; break;}
						}
						if (flag==0) newset.add(res);
						break;
					}
				}
			}
			int limit = 5000;
			// otherwise clause = clause + new
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
	
	
	//-------------------------------------------------------------------------------------------------------------------------------
	/*
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
			// delete containing only one symbol
			clause = deleteOne(clause);
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
						// flag to indicate need to add to newset or not
						int flag = 0;
						for (int k2=0;k2<M*N;k2++) {
							if (res[k2]==true && res[k2+M*N]==true) {flag=1; break;}
						}
						if (flag==0) newset.add(res);
						break;
					}
				}
			}
			// otherwise clause = clause + new
			int size_old = clause.size();
			clause = Add(clause,newset);
			int size_new = clause.size();
			if (size_old==size_new) return true;
		}
	}
	*/
	
	/* 30 - Delete One Unit */
	public static ArrayList <boolean[]> deleteOne(ArrayList <boolean[]> clause) {
		int s = clause.size();
		int i = 0;
		while (true) {			
			s = clause.size();
			if (i>=s) break;
			boolean[] line = clause.get(i);
			int pos = One(line);
			if (pos>=0) {
				// revise
				for (int j=0;j<s;j++) {
					if (j==i) continue;
					boolean[] line2 = clause.get(j);
					if (pos<M*N && line2[pos+M*N]==true) {
						line2[pos+M*N] = false;
					}
					if (pos>=M*N && line2[pos-M*N]==true) {
						line2[pos-M*N] = false;
					}	
				}
				// delete 
				clause.remove(i);
			}
			i++;
		}
		return clause;
	}
	
	/* 3x - Judge If One Unit - if yes, return position */
	public static int One(boolean[] s) {
		int count_true = 0;
		int pos = -1;
		for (int i=0;i<s.length;i++) {
			if (s[i]==true) {
				count_true++;
				pos = i;
			}
		}
		if (count_true>1) return -1;
		else return pos;
	}
	//-------------------------------------------------------------------------------------------------------------------------------
	
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
	
	/* printout: print out a disjunctive form for debugging purpose */
	public static void printout(boolean[] sentence) {
		String s = "";
		for (int i=0;i<sentence.length;i++) {
			if (sentence[i]==true) s+=",1";
			else s+=",0";
		}
		System.out.println("	"+s.substring(1));
	}
	
	/* printout2: print out a cnf form for debugging purpose */
	public static void printout2(boolean[][] cnf) {
		String s = "";
		int n1 = cnf.length; int n2 = cnf[0].length;
		for (int i=0;i<n1;i++) {
			String k = "";
			for (int j=0;j<n2;j++) {
				if (cnf[i][j]==true) k+=",1";
				else k+=",0";
			}
			s+="  ("+k.substring(1)+")\n";
		}
		System.out.println("cnf: \n"+s);
	}
	
	/* printout3: print out S assignment for debugging purpose */
	public static void printout3(int[][] S) {
		int n1 = S.length; int n2 = S[0].length;
		for (int i=0;i<n1;i++) {
			String k = "";
			for (int j=0;j<n2;j++) {
				k+=","+(String) (S[i][j]+" ");
			}
			System.out.println("	 "+k.substring(1));
		}
	}
	
	/* printout4: print out clause for debugging purpose */
	public static void printout2b(ArrayList<boolean[]> clause) {
		int n2 = clause.get(0).length;
		for (int i=0;i<clause.size();i++) {
			String k = "";
			for (int j=0;j<n2;j++) {
				if (clause.get(i)[j]==true) k+=",1";
				else k+=",0";
			}
			System.out.println("	"+k.substring(1));
		}
	}
	
	/* printout5: printout ArrayList<int[]> for debugging purpose */
	public static void printout2c(ArrayList<int[]> CO) {
		int F = CO.size(); int V = CO.get(0).length;
		for (int sd=0;sd<F;sd++) {
			String s = "";
			for (int sd2=0;sd2<V;sd2++) {
				s+=","+(String) (CO.get(sd)[sd2]+" ");
			}
			System.out.println(s.substring(1));	
		}
	}
	
}
