package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextSearcher {
	
	private String[] words;
	private TextTokenizer lexer;
	private final String VALID_CHARACTERS = "[a-zA-Z0-9']+";
	private Map<String, Set<Integer>> memo = new HashMap<String, Set<Integer>>();

	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;
		
		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}
		
		init(w.toString());
		r.close();
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	private void init(String fileContents) {
		lexer = new TextTokenizer(fileContents, VALID_CHARACTERS);
		List<String> tokens = new ArrayList<String>();
		while(lexer.hasNext()) {
			tokens.add(lexer.next());
		}
		words = (String[])tokens.toArray(new String[tokens.size()]);
	}
	
	/**
	 * Case insensitive comparison to see if a contains b
	 * @param a, the original word in the text
	 * @param b, the query being searched
	 * @return true if a contains b, false otherwise
	 */
	private boolean stringContains(String a, String b) {
		return a.toLowerCase().contains(b.toLowerCase());
	}
	
	/**
	 * Returns the previous n words from a given index
	 * @param index - the index into the words array that is a search match
	 * @param n - the previous n words to process
	 * @return a string of the previous n words, or just the beginning of the file if reached
	 */
	private String previous(int index, int n) {
		StringBuilder sb = new StringBuilder("");
		while (n > 0 && index >= 0) {
			sb.insert(0, words[index]);
			if (lexer.isWord(words[index])) {
				n--;
			}
			index--;
		}
		return sb.toString();
	}
	
	/**
	 * Returns the next n words from a given index
	 * @param index - the index into the words array that is a search match
	 * @param n - the next n words to process
	 * @return - a string of the next n words, or the rest of the file if the end is reached
	 */
	private String next(int index, int n) {
		StringBuilder sb = new StringBuilder("");
		while (n > 0 && index < words.length) {
			sb.append(words[index]);
			if (lexer.isWord(words[index])) {
				n--;
			}
			index++;
		}
		return sb.toString();
	}
	
	/**
	 * Gets the word within the words array at the index
	 * Prepends the previous n contextWords and appends the next n contextWords
	 * @param index, the index within words
	 * @param contextWords, the number of words to add on both sides
	 * @return a string computing the contextWords from both sides of the word at words[index]
	 */
	private String processWord(int index, int contextWords) {
		return previous(index-1, contextWords) + words[index] + next(index+1, contextWords);
	}

	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
		List<String> wordsFound = new ArrayList<String>();
		queryWord = queryWord.toLowerCase();
		
		// Check if the query has been memoized
		if (memo.containsKey(queryWord)) {
			Set<Integer> querySet = memo.get(queryWord);
			for(int i:querySet) {
				wordsFound.add(processWord(i, contextWords));
			}
		} else {
			Set<Integer> memoSet = new HashSet<Integer>();
			// Loop through the text file
			for(int i=0; i<words.length; i++) {
				if(stringContains(words[i], queryWord)) {
					String s = processWord(i, contextWords);
					wordsFound.add(s);
					
					// Update memo
					memoSet.add(i);
				}
			}
			memo.put(queryWord, memoSet);
		}
		
		String[] strArr = new String[wordsFound.size()];
		strArr = wordsFound.toArray(strArr);
		return strArr;
	}
}
