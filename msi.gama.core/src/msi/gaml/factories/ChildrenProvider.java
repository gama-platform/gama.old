/*********************************************************************************************
 * 
 *
 * 'ChildrenProvider.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.factories;

import java.util.*;
import msi.gaml.descriptions.IDescription;

public class ChildrenProvider {

	private final List<IDescription> children;
	public static final ChildrenProvider NONE = new ChildrenProvider(null);

	public ChildrenProvider(final List descs) {
		children = descs;
	}

	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

}
