import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Checkexists {
	 public static void main(String[] args)  throws Exception {
		/* read */
		ArrayList<String> myList = new ArrayList<String>();
		int i = 0;
		File file1 = new File("top-tier fhc.txt");
		System.out.println(file1.getCanonicalPath());
		Scanner scanner1 = new Scanner(file1);
		while(scanner1.hasNext() ) {
			String line = scanner1.nextLine();
			System.out.println(line);
			myList.add(line);
			i++;
		}       
		scanner1.close();
		System.out.println("\n\n\n");
		String path = "D:\\Research\\1 Ling\\Bank Holding\\Data\\ffiec pool\\top-tier fhc - Mar 2014\\hierarchy\\";
		 
		/* check 1 */
		boolean check = new File(path,"1039502.pdf").exists();
		System.out.println(check);
		
		/* check one by one*/
		ArrayList<String> needList = new ArrayList<String>();
		i = 0;
		for (int j=0;j<myList.size();j++) {
			check = new File(path,myList.get(j)+".pdf").exists();
			if (check==true) needList.add(myList.get(j));
		}
		
		/* write to file */
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(path+"downloaded.txt"));
		for (int j=0;j<needList.size();j++) {
			writer.write(needList.get(j)+'\n');
		}
		writer.close();
	 }
}
