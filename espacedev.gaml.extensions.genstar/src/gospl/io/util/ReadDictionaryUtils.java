package gospl.io.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import core.configuration.GenstarJsonUtil;
import core.configuration.dictionary.AttributeDictionary;
import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

/**
 * Provides tools to read dictionaries from various file formats 
 * 
 * @author Samuel Thiriot
 */
public class ReadDictionaryUtils {

	

	/**
	 * returns true if these modalities seem to be ranges
	 * @param modalities
	 * @return
	 */
	public static boolean detectIsRange(Collection<String> modalities) {
		
		if (modalities.size()<2)
			return false;
		
		 // we might have <anything not number><several numbers><anything but numbers>
		Pattern oneNumber = Pattern.compile("[\\D]*\\d+[\\D]*"); 
		
		List<String> mods = new ArrayList<>(modalities);
		
		boolean avoidfirstel = false;
		// if there is not one number in the first one, it's not a range
		if (oneNumber.matcher(mods.get(0)).matches()) {
			//System.out.println("no, because the first number is not a number: "+mods.get(0));
			avoidfirstel = true;
		}
		
		boolean avoidlastel = false;
		
		// there should also be only one number in the last one
		if (oneNumber.matcher(mods.get(mods.size()-1)).matches()) {
		//	System.out.println("no, because the last number is not a number: "+(mods.size()-1));
			avoidlastel = true;
		}
		
		// we might have <anything not number><several numbers><anything but numbers><several numbers><anything but numbers>
		Pattern twoNumbers = Pattern.compile("[\\D]*\\d+[\\D]+\\d+[\\D]*");

		// and then two numbers inbetween
		for (int i= (avoidfirstel ? 1 : 0); i<mods.size()-(avoidlastel ? 2 : 1); i++) {
			if (!twoNumbers.matcher(mods.get(i)).matches()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * return true if the modalities are numeric.
	 * The detections works as: it is numeric if it is made of only numbers
	 * or there is exactly one number inside it.
	 * @param modalities
	 * @return
	 */
	public static boolean detectIsInteger(Collection<String> modalities) {

		Pattern oneNumber = Pattern.compile("[\\D]*\\d+[\\D]*"); 

		for (String s: modalities) {
			if (!oneNumber.matcher(s).matches())
				return false;
			/*try {
				Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return false;
			}*/
		}
		return true;
	}
	

	
	private ReadDictionaryUtils() {}


	/**
	 * Reads a dictionnary in the Genstar JSON format.
	 * @param filename
	 * @return
	 */
	public static IGenstarDictionary<Attribute<? extends IValue>> readFromGenstarConfig(
			String filename) {
		
		GenstarJsonUtil sju = new GenstarJsonUtil();

		try {
			return sju.unmarshalFromGenstarJson(
					FileSystems.getDefault().getPath(filename), 
					AttributeDictionary.class);
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("error while reading the config file: "+e.getMessage(), e);
		}
		
	}

}
