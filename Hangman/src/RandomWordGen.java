import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RandomWordGen {
	public static void main(String[] args) throws FileNotFoundException {
		String wordList = "src/WordLists/Scrabble.txt";
		Scanner scan = new Scanner(new File(wordList));
		ArrayList<String> words = new ArrayList<>(370100);
		while(scan.hasNext()) {
			words.add(scan.nextLine());
		}
		for(int i = 0; i<10; i++) {
			System.out.println(words.remove((int) (Math.random()*words.size())).toLowerCase());
		}
		scan.close();
	}
}
