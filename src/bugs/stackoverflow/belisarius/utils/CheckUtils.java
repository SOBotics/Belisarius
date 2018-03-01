package bugs.stackoverflow.belisarius.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class CheckUtils {

	public static HashMap<Integer, String> checkForBlackListedWords(String target, String postType){
		HashMap<Integer, String> caught = new HashMap<Integer, String>();
		HashMap<Integer,String> blacklistedWords = DatabaseUtils.getBlacklistedWords(postType);
		
		try {
		Iterator iterator = blacklistedWords.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, String> blacklistedWord = (Map.Entry<Integer, String>)iterator.next();
			if(checkIfContainsKeyword(target, blacklistedWord.getValue()))
			{
				caught.put(blacklistedWord.getKey(), blacklistedWord.getValue());
			}
		}
		} catch (Exception e) {
		e.printStackTrace();	
		}
        return caught;
    }
   
	public static boolean checkIfContainsKeyword(String target, String keyword){
        String body = stripTags(target);
		return body.toLowerCase().contains(keyword.toLowerCase());
	}
	
    private static String stripTags(String target) {
        Document doc = Jsoup.parse("<body>"+target+"</body>");
        doc.getElementsByTag("a").remove();
        doc.getElementsByTag("code").remove();
        doc.getElementsByTag("img").remove();
        doc.getElementsByTag("pre").remove();
        doc.getElementsByTag("blockquote").remove();
        return doc.outerHtml();
    }
    
    private static String removeHtml(String target) {
    	return Jsoup.parse(target).text();
    }
    
    public static String checkForLongWords(String target){
        String bodyParts[] = removeHtml(stripTags(target)).replaceAll("[^a-zA-Z ]", " ").split(" ");
        for(String part : bodyParts){
            if (part.length()>50){
                return part;
            }
        }
        return null;
    }
    
    public static boolean checkIfNoCodeBlock(String target){
        return (!target.contains("<code>"));
    }
    
    public static double getJaroWiklerScore(String original, String target, double percentage) {
    	String originalBody = stripTags(original);
    	String targetBody = stripTags(target);
    	double score = 1.0;
    	
    	if (targetBody.length() < originalBody.length()*(percentage)) {
    		JaroWinkler js = new JaroWinkler();
    		score = js.similarity(targetBody, originalBody);
    	}

		return  score;
    }

    		
    public static String checkForFewUniqueCharacters(String target) {
    	String body = removeHtml(stripTags(target));
    	
    	long uniquesCount = body.chars().distinct().count();
    	if ((body.length() >= 30 && uniquesCount <= 6) || body.length()>=100 && uniquesCount <= 15) {
    		return body.chars().distinct().collect(StringWriter::new, StringWriter::write, (swl, swr) -> swl.write(swr.toString())).toString();
    	}
    	
    	return null;
    }
    
   
    public static HashMap<Integer, String> checkForOffensiveWords(String target) {
		HashMap<Integer, String> caught = new HashMap<Integer, String>();
		HashMap<Integer,String> offensiveWords = DatabaseUtils.getOffensiveWords();

		try {
			Iterator iterator = offensiveWords.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, String> offensiveWord = (Map.Entry<Integer, String>)iterator.next();
				
				Pattern pattern = Pattern.compile(offensiveWord.getValue());
	        	
				if (checkIfBodyContainsOffensiveWord(pattern, target)) {
					caught.put(offensiveWord.getKey(), offensiveWord.getValue());
				}
			}
		} catch (Exception e) {
		e.printStackTrace();	
		}
        return caught;
    }
        
	public static boolean checkIfBodyContainsOffensiveWord(Pattern pattern, String target){
        String body = removeHtml(target);
        return pattern.matcher(body).find();
	}
	
	public static Set<String> checkRepeatedWords(String target) {
		Set<String> repeatedWords = new HashSet<String>();
		
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
		
		return repeatedWords;
	}
    
}
