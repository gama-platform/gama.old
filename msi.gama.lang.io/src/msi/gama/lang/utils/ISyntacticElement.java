/**
 * Created by drogoul, 18 nov. 2011
 * 
 */
package msi.gama.lang.utils;

import java.util.*;

/**
 * Written by drogoul
 * Modified on 18 nov. 2011
 * 
 * An interface to manipulate syntactic elements (either jdom elements or EObjects)
 * 
 */
public interface ISyntacticElement {

	String getName();

	String getAttribute(String name);

	Map<String, String> getAttributes();

	void setAttribute(String string, String string2);

	List<ISyntacticElement> getChildren();

	List<ISyntacticElement> getChildren(String name);

	ISyntacticElement getChild(String name);

	boolean hasParent(String name);

	/*
	 * Returns either a LineNumberElement or a Statement
	 */
	Object getUnderlyingElement();

	int getLineNumber();

	String getFilename();

}
