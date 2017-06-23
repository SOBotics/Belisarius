package bugs.stackoverflow.belisarius.reasons;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class TextRemoved implements Reason {

	private String target;
	private String original;
	
	private double qualifier;
	
	private double score;
	
	public TextRemoved(String target, String original, double qualifier) {
		this.target = target.toLowerCase();
		this.original = original.toLowerCase();
		this.qualifier = qualifier;
	}
	
	@Override
	public boolean isHit() {
		 if (this.target.length() < this.original.length()*(0.7)) {
			JaroWinkler js = new JaroWinkler();
		
			score = js.similarity(this.target, this.original);
			
			if (score * this.qualifier < 0.6) {
				return true;
			}
		 }
		return false;
	}

	@Override
	public String getDescription() {
		double score = Math.round(this.score*100.0)/100.0;
		return "Text removed. Score: " + score;
	}

}
