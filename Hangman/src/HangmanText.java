import java.util.*;
import java.io.*;

public class HangmanText {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Welcome to Hangman!");
		
		//Asks player for difficulty setting and makes sure they enter a valid response
		System.out.print("What difficulty would you like? 1-Medium or 2-Very Hard ");
		String dL = scan.nextLine();
		while(!(dL.equals("1")||dL.equals("2"))) {
			System.out.print("Enter a valid response: ");
			dL = scan.next();
		}
		int difficultyLvl = Integer.parseInt(dL);
		
		//Gets a random word from the difficulty of choice
		String theWord = getWord(difficultyLvl);
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
	
	public static String getWord(int difficultyLvl) throws FileNotFoundException {
		//This chooses which file to use based on the difficulty level
		File wList = null;
		int length = 0;
		if(difficultyLvl==1) {
			wList = new File("CommonWords.txt");
			//length = 10000;
		}
		else if(difficultyLvl==2) {
			wList = new File("Words.txt");
			//length = 370099;
		}
		
		//Finds the length of the file
		Scanner getLength = new Scanner(wList);
		List <String>words = new ArrayList<>();
		while(getLength.hasNextLine()){
			length++;
			words.add(getLength.nextLine());
		}
		getLength.close();
		
		//Creates a random integer less than the number of words, and gets the list value at that location
		int rndWord = (int) (Math.random()*length);
		
		/*//Old Method(more looping is slower):
		Scanner getWord = new Scanner(wList);
		int count = 0;
		String theWord = null;
		while(count<rndWord) {
			count++;
			theWord = getWord.next();
		}
		getWord.close();*/
		
		
		//return theWord.toLowerCase();
		return words.get(rndWord).toLowerCase();
	}
}