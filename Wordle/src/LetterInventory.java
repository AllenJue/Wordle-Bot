import java.util.HashMap;

public class LetterInventory implements Comparable<LetterInventory>{
    private final static int ALPHABET_SIZE = 26;
    private String original;
    private int[] freq;
    private int numLetters;
    private int overLaps;

    /**
     * Constructor that accepts a String and counts the letters in freq <br>
     * pre: input != null
     * 
     * @param input that needs to be inventoried
     */
    public LetterInventory(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input can not be null");
        }
        freq = new int[ALPHABET_SIZE];
        overLaps = 0;
        original = input;
        numLetters = 0;
        for (char c : input.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                freq[c - 'a']++;
                numLetters++;
            }
        }
    }

    /**
     * A method that accepts a char and returns the frequency of that letter in this
     * LetterInventory<br>
     * pre: 'a' <= tgt <= 'z' || 'A' <= tgt <= 'Z'
     * 
     * @param tgt is the character whose frequency should be returned
     * @return the frequency that tgt appears
     */
    public int get(char tgt) {
        return freq[Character.toLowerCase(tgt) - 'a'];
    }

    /**
     * a method named size that returns the total number of letters in this LetterInventory.
     * 
     * @return the number of letters in LetterInventory
     */
    public int size() {
        return numLetters;
    }

    /**
     * a method named isEmpty that returns true if the size of this LetterInventory is 0, false
     * otherwise.
     * 
     * @return if a LetterInventory is empty
     */
    public boolean isEmpty() {
        return numLetters == 0;
    }

    /**
     * a method named toString that returns a String representation of this LetterInventory. All
     * letters in the inventory are listed in alphabetical order.
     * 
     * @return a String in alphabetical order with the number of occurrences of each letter in
     *         LetterInventory
     */
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (int letter = 0; letter < freq.length; letter++) {
//            for (int frequency = 0; frequency < freq[letter]; frequency++) {
//                sb.append((char) (letter + 'a'));
//            }
//        }
//        return sb.toString();
    	return original;
    }

    /**
     * Updates the number of overlaps between two words
     * @param other LetterInventory to be compared to
     */
    public void getOverlaps(LetterInventory other) {
    	for(int i = 0; i < freq.length; i++) {
    		// find overlap. Minimum occurence for both
    		int overLap = Math.min(this.freq[i], other.freq[i]);
    		this.overLaps += overLap;
    		other.overLaps += overLap;
    	}
    }
    /**
     * a method that gets a new LetterInventory created by adding the frequencies from the calling
     * LetterInventory object to the frequencies of the letters in the explicit parameter. <br>
     * pre: other != null <br>
     * post: this and other are not altered
     * 
     * @param other the explicit LetterInventory that is adding to this
     * @return a LetterInventory that has the combined frequencies of the calling LetterInventory
     *         and the explicit parameter
     */

    public LetterInventory add(LetterInventory other) {
        return generalizedOperator(other, true);
    }

    /**
     * Gets a LetterInventory that contains the frequency of the difference between the frequencies
     * of two LetterInventory objects, or null if a frequency is < 0. <br>
     * pre: other != null <br>
     * post: this and other are not altered
     * 
     * @param other the explicit LetterInventory that is subtracting from this
     * @return a LetterInventory that has the difference in frequencies of the calling
     *         LetterInventory and explicit parameter, or null if there is a negative frequency
     */
    public LetterInventory subtract(LetterInventory other) {
        return generalizedOperator(other, false);
    }

    /**
     * Gets the sum or difference of two LetterInventory's depending on positive<br>
     * pre: other != null
     * 
     * @param other    the explicit LetterInventory
     * @param positive the sign for whether the LetterInventory's should be added or subtracted
     * @return positive ? Sum of LetterInventory's : difference of LetterInventory's
     */
    private LetterInventory generalizedOperator(LetterInventory other, boolean positive) {
        if (other == null) {
            throw new IllegalArgumentException(
                    "Can not add or subtract with a null LetterInventory");
        }
        int sign = positive ? 1 : -1;
        int ansSize = 0;
        LetterInventory ans = new LetterInventory("");
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            // get the difference if subtracting, or sum if adding
            ans.freq[i] = this.freq[i] + (sign * other.freq[i]);
            if (ans.freq[i] < 0) {
                return null;
            }
            ansSize += ans.freq[i];
        }
        ans.numLetters = ansSize;
        return ans;
    }

    /**
     * Gets the original word for this letter inventory
     * @return original
     */
    public String getWord() {
    	return original;
    }
    
    /**
     * Matches current inventory with ans
     * @param currentAns current ans in wordle
     * @return true if is a viable answer still
     */
    public boolean match(char[] currentAns) {
    	for(int i = 0; i < currentAns.length; i++) {
    		if(currentAns[i] != '-' && original.charAt(i) != currentAns[i]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Restores overlaps to original
     */
    public void clearOverlap() {
    	overLaps = 0;
    }
    
    /**
     * Compares if two LetterInventory objects are equal
     * 
     * @param o the object that needs to be compared to this
     * @return o is the exact same as this
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof LetterInventory) {
            LetterInventory other = (LetterInventory) o;
            if (other.size() == this.size()) {
            	return this.original.equals(other.original);
                // return Arrays.equals(other.freq, this.freq);
            }
        }
        return false;
    }

    /**
     * Used to sort LetterInventories by greatest to least number of overlaps
     */
	@Override
	public int compareTo(LetterInventory other) {
		return other.overLaps - this.overLaps;
	}
	
	/**
	 * Creates hashCode for letter inventories. Just the count of letters appended onto each other
	 * @return hashCode for LetterInventory
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		for(int i : freq) {
			hash *= 10;
			hash += i;
		}
		return hash;
	}

	/**
	 * Gets if the letter inventory contains an illegal character for the Wordle
	 * @param illegalCharacters 2d array containing illegal characters for each position
	 * @return true if this contains an illegal character
	 */
	public boolean containsIllegalChar(boolean[][] illegalCharacters) {
		for(int i = 0; i < original.length(); i++) {
			if(illegalCharacters[i][original.charAt(i) - 'a']) {
				// System.out.println("Removing: " + original + " because illegal character " + original.charAt(i));
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets if the letter inventory is missing a required character for the Wordle
	 * @param requiredCharacters HashMap containing the required characters and their counts
	 * @return true if missing a required character
	 */
	public boolean missingRequired(HashMap<Character, Integer> requiredCharacters) {
		for(Character c : requiredCharacters.keySet()) {
			if(requiredCharacters.get(c) > this.get(c)) {
				// System.out.println("Removing: " + original + " because missing required character " + c);
				return true;
			}
		}
		return false;
	}
}
