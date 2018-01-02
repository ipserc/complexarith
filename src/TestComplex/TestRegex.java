package TestComplex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestRegex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String regex = "[ \\t]*(([\\+|\\-])?(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?))?[ \\t]*(([\\+|\\-])?[ \\t]*(\\d*\\.?\\d+(E[\\+|\\-|\\d]\\d*)?)?)(i)?";
		final String string = "+i";

		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
		    System.out.println("Full match: " + matcher.group(0));
		    for (int i = 1; i <= matcher.groupCount(); i++) {
		        System.out.println("Group " + i + ":" + matcher.group(i)+":");
		    }
		}
	}
}
