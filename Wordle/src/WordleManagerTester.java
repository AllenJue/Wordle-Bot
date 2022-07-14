import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordleManagerTester {
	private int games;
	private int wins;
	private long totalTurnsTaken;
	private List<LetterInventory> activeInventories;
	private List<String> possibleAnswers;
	
	/**
	 * Constructor for Wordle Bot tester
	 */
	public WordleManagerTester() {
		games = 0;
		wins = 0;
		totalTurnsTaken = 0;
		activeInventories = new ArrayList<>();
		possibleAnswers = new ArrayList<>();
		initializeActiveInventories();
	}
	
	/**
	 * Tests wordle bot numTests number of times on random words
	 * @param numTests the number of tests for wordle bot to try
	 */
	public void test(int numTests) {
		WordleManager m = new WordleManager(5);
		games = numTests;
		for(int i = 0; i < numTests; i++) {
			LetterInventory secretWord = new LetterInventory(getSecretWord());
			// System.out.println("New game, secret word is: " + secretWord);
			int guesses = 6;
			while(guesses > 0 && !m.currentAns().equals(secretWord.getWord())) {
				String bestGuess = m.getBestGuess();
				guesses--;
				String response = makeGuess(bestGuess, secretWord);
				// System.out.println("Best guess: " + bestGuess + " processed guess: " + response + " and word: " + secretWord.getWord());
				m.processGuess(response);
			}
			if(m.currentAns().equals(secretWord.getWord())) {
				wins++;
			}
			totalTurnsTaken += 6 - guesses;
			m.reset();
		}
		System.out.println("Results over " + games + " games:");
		System.out.println("Win %: " + getWinPercentage());
		System.out.println("Avg number of turns: " + getAvgTurns());
	}
	
	/**
	 * Tries making a guess with the best guess and secret word
	 * @param bestGuess best guess for algorithm currently
	 * @param secretWord target word
	 * @return a String where the best guess letters are interleaved with
	 * 'G' meaning correct letter and position, 'Y' correct letter incorrect position,
	 * and 'N' not correct letter
	 */
	private String makeGuess(String bestGuess, LetterInventory secretWord) {
		if(bestGuess == null || secretWord == null) {
			throw new NullPointerException("Null parameters. Secretword or bestguess invalid"
					+ "\n best guess: " + bestGuess + " secretWord: " + secretWord);
		}
		int[] freq = new int[26];
		char[] ans = new char[10];
		// do correct letters first
		for(int i = 0; i < bestGuess.length(); i++) {
			char c = bestGuess.charAt(i);
			ans[2 * i] = c;
			// if corresponding space, incremement letter seen and append 'G'
			if(c == secretWord.getWord().charAt(i)) {
				ans[2 * i + 1] = 'G';
				freq[c - 'a']++;
			}
		}
		for(int i = 0; i < bestGuess.length(); i++) {
			if(ans[2 * i + 1] != 'G') {
				char c = bestGuess.charAt(i);
				// if need letter but not in the correct space, increment times seen and append 'Y'; otherwise 'N'
				if(secretWord.get(c) > freq[c - 'a'] && secretWord.getWord().charAt(i) != c) {
					freq[c - 'a']++;
					ans[2 * i + 1] = 'Y';
				} else {
					ans[2 * i + 1] = 'N';
				}
			}
		}
		
		return new String(ans);
	}
	
	/**
	 * Helper to create active inventories with an existing dictionary
	 */
	private void initializeActiveInventories() {
		try {
			// populate active words
			BufferedReader fr = new BufferedReader(new FileReader("dictionaries/5Letter.txt"));
			String line = fr.readLine();
			while(line != null) {
				activeInventories.add(new LetterInventory(line));
				line = fr.readLine();
			}
			fr = new BufferedReader(new FileReader("dictionaries/wordleAnswers.txt"));
			line = fr.readLine();
			while(line != null) {
				possibleAnswers.add(line);
				activeInventories.add(new LetterInventory(line));
				line = fr.readLine();
			}
		} catch (IOException e) {
			System.out.println("file does not exist");
			e.printStackTrace();
		}
	}
	
	/**
	 * Debugging purposes. Prints possible answers
	 */
	public void printData() {
		System.out.println("Possible answers: " + possibleAnswers);
	}
	
	/**
	 * Generates a secret word from the possible answers
	 * @return
	 */
	private String getSecretWord() {
		return possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));
	}
	
	/**
	 * Gets win percentage of all trials
	 * @return wins / games
	 */
	public double getWinPercentage() {
		return wins / (games * 1.0);
	}
	
	/**
	 * Gets the avg number of turns taken per round
	 * @return avgTurnsTaken
	 */
	public double getAvgTurns() {
		return (totalTurnsTaken * 1.0) / games;
	}
}
