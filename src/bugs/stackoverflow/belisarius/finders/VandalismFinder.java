package bugs.stackoverflow.belasarius.finders;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class VandalismFinder {

	private String string1;
	private String string2;
		
	private double quantifier;
	
	public VandalismFinder(String s1, String s2, double quantifier) {
		this.string1 = s1;
		this.string2 = s2;
		this.quantifier = quantifier;
	}
	
	public Boolean vandalismFound() {
		Boolean vandalismFound = false;
		
		JaroWinkler js = new JaroWinkler();
		
		double score = js.similarity(string1, string2);
		
		if (score * quantifier < 0.5) {
			vandalismFound = true;
		}
		
		return vandalismFound;
	}
	
}
