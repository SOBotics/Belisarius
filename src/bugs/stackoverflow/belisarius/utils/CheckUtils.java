package bugs.stackoverflow.belisarius.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class CheckUtils {

	public static List<String> checkForBlackListedWords(String target, String path){
        return checkForListedWords(target, path);
    }
	
    private static List<String> checkForListedWords(String target, String path){
        List<String> words = new ArrayList<String>();
    	
    	try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for(String word : lines){
                if(checkIfBodyContainsKeyword(target, word.trim()))
                	words.add(word.trim());
            }
        }
        catch (IOException exception){
            System.out.println(path + " not found.");
        }
        return words;
    }
    
	public static boolean checkIfBodyContainsKeyword(String target, String keyword){
        String body = stripBody(target);
		return body.toLowerCase().contains(keyword.toLowerCase());
	}
	
    private static String stripBody(String target) {
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
        String bodyParts[] = removeHtml(stripBody(target)).replaceAll("[^a-zA-Z ]", " ").split(" ");
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
    	String originalBody = stripBody(original);
    	String targetBody = stripBody(target);
    	double score = 1.0;
    	
    	if (targetBody.length() < originalBody.length()*(percentage)) {
    		JaroWinkler js = new JaroWinkler();
    		score = js.similarity(targetBody, originalBody);
    	}

		return  score;
    }

    		
    public static String checkForFewUniqueCharacters(String target) {
    	String body = removeHtml(stripBody(target));
    	
    	long uniquesCount = body.chars().distinct().count();
    	if ((body.length() >= 30 && uniquesCount <= 6) || body.length()>=100 && uniquesCount <= 15) {
    		return body.chars().distinct().collect(StringWriter::new, StringWriter::write, (swl, swr) -> swl.write(swr.toString())).toString();
    	}
    	
    	return null;
    }
    
   
    public static List<String> checkForOffensiveWords(String target, String path) {
        List<String> words = new ArrayList<String>();
        
        List<Pattern> patterns = getRegexs(path);
        
		for (Pattern pattern : patterns) {
			if (checkIfBodyContainsOffensiveWord(pattern, target)) {
				words.add(pattern.toString());
			}
		}
    	
        return words;
    }
    
    private static List<Pattern> getRegexs(String path) {
    	List<Pattern> patterns = new ArrayList<Pattern>();
    	
    	try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for(String word : lines){
            	Pattern pattern = Pattern.compile(word);
            	patterns.add(pattern);
            }
        }
        catch (IOException exception){
            System.out.println(path + " not found.");
        }
    	
    	return patterns;
    }
    
	public static boolean checkIfBodyContainsOffensiveWord(Pattern pattern, String target){
        String body = removeHtml(target);
        
        boolean match = pattern.matcher(body).find();
		if (match) {
			return true;
		}
		
		return false;
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
