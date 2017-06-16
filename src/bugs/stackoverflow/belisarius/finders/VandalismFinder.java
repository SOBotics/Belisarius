package bugs.stackoverflow.belisarius.finders;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class VandalismFinder {

	private String string1;
	private String string2;
	private static double score;
		
	private double quantifier;
	
	public VandalismFinder(String s1, String s2, double quantifier) {
		this.string1 = removeIntentionalRepetitions(s1);
		this.string2 = removeIntentionalRepetitions(s2);
		this.quantifier = quantifier;
	}
	
	public Boolean vandalismFound() {
		 if (string1.length() < string2.length()*(0.6)) {
			return true;
		 }
		
		JaroWinkler js = new JaroWinkler();
		
		score = js.similarity(string1, string2);
		
		if (score * quantifier < 0.6) {
			return true;
		}
		
		return false;
	}
	
	public static double getScore() {
		return score;
	}
	
	private static String removeIntentionalRepetitions(String result) {
		// TODO Improve implementation
		//1 first if 3 letter or more
		//2 if word contains 2 letters in sequenze and is over threshold
		return result.replaceAll("(.)\\1{2,}", "$1");
	}
	
}
