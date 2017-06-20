package bugs.stackoverflow.belisarius.reasons;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class KeywordFound implements Reason {

	private String target;
	private List<String> keywordsFound = new ArrayList<String>();
	
	private boolean isCheckOnTitle;
	private boolean isCheckOnBody;
	
	public static String regexTitleFile = "./ini/regex_title.txt";
	public static String regexBodyFile = "./ini/regex_body.txt";
	
	public KeywordFound(String target, boolean isCheckOnTitle, boolean isCheckOnBody) {
		this.target = target.toLowerCase();
		this.isCheckOnTitle = isCheckOnTitle;
		this.isCheckOnBody = isCheckOnBody;
	}
	
	@Override
	public boolean isHit() {
		try {
			List<Pattern> regexs = getRegexs(isCheckOnTitle ? regexTitleFile : isCheckOnBody ? regexBodyFile : "");
			for (Pattern pattern : regexs) {
				boolean match = pattern.matcher(this.target).find();
				if (match) {
					this.keywordsFound.add(pattern.toString());
				}
			}
		} catch (IOException e) {
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
					Pattern p = Pattern.compile(line);
					patterns.add(p);
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return patterns;
	}

	@Override
	public String getDescription() {
		return "Keyword found";
	}
	
	public List<String> getFoundKeywords() {
		return this.keywordsFound;
	}

}