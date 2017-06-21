package bugs.stackoverflow.belisarius.reasons;

import java.util.*;

public class RepeatedWords implements Reason {

	private String target;
	private Set<String> repeatedWords = new HashSet<String>();
	
	private boolean isCheckOnBody;
	
	public RepeatedWords(String target, boolean isCheckOnBody) {
		this.target = target;
		this.isCheckOnBody = isCheckOnBody;
	}
	
	public boolean isHit() {
		if (this.isCheckOnBody) {
		
			try{
				String[] words = target.split("\\W");
				for (String word : words) {
					if (!repeatedWords.contains(word)) {
						repeatedWords.add(word);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return repeatedWords.size() <= 5;
	}

	@Override
	public String getDescription() {
		String repeatedWords = "Repeated word" + (this.getRepeatedWords().size() > 1 ? "s" : "") + " found; ";
		
		for (String word : this.getRepeatedWords()) {
			repeatedWords += "**" + word + "**; ";
		}
		
		return repeatedWords.trim();
	}
	
	public Set<String> getRepeatedWords() {
		return this.repeatedWords;
	}

}
