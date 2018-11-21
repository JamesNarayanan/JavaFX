import java.util.*;
import java.io.*;

public class HangmanText {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Welcome to Hangman!");
		
		//Asks player for difficulty setting and makes sure they enter a valid response
		System.out.print("What difficulty would you like? 1-Medium | 2-Very Hard | 3-Scrabble Words");
		String dL = scan.nextLine();
		while(!(dL.equals("1")||dL.equals("2")||dL.equals("3"))) {
			System.out.print("Enter a valid response: ");
			dL = scan.next();
		}
		int difficultyLvl = Integer.parseInt(dL);
		
		//Gets a random word from the difficulty of choice
		String theWord = null;
		try {
			theWord = getWord(difficultyLvl);
		} catch (IOException e) {e.printStackTrace();}
		//System.out.print(theWord);
		
		//Variable initialization
		String progress = "";
		for(int i = 0; i<theWord.length(); i++) progress += "*";
		boolean gotWord = false;
		boolean first = true;
		int lives = 10;
		
		//This changes the amount of lives depending on the difficulty level
		switch(difficultyLvl) { 
			case 1: lives = 6; break;
			case 3:
			case 2: lives = 8; break;
		}
		char guess;
		String temp = "";
		String enteredLetters = "";
		
		//Loops until player gets the word or fails
		while(gotWord==false) {
			//Only gives them directions and not previously used letters the first time, since there are none
			if(first) {
				System.out.println("*'s represent unknown chracters in the word");
			}
			else {
				System.out.println("\nPreviously entered letters: " + enteredLetters);
			}
			first = false;
			//This is when they enter their guess
			System.out.print("Guess a letter in " + progress + " → ");
			guess = scan.next().charAt(0);
			
			//If the word doesn't contain their guess and they haven't guessed the letter before, they lose a life
			if(!theWord.contains("" + guess) && !enteredLetters.contains("" + guess)) {
				lives--;
				System.out.println("\n" + guess + " is not in the word");
				if(lives>1) System.out.println("You have " + lives + " lives remaining");
				if(lives==1) System.out.println("You have 1 life remaining");
				//If there are no lives left, the player loses
				if(lives==0) {
					System.out.println("Game Over! The word was " + theWord);
					System.exit(0);
				}
				enteredLetters += guess + " ";
			}
			//If they have entered the letter before, they are told so
			else if(enteredLetters.contains("" + guess)) {
				System.out.println("\nYou have already entered this letter");
			}
			//This replaces the asterisks in their hint word with the correct letters
			else {
				for(int i = 0; i<theWord.length(); i++) {
					if(theWord.charAt(i)==guess) {
						temp += theWord.charAt(i);
					}
					else {
						temp += progress.charAt(i);
					}
				}
				progress = temp;
				temp = "";
				enteredLetters += guess + " ";
				System.out.println("\n" + guess + " is in the word!");
			}
			if(progress.equals(theWord)) gotWord = true;
		}
		
		System.out.println("You won with " + lives + " lives left! Your word was " + theWord);
		
		
		scan.close();
	}
	
	private static String getWord(int difficultyLevel) throws IOException {
		//This chooses which file to use based on the difficulty level
		InputStream input = null;
		if(difficultyLevel==1) {
			input = Hangman.class.getResourceAsStream("/WordLists/CommonWords.txt");
		}
		else if(difficultyLevel==2) {
			input = Hangman.class.getResourceAsStream("/WordLists/Scrabble.txt");
		}
		else if(difficultyLevel==3) {
			input = Hangman.class.getResourceAsStream("/WordLists/Words.txt");
		}
		
		List<String> words = Arrays.asList(Hangman.extract(input).split("\n"));
		
		//Creates a random integer less than the number of words, and gets the list value at that location
		int rndWord = (int) (Math.random()*words.size());
		while(
			!words.get(rndWord).toLowerCase().contains("a") &&
			!words.get(rndWord).toLowerCase().contains("e") &&
			!words.get(rndWord).toLowerCase().contains("i") &&
			!words.get(rndWord).toLowerCase().contains("o") &&
			!words.get(rndWord).toLowerCase().contains("u") &&
			!words.get(rndWord).toLowerCase().contains("y") ||
			words.get(rndWord).length()<3) {
			rndWord = (int) (Math.random()*words.size());
		}
		String word = words.get(rndWord).toLowerCase();
		return word.substring(0, word.length()-1);
	}
}