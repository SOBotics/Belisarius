package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class CheckUtils {

	public static List<String> checkForBlackListedWords(String target, String path){
        return checkForListedWords(target, path);
    }
	
    private static List<String> checkForListedWords(String target, String file){
        List<String> words = new ArrayList<String>();
    	
    	try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            for(String word : lines){
                if(checkIfBodyContainsKeyword(target, word.trim()))
                	words.add(word.trim());
            }
        }
        catch (IOException exception){
            System.out.println(file+" not found.");
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
        String bodyParts[] = target.replaceAll("[^a-zA-Z ]", " ").split(" ");
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
    
}
