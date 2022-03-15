package core.metamodel.value.categoric.template;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * TODO: extends {@link Format} method to fit java interface requirement
 * 
 * @author kevinchapuis
 *
 */
public class GSCategoricTemplate extends Format {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean caseSensitive;
	private String[] regex;
	
	public GSCategoricTemplate(){
		this(true);
	}
	
	public GSCategoricTemplate(boolean caseSensitive, String... regex){
		this.caseSensitive = caseSensitive;
		this.regex = regex;
	}
	
	/**
	 * Change parameter String value to desired nominal space format. 
	 * For example if the string should be case sensitive or not, or reflect
	 * a date
	 * 
	 * @param value
	 * @return
	 */
	public String getFormatedString(String string){
		string = string.trim();
		String formatedString = caseSensitive ? string : string.toLowerCase();
		Arrays.asList(regex).stream().forEach(r -> formatedString
				.replaceAll(caseSensitive ? r : r.toLowerCase(), ""));
		return this.format(formatedString);
	}
	
	public void setRegex(String[] regex) { this.regex = regex; }
	
	public String[] getRegex() { return this.regex; }

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		return toAppendTo.append(obj.toString());
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return source;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(regex);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GSCategoricTemplate other = (GSCategoricTemplate) obj;
		if (caseSensitive != other.caseSensitive)
			return false;
		if (!Arrays.equals(regex, other.regex))
			return false;
		return true;
	}
	
}
