/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.util.regex.Pattern;

public class TweerRegularExpressions {
/*	public static final String NormalEyes = "[:=]";
	public static final String Wink = "[;]";
	public static final String NoseArea = "(|o|O|-)" ;
	public static final String HappyMouths = "[D\\)\\]]";
	public static final String SadMouths = "[\\(\\[]";
	public static final String Tongue = "[pP]";
	public static final String OtherMouths = "[doO/\\]";
	public static final Pattern HAPPY_REGEX =  Pattern.compile( "(\\^_\\^|" + NormalEyes + NoseArea + HappyMouths + ")");
	public static final Pattern SAD_REGEX = Pattern.compile(NormalEyes + NoseArea + SadMouths);
	public static final Pattern Wink_RE = Pattern.compile(Wink + NoseArea + HappyMouths);
	public static final Pattern Tongue_RE = Pattern.compile(NormalEyes + NoseArea + Tongue);
	public static final Pattern Other_RE = Pattern.compile( "("+NormalEyes+"|"+Wink+")"  + NoseArea + OtherMouths );
	public static final String Emoticon = (
		"("+NormalEyes+"|"+Wink+")" +
		NoseArea + 
		"("+Tongue+"|"+OtherMouths+"|"+SadMouths+"|"+HappyMouths+")"
	);
	public static final Pattern EMOTICON_REGEX = Pattern.compile(Emoticon);
*/
	
	private static final String SPACE_EXCEPTIONS = "\\n\\r";
	public static final String SPACE_CHAR_CLASS = "\\p{C}\\p{Z}&&[^"
			+ SPACE_EXCEPTIONS + "\\p{Cs}]";
	public static final String SPACE_REGEX = "[" + SPACE_CHAR_CLASS + "]";

	public static final String PUNCTUATION_CHAR_CLASS = "\\p{P}\\p{M}\\p{S}"
			+ SPACE_EXCEPTIONS;
	public static final String PUNCTUATION_REGEX = "[" + PUNCTUATION_CHAR_CLASS
			+ "]";
	private static final String EMOTICON_DELIMITER = SPACE_REGEX + "|"
			+ PUNCTUATION_REGEX;

	public static final Pattern SMILEY_REGEX_PATTERN = Pattern
			.compile(":[)DdpP]|:[ -]\\)|<3");
	public static final Pattern FROWNY_REGEX_PATTERN = Pattern
			.compile(":[(<]|:[ -]\\(");
	public static final Pattern EMOTICON_REGEX_PATTERN = Pattern
			.compile("(?<=^|" + EMOTICON_DELIMITER + ")("
					+ SMILEY_REGEX_PATTERN.pattern() + "|"
					+ FROWNY_REGEX_PATTERN.pattern() + ")+(?=$|"
					+ EMOTICON_DELIMITER + ")");
	public static final Pattern DQ = Pattern.compile("(\"|\')");
}
