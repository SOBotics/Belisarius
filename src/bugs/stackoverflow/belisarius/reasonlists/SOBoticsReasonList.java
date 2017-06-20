package bugs.stackoverflow.belisarius.reasonlists;

import bugs.stackoverflow.belisarius.reasons.*;

import java.util.ArrayList;
import java.util.List;

public class SOBoticsReasonList implements ReasonList {

	private String target;
	private String original;
	
	private boolean isCheckOnTitle;
	private boolean isCheckOnBody;
	
	private double quantifier;
	
	public SOBoticsReasonList(String target, String original, boolean isCheckOnTitle, boolean isCheckOnBody, double quantifier) {
		this.target = target;
		this.original = original;
		this.isCheckOnTitle = isCheckOnTitle;
		this.isCheckOnBody = isCheckOnBody;
		this.quantifier = quantifier;
	}
	
	@Override
	public List<Reason> reasons() {
		List<Reason> reasons = new ArrayList<Reason>();
		
		reasons.add(new KeywordFound(this.target, this.isCheckOnTitle, this.isCheckOnBody));
		reasons.add(new TextRemoved(this.target, this.original, quantifier));
		return reasons;
	}


}
