package bugs.stackoverflow.belisarius.reasons;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bugs.stackoverflow.belisarius.models.Post;

public class KeywordFound implements Reason {

	private String postType;
	private String target;
	private List<String> keywordsFound = new ArrayList<String>();
	
	private boolean isCheckOnTitle;
	private boolean isCheckOnBody;
	
	public static String regexTitleFile = "./ini/regex_title.txt";
	public static String regexQuestionBodyFile = "./ini/regex_question_body.txt";
	public static String regexAnswerBodyFile = "./ini/regex_answer_body.txt";
	
	public KeywordFound(String postType, String target, boolean isCheckOnTitle, boolean isCheckOnBody) {
		this.postType = postType;
		this.target = target.toLowerCase();
		this.isCheckOnTitle = isCheckOnTitle;
		this.isCheckOnBody = isCheckOnBody;
	}
	
	@Override
	public boolean isHit() {
		try {
			List<Pattern> regexs = getRegexs(getFilename());
			for (Pattern pattern : regexs) {
				Matcher matcher = pattern.matcher(this.target);
				if (matcher.find()) {
					this.keywordsFound.add(matcher.group());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.keywordsFound.size()>0;
	}
	
	private List<Pattern> getRegexs(String fileName) throws IOException {

		List<Pattern> patterns = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			String line = br.readLine();
			while (line != null) {
				if (line.trim().length() > 0) {
					Pattern p = Pattern.compile(line, Pattern.CASE_INSENSITIVE);
					patterns.add(p);
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return patterns;
	}
	
	private String getFilename() {
		
		switch (this.postType) {
			case Post.postType_Question: {
				if (this.isCheckOnBody) {
					return regexQuestionBodyFile;
				} else if (this.isCheckOnTitle) {
					return regexTitleFile;
				}
			}
			case Post.postType_Answer : return regexAnswerBodyFile;
		    
			default:
				throw new RuntimeException("Unable to determine post type to return correct filename. Post type; " + this.postType + ".");
		}

	}

	@Override
	public String getDescription() {
		String keywordsFound = "Keyword" + (this.getFoundKeywords().size() > 1 ? "s" : "") + " found; ";
		
		for (String keyword : this.getFoundKeywords()) {
			keywordsFound += "**" + keyword + "**; ";
		}
		
		return keywordsFound.trim();
	}
	
	public List<String> getFoundKeywords() {
		return this.keywordsFound;
	}

}