package com.ipserc.arith.complex;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ASCII-art box/title rendering used by {@link Complex}'s {@code boxTitle*}/{@code boxText*}/
 * {@code printBoxTitle*}/{@code printBoxText*}/{@code repeat} public methods. Package-private:
 * {@code Complex} keeps the public API (unchanged signatures, delegating one-liners) so external
 * callers (e.g. {@code Syseq}, {@code Spline}, and many test files use {@code Complex.repeat(...)})
 * are unaffected.
 * <p>
 * Extracted verbatim from {@code Complex.java}'s "BOXES &amp; TITLES" section (Sixth session, step 2,
 * Phase 2.1): confirmed zero coupling to {@code Complex} instance fields or thread-local state --
 * every method here takes only {@code (int size, String title/text, ...)} and does pure string/int
 * manipulation, so this was the lowest-risk section to extract first.
 */
final class ComplexBoxArt {

	private ComplexBoxArt() {}

	/**
	 * Substitute for String.repeat(int n). This method is not available for Java 1.8 on Windows
	 * @param str The String to repeat
	 * @param n The number of times to repeat the string
	 * @return The final String
	 */
	static String repeat(String str, int n) {
		String result ="" ;
		for(int i = 0; i < n; ++i) result += str;
		return result;
	}

	/**
	 * Generates a random BoxTitle from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	static String boxTitleRandom(int size, String title) {
		switch (ThreadLocalRandom.current().nextInt(7)+1) {
			case 1: return boxTitle1(size, title);
			case 2: return boxTitle2(size, title);
			case 3: return boxTitle3(size, title);
			case 4: return boxTitle4(size, title);
			case 5: return boxTitle5(size, title);
			case 6: return boxTitle6(size, title);
			case 7: return boxTitle7(size, title);
		}
		return boxTitle1(size, title);
	}

	/**
	 * Generates a BoxTitle from its components
	 * @param size
	 * @param title
	 * @param csi
	 * @param top
	 * @param csd
	 * @param msi
	 * @param msd
	 * @param mdi
	 * @param mdd
	 * @param cii
	 * @param bot
	 * @param cid
	 * @param nmid
	 * @return
	 */
	static String  makeBoxTitle(int size, String title, String csi, String top, String csd, String msi, String msd, String mdi, String mdd, String cii, String bot, String cid, Boolean nmid) {
		String boxTitle = "";
		String theTitleTop;
		String theTitleText;
		String theTitleMid;
		String theTitleBot;

		// Math.max guards the case where 'size' is only 1-3 chars more than the title: the
		// mandatory overhead below (mdi + space + space + mdd = 4 chars) needs titleSize-title.length()
		// >= 4, or ((titleSize-title.length())/2)-2 goes negative, repeat() silently treats that as
		// 0, and the resulting title line ends up LONGER than titleSize -- misaligned vs.
		// theTitleTop/Mid/Bot below, which are always exactly titleSize wide.
		int titleSize = Math.max(size, title.length()+4);
		theTitleTop = csi+repeat(top, titleSize-2)+csd;
		theTitleMid = msi+repeat(" ", titleSize-2)+msd;
		theTitleText = mdi+repeat(" ", ((titleSize-title.length())/2)-2)+" "+title;
		theTitleText += " "+repeat(" ", titleSize-theTitleText.length()-2)+mdd;
		theTitleBot = cii+repeat(bot, titleSize-2)+cid;

		boxTitle = theTitleTop+System.lineSeparator()
			+theTitleMid+System.lineSeparator()
			+theTitleText+System.lineSeparator();
		if (nmid) boxTitle += theTitleMid+System.lineSeparator();
		boxTitle += theTitleBot;
		return boxTitle;
	}

	/**
	 * Returns a title inside a single-line box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	static String boxTitle1(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"|", "|",
							"|", "|",
							"|", "_", "|", false);
	}

	/**
	 * Returns a title inside an equal-pipe box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	static String boxTitle2(int size, String title) {
		return makeBoxTitle(size, title,
							" ", "_", " ",

							"/", "\\",
							"|", "|",
							"\\", "_", "/" , false);
	}

	/**
	 * Returns a title inside an angle-bracket box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	static String boxTitle3(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\", "/",
							"<", ">",
							"/", "_", "\\", false);
	}

	/**
	 * Returns a title inside a plus-dash box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	static String boxTitle4(int size, String title) {
		return makeBoxTitle(size, title,
							"+", "-", "+",
							"|", "|",
							"|", "|",
							"+", "-", "+", true);
	}

	/**
	 * Returns a title inside a hash-equal box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	static String boxTitle5(int size, String title) {
		return makeBoxTitle(size, title,
							"#", "=", "#",
							"I", "I",
							"I", "I",
							"#", "=", "#", true);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxTitle6(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\",     "/",
							"[",      "]",
							"/", "_", "\\", false);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxTitle7(int size, String title) {
		return makeBoxTitle(size, title,
							"_", "_", "_",
							"\\",     "/",
							"(",      ")",
							"/", "_", "\\", false);
	}

	/**
	 * Prints a Title Box in the standar output
	 * @param boxId The box Id
	 * @param size The box size
	 * @param title The box title
	 */
	static void printBoxTitle(int boxId, int size, String title) {
		switch (boxId) {
		case 1: System.out.println(boxTitle1(size, title)); break;
		case 2: System.out.println(boxTitle2(size, title)); break;
		case 3: System.out.println(boxTitle3(size, title)); break;
		case 4: System.out.println(boxTitle4(size, title)); break;
		case 5: System.out.println(boxTitle5(size, title)); break;
		case 6: System.out.println(boxTitle6(size, title)); break;
		case 7: System.out.println(boxTitle7(size, title)); break;
		}
	}

	/**
	 * Generates a random BoxText from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	static String boxTextRandom(int size, String title) {
		switch (ThreadLocalRandom.current().nextInt(7)+1) {
			case 1: return boxText1(size, title);
			case 2: return boxText2(size, title);
			case 3: return boxText3(size, title);
			case 4: return boxText4(size, title);
			case 5: return boxText5(size, title);
			case 6: return boxText6(size, title);
			case 7: return boxText7(size, title);
		}
		// Unreachable in practice (nextInt(7)+1 is always in [1,7], matching all 7 cases above),
		// but was calling boxTitle1 (wrong family) instead of boxText1 -- copy-paste from boxTitleRandom's
		// analogous fallback.
		return boxText1(size, title);
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String makeBoxText(int size, String text, String csi, String top, String csd, String mdi, String mdd) {
		String theBoxTopBot;
		String theBoxText;
		// Math.max guards the case where 'size' is exactly text.length()+1: the mandatory overhead
		// below (mdi+mdd = 2 chars) needs boxSize-text.length() >= 2, or the padding math ends up
		// producing a text line 1 char longer than theBoxTopBot below (always exactly boxSize wide).
		int boxSize = text.length() < size ? Math.max(size, text.length()+2) : text.length()+4;

		theBoxTopBot = csi+repeat(top, boxSize-2)+csd;
		theBoxText = mdi+repeat(" ", (boxSize-text.length()-2)/2)+text;
		theBoxText += repeat(" ", boxSize-1-theBoxText.length())+mdd;

		return theBoxTopBot+System.lineSeparator()+
				theBoxText+System.lineSeparator()+
				theBoxTopBot;
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText1(int size, String text) {
		return makeBoxText(size, text,
							"+", "+", "+",
							"|",      "|");
	}

	/**
	 * Returns a text inside of a plus-minux-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText2(int size, String text) {
		return makeBoxText(size, text,
				"+", "-", "+",
				"|",      "|");
	}

	/**
	 * Returns a text inside of a X-*-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText3(int size, String text) {
		return makeBoxText(size, text,
				"*", "*", "*",
				"|",      "|");
	}

	/**
	 * Returns a text inside of a hash-equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText4(int size, String text) {
		return makeBoxText(size, text,
				"#", "=", "#",
				"|",      "|");
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText5(int size, String text) {
		return makeBoxText(size, text,
				":", ":", ":",
				":",      ":");
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText6(int size, String text) {
		return makeBoxText(size, text,
				" ", "-", " ",
				"(",      ")");
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	static String boxText7(int size, String text) {
		return makeBoxText(size, text,
				"·", "-", "·",
				"[",      "]");
	}

	/**
	 * Pints a box text in the standard output
	 * @param boxId The id ob the text box
	 * @param size The size of the text box
	 * @param text The text
	 */
	static void printBoxText(int boxId, int size, String text) {
		switch (boxId) {
		case 1: System.out.println(boxText1(size, text)); break;
		case 2: System.out.println(boxText2(size, text)); break;
		case 3: System.out.println(boxText3(size, text)); break;
		case 4: System.out.println(boxText4(size, text)); break;
		case 5: System.out.println(boxText5(size, text)); break;
		case 6: System.out.println(boxText6(size, text)); break;
		case 7: System.out.println(boxText7(size, text)); break;
		}
	}
}
