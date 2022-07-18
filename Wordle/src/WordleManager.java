import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WordleManager {
	private int guesses;
	private int size;
	private List<LetterInventory> activeInventories;
	private List<LetterInventory> possibleAnswers;
	private List<LetterInventory> activeCopy;
	private List<LetterInventory> answerCopy;
	private boolean[][] illegalCharacters;
	private HashMap<Character, Integer> requiredCharacters;
	private char[] currentAns;
    private boolean gameOver;

	
	/**
	 * Constructor for wordle game. Starts the game with words of length letters
	 * @param letters length of guesses
	 */
	public WordleManager(int letters) {
		guesses = 6;
		size = letters;
		activeInventories = new ArrayList<>();
		possibleAnswers = new ArrayList<>();
		illegalCharacters = new boolean[5][26];
		requiredCharacters = new HashMap<>();
		currentAns = new char[] {'-', '-', '-', '-', '-'};
		initializeActiveInventories();
		activeCopy = new ArrayList<>(activeInventories);
		answerCopy = new ArrayList<>(possibleAnswers);
		gameOver = false;
	}
	
	/**
	 * Resets each field to initial state
	 */
	public void reset() {
		activeInventories.clear();
		for(int i = 0; i < illegalCharacters.length; i++) {
			for(int j = 0; j < illegalCharacters[0].length; j++) {
				illegalCharacters[i][j] = false;
			}
		}
		requiredCharacters.clear();
		for(int i = 0; i < currentAns.length; i++) {
			currentAns[i] = '-';
		}
		activeInventories = new ArrayList<>(activeCopy);
		possibleAnswers = new ArrayList<>(answerCopy);
		gameOver = false;
	}
	
	/**
	 * Helper to create active inventories with an existing dictionary
	 */
	private void initializeActiveInventories() {
		String nameOfFile = size + "Letter.txt";
		File letterFile = new File("dictionaries/" + nameOfFile);
		if(letterFile.length() == 0) {
			createLetterFile(size);
		}
		try {
			// populate active words
			@SuppressWarnings("resource")
			BufferedReader fr = new BufferedReader(new FileReader("dictionaries/" + nameOfFile));
			String line = fr.readLine();
			while(line != null) {
				activeInventories.add(new LetterInventory(line));
				line = fr.readLine();
			}
			fr = new BufferedReader(new FileReader("dictionaries/wordleAnswers.txt"));
			line = fr.readLine();
			while(line != null) {
				possibleAnswers.add(new LetterInventory(line));
				activeInventories.add(new LetterInventory(line));
				line = fr.readLine();
			}
			updateSortedFrequencies();
		} catch (IOException e) {
			System.out.println("file does not exist");
			e.printStackTrace();
		}
	}
		
	/**
	 * Creates a file with letterCount sized words
	 * @param letterCount size of words in the file
	 */
	public void createLetterFile(int letterCount) {
		if(letterCount < 1) {
			throw new IllegalArgumentException("letter count must be a positive number. Currently: " + letterCount);
		}
		try {
	        File myObj = new File("dictionaries/" +letterCount + "Letter.txt");
	        if (myObj.createNewFile()) {
	            System.out.println("File created: " + myObj.getName());
	        } else {
	            System.out.println("File already exists.");
	        }
	    } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter("dictionaries/" + letterCount + "Letter.txt");
			BufferedReader reader = new BufferedReader(new FileReader("dictionaries/engmix.txt"));
			String line = reader.readLine();
			while(line != null) {
				if(line.length() == letterCount) {
					fw.write(line + "\n");
				}
				line = reader.readLine();
			}
			fw.close();
			reader.close();
			System.out.println("Sucessfully written");
		} catch (IOException e) {
			System.out.println("Could not write to file");
		}
	}
	
	/**
	 * Calculates the overlaps and sorts the letter inventories
	 */
	private void updateSortedFrequencies() {
		clearAllOverlaps();
		calculateOverLaps();
		Collections.sort(activeInventories);
	}
	
	/**
	 * Gets the best guess, which should be the first element (most overlaps)
	 * @return activeInventories.get(0).getWord();
	 */
	public String getBestGuess() {
		if(activeInventories.size() == 0 && possibleAnswers.size() == 0) {
			return null; // no guess
		} 
		// if only one guess, at least guess from answers list
		return guesses == 1 || activeInventories.isEmpty() ? possibleAnswers.get(0).getWord() : activeInventories.get(0).getWord();
	}
	
	/**
	 * Takes in a guess and updates the active word list
	 * @param guess with results
	 */
	public void processGuess(String guess) {
		if(guess.length() != 10) {
			throw new IllegalArgumentException("Invalid guess: " + guess);
		}
		guesses--;
		HashMap<Character, Integer> tempReq = new HashMap<>();
		for(int i = 0; i < guess.length(); i += 2) {
			char guessLetter = guess.charAt(i);
			char status = guess.charAt(i + 1);
			switch(status) {
				case 'N':
					if(!requiredCharacters.containsKey(guessLetter) && !tempReq.containsKey(guessLetter)) {
						for(int j = 0; j < illegalCharacters.length; j++) {
							illegalCharacters[j][guessLetter - 'a'] = true;
						}
					} else {
						illegalCharacters[i / 2][guessLetter - 'a'] = true;
					}
					break;
				case 'Y':
					illegalCharacters[i / 2][guessLetter - 'a'] = true;
					tempReq.put(guessLetter, tempReq.getOrDefault(guessLetter, 0) + 1);
					break;
				case 'G':
					tempReq.put(guessLetter, tempReq.getOrDefault(guessLetter, 0) + 1);
					currentAns[i / 2] = guessLetter;
					break;
			}
		}
		updateReqCharacters(tempReq);
		updateInventories(true);
		updateInventories(false);
		updateSortedFrequencies();
		// no answers is game OVER
		if(possibleAnswers.size() == 0 || !(new String(currentAns).contains("-"))) {
			gameOver = true;
		}
	}
	
	/**
	 * Updates required characters with new guess
	 * @param tempReq required characters for current guess
	 */
	private void updateReqCharacters(HashMap<Character, Integer> tempReq) {
		for(Character c : tempReq.keySet()) {
			requiredCharacters.put(c, tempReq.get(c));
		}
	}

	/**
	 * Removes characters from active inventories or answer list depending on the current guess
	 * @param answerList if true, update inventory of possible answers
	 */
	private void updateInventories(boolean answerList) {
		Iterator<LetterInventory> itr = answerList ? possibleAnswers.iterator() : activeInventories.iterator();
		while(itr.hasNext()) {
			LetterInventory curInventory = itr.next();
			// remove if doesn't match correct pattern of correct characters || contains illegal character || missing required letters
			if(!curInventory.match(currentAns) || curInventory.containsIllegalChar(illegalCharacters)
					|| curInventory.missingRequired(requiredCharacters)) {
				itr.remove();
			}
		}
	}

	/**
	 * Calculates the overlaps in the active inventories by comparing the letter inventories of each
	 * word to each other
	 */
	private void calculateOverLaps() {
		for(int i = 0; i < activeInventories.size() - 1; i++) {
			for(int j = i + 1; j < activeInventories.size(); j++) {
				activeInventories.get(i).getOverlaps(activeInventories.get(j));
			}
		}
	}


	/**
	 * Restore overlaps to 0 to find new overlaps
	 */
	private void clearAllOverlaps() {
		for(LetterInventory ai : activeInventories) {
			ai.clearOverlap();
		}
	}
	
	/**
	 * Getting probability of correct word
	 * @return 1 / # of active words
	 */
	public double getProbability() {
		return 1.0 / activeInventories.size();
	}

	/**
	 * Gets current ans
	 * @return currentAns.toString()
	 */
	public String currentAns() {
		return new String(currentAns);
	}
	
	/**
	 * Debugging purposes. Prints remaining size of in inventory
	 */
	public void printRemaining() {
		System.out.println("Remaining: " + activeInventories.size() + " " + activeInventories);
	}
	
	/**
	 * Gets if a wordle is likely solvable or not. After a number of words are removed per turn
	 * If the remaining words is not expected to be 1 by the last turn comes, it will come out to be unlikely
	 * @return true is expected remWords < 2 by final round
	 */
	public boolean likelySolvable() {
		final double WORD_DIVISOR = 7.5;
		double remWords = activeInventories.size();
		for(int i = 0; i < guesses; i++) {
			remWords /= WORD_DIVISOR;
		}
		return remWords < 2;
	}
	
	/**
	 * Gets if the game is over
	 */
	public boolean gameOver() {
		return gameOver;
	}
}
