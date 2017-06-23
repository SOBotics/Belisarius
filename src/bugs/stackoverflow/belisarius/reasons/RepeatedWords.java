package bugs.stackoverflow.belisarius.reasons;

import java.util.*;

public class RepeatedWords implements Reason {

	private String target;
	private Map<String, Integer> repeatedWords = new HashMap<String, Integer>();
	
	private boolean isCheckOnBody;
	
	public RepeatedWords(String target, boolean isCheckOnBody) {
		this.target = target;
		this.isCheckOnBody = isCheckOnBody;
	}
	
	public boolean isHit() {
		String[] words = target.split("\\W");
		if (this.isCheckOnBody) {
			for (String word : words) {
				if (!this.repeatedWords.containsKey(word)) {
					this.repeatedWords.put(word, 1);
				} else {
					this.repeatedWords.put(word, this.repeatedWords.get(word) + 1);
				}
			}
		}
		return (words.length>=5 && this.repeatedWords.size()<=5 && getCountOfRepeatedWords()>=10);
	}
	
	private Integer getCountOfRepeatedWords() {
	    int count = 0;
		
		for (Integer value : this.repeatedWords.values()) {
			if (value > 1) {
				count += value;
			}
		}
		
		return count;
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
		return this.repeatedWords.keySet();
	}

}
