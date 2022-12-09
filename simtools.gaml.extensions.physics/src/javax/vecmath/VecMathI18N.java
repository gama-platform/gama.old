/*******************************************************************************************************
 *
 * VecMathI18N.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * The Class VecMathI18N.
 */
class VecMathI18N {
    
    /**
     * Gets the string.
     *
     * @param key the key
     * @return the string
     */
    static String getString(String key) {
	String s;
	try {
	    s = ResourceBundle.getBundle("javax.vecmath.ExceptionStrings").getString(key);
	}
	catch (MissingResourceException e) {
	    System.err.println("VecMathI18N: Error looking up: " + key);
	    s = key;
	}
	return s;
    }
}
