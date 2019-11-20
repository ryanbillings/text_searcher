package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TextSearcher {
	
	private String[] words;
	private TextTokenizer lexer;
	private final String VALID_CHARACTERS = "[a-zA-Z0-9']+";

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
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
		List<String> wordsFound = new ArrayList<String>();
		
		for(int i=0; i<words.length; i++) {
			if(stringContains(words[i], queryWord)) {
				String s = previous(i-1, contextWords) + words[i] + next(i+1, contextWords);
				wordsFound.add(s);
			}
		}
		
		String[] strArr = new String[wordsFound.size()];
		strArr = wordsFound.toArray(strArr);
		return strArr;
	}
}

// Any needed utility classes can just go in this file

