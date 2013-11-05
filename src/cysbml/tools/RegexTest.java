package cysbml.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	
	public static void main(String[] args){
		String text = "BIOMD0000000070, BIOMD0000000071";
		Pattern pattern = Pattern.compile("((BIOMD|MODEL)\\d{10})|(BMID\\d{12})");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()){
			String id = matcher.group();
			System.out.println(id);
		}
		
	}
	
}
