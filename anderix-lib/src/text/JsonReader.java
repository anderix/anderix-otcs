/**
 * Copyright (c) 2019  Anderix
 * All Rights Reserved
 */
package anderix.text;

import java.lang.Character;
import java.util.HashMap;

public class JsonReader {
	private String json;
	protected HashMap<String, String> values = new HashMap<String, String>();
	
	public JsonReader(String json) {
		this.json = json;
		parseJson();
	}
	
	public String get(String path) {
		return (String)this.values.get(path);
	}

	private boolean isNull(String str) {
		if ( str == null ) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getInt(String path) {
		int rval = 0;
		if ( !isNull(this.values.get(path)) ) {
			rval = Integer.parseInt(this.values.get(path));
		}
		return rval;
	}

	public long getLong(String path) {
		long rval = 0;
		if ( !isNull(this.values.get(path)) ) {
			rval = Long.parseLong(this.values.get(path));
		}
		return rval;
	}
	
	public boolean getBoolean(String path) {
		boolean rval = false;
		if ( !isNull(this.values.get(path)) ) { 
			if ( "true".equalsIgnoreCase(this.values.get(path)) ) rval = true;
		}
		return rval;
	}

	public float getFloat(String path) {
		float rval = 0;
		if ( !isNull(this.values.get(path)) ) {
			rval = Float.parseFloat(this.values.get(path));
		}
		return rval;
	}
	
	public double getDouble(String path) {
		double rval = 0;
		if ( !isNull(this.values.get(path)) ) {
			rval = Double.parseDouble(this.values.get(path));
		}
		return rval;
	}
	
	protected void parseJson() {
		parseJson(this.json);
	}
	
	protected void parseJson(String json) {
		//For now I will assume valid Json
		//Json starts with {, so the whole thing is an object
		if ( nextChar(json, 0) == '{' ) {
			parseObject(json, "");
		}
	}

	protected void parseObject(String obj, String path) {
		obj = obj.trim(); // now { will be in the 0th position, and } shoul be in the last position
		int endPostion = positionOfMatchingCurlyBrace(obj, 0); // but let's make sure we know the end position
		String name = "";
		String value = "";
		int colon = obj.indexOf(':'); //find the first :
		int nextStart = 1; // right after the {
		int valueStart = -1;
		int valueEnd = -1;
		char firstCharOfValue;
		int firstCharIndex;
		
		do { //There will be at least one name-value pair. 
			name = path + removeQuotes(obj.substring(nextStart, colon).trim());//The name is in between nextStart and the colon. Trim it and, and if it's quoted, remove the quotes
			// now find the value
			// we know it starts at colon, but where does it end? We need to look at the first char after the :
			firstCharOfValue = nextChar(obj, colon+1); //start looking right after the :
			firstCharIndex = obj.indexOf(firstCharOfValue, colon+1);
			// need to test for empty object, i.e. {}
			//if ( firstCharIndex == endPostion ) return; // this means the object is empty
	
			if ( firstCharOfValue == '"' ) { // it's a quoted string
				valueStart = firstCharIndex+1;
				valueEnd = obj.indexOf('"', firstCharIndex+1);
				value = obj.substring(valueStart, valueEnd); // the value is the string between the quotes
				valueEnd++; // add one to stay consistent with object and array
				value = removeJsonEscapeCharacters(value);
//System.out.println("PUT " + name + "=" + value);
				this.values.put(name, value);
			} else if ( firstCharOfValue == '{' ) { // it's another object
				//first, store the value in the current key as a json object
				valueStart = firstCharIndex;
				valueEnd = positionOfMatchingCurlyBrace(obj, firstCharIndex)+1;
				value = obj.substring(valueStart, valueEnd); // the value is the string between the curly braces
//System.out.println("PUT " + name + "=" + value);
				this.values.put(name, value);
				
				//then, recurse through the object
				parseObject(value, name + ".");
			
			} else if ( firstCharOfValue == '[' ) { // it's an array
				//first, store the value in the current key as an array
				valueStart = firstCharIndex;
				valueEnd = positionOfMatchingBracket(obj, firstCharIndex)+1;
				value = obj.substring(valueStart, valueEnd); // the value is the string between the brackets
//System.out.println("PUT " + name + "=" + value);
				this.values.put(name, value);

				//what is the first character of the array?
				char firstCharOfArray = nextChar(obj, firstCharIndex+1);
				int firstCharOfArrayIndex = obj.indexOf(firstCharOfArray, firstCharIndex+1);
				
				if ( firstCharOfArray == '{' ) { // an array might contain a list of comma-separated object -- look for {
					int counter = 0;
					int firstCharOfObjectIndex = firstCharOfArrayIndex;
					int lastCharOfObjectIndex = -1;
					String arrayValue = "";
					do {
						lastCharOfObjectIndex = positionOfMatchingCurlyBrace(obj, firstCharOfObjectIndex);
						arrayValue = obj.substring(firstCharOfObjectIndex, lastCharOfObjectIndex+1);
//System.out.println("PUT " + name+ "[" + Integer.toString(counter) + "]=" + arrayValue);
						this.values.put(name+ "[" + Integer.toString(counter) + "]", arrayValue);
						parseObject(arrayValue, name+ "[" + Integer.toString(counter) + "].");
						firstCharOfObjectIndex = obj.indexOf('{', lastCharOfObjectIndex); // look for the next object. If no more this will be -1.
						counter++;
					} while ( firstCharOfObjectIndex != -1 && firstCharOfObjectIndex < valueEnd );
//System.out.println("PUT " + name+ ".length=" + Integer.toString(counter));
					this.values.put(name + ".length", Integer.toString(counter));

				} else { // OR else an array contains a list of comma-separated values
					String[] arrayValues = (removeBrackets(value)).split(",");
					// Check for empty array
					if ( arrayValues.length == 1 && "".equals(arrayValues[0].trim()) ) {
						this.values.put(name + ".length", "0");
					} else {
//System.out.println("PUT " + name+ ".length=" + Integer.toString(arrayValues.length));
						this.values.put(name + ".length", Integer.toString(arrayValues.length));
						for ( int i = 0; i < arrayValues.length; i++ ) {
//System.out.println("PUT " + name+ "[" + Integer.toString(i) + "]=" + removeQuotes(arrayValues[i].trim()));
							this.values.put( name + "[" + Integer.toString(i) + "]", removeQuotes( arrayValues[i].trim() ) );
						
						}
					}

				}
			
			} else { // if it's not one of the above, then it's a boolean, number, unquoted string, etc. We'll treat them all as strings
				// look for comma or }. I don't think I need to look for ] or anything else...
				valueStart = firstCharIndex;
				int nextCommaIndex = obj.indexOf(',', firstCharIndex);
				int nextCurlyBraceIndex = obj.indexOf('}', firstCharIndex);
				if ( nextCommaIndex != -1 && nextCommaIndex < nextCurlyBraceIndex ) {
					valueEnd = nextCommaIndex;
				} else {
					valueEnd = nextCurlyBraceIndex;
				}
				value = obj.substring(valueStart, valueEnd);
				value = removeJsonEscapeCharacters(value);
//System.out.println("PUT " + name + "=" + value);
				this.values.put(name, value);
			}
			
			char c = nextChar(obj, valueEnd);
			nextStart = obj.indexOf(c, valueEnd);
			if ( c == ',' ) nextStart++;
			colon = obj.indexOf(':', nextStart);
		} while ( colon != -1 );
	}
		
	private String removeJsonEscapeCharacters(String str) {
		String rval = str;
		rval = str.replaceAll("\\\\/", "/");
		return rval;
	}
	
	protected char nextChar(String str, int startIndex) {
		char rval;
		do {
			rval = str.charAt(startIndex++);
		} while ( Character.isWhitespace(rval) );
		return rval;
	}

	private String removeBrackets(String value) {
		if ( value.length() > 0 ) {
			if ( value.charAt(0) == '[' ) value = value.substring(1);
			if ( value.charAt(value.length()-1) == ']' ) value = value.substring(0, value.length()-1);
		}
		return value;
	}
	
	private String removeCurlyBraces(String value) {
		if ( value.length() > 0 ) {
			if ( value.charAt(0) == '{' ) value = value.substring(1);
			if ( value.charAt(value.length()-1) == '}' ) value = value.substring(0, value.length()-1);
		}
		return value;
	}
	
	private String removeQuotes(String value) {
		if ( value.length() > 0 ) {
			if ( value.charAt(0) == '"' ) value = value.substring(1);
			if ( value.charAt(value.length()-1) == '"' ) value = value.substring(0, value.length()-1);
		}
		return value;
	}
	
	
	private int positionOfMatchingCurlyBrace(String str, int indexOfCurlyBraceToMatch) {
		int rval = 0;
		int braceCount = 0;
		
		for ( int i = indexOfCurlyBraceToMatch; i < str.length(); i++ ) {
			if ( str.charAt(i) == '}' ) {
				if ( --braceCount == 0 ) {
					rval = i;
					break;
				}
			} else if ( str.charAt(i) == '{' ) {
				braceCount++;
			}
		}
		return rval;
	}

	private int positionOfMatchingBracket(String str, int indexOfBracketToMatch) {
		int rval = 0;
		int bracketCount = 0;
		for ( int i = indexOfBracketToMatch; i < str.length(); i++ ) {
			if ( str.charAt(i) == ']' ) {
				if ( --bracketCount == 0 ) {
					rval = i;
					break;
				}
			} else if ( str.charAt(i) == '[' ) {
				bracketCount++;
			}
		}
		return rval;
	}	
}