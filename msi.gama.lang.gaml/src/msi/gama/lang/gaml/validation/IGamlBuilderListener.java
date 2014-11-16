/*********************************************************************************************
 * 
 * 
 * 'IGamlBuilderListener.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.validation;

import java.util.*;
import msi.gaml.descriptions.*;

/**
 * The class IGamlBuilder.
 * 
 * @author drogoul
 * @since 2 mars 2012
 * 
 */
public interface IGamlBuilderListener {

	public static interface IGamlBuilderListener2 extends IGamlBuilderListener {

		void validationEnded(final Collection<? extends IDescription> experiments, final ErrorCollector status);
	}

	void validationEnded(Set<String> experiments, ErrorCollector status);
}
