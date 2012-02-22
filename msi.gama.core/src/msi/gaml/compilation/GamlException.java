/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;

/**
 * The Class GamlException.
 */
@SuppressWarnings("serial")
public class GamlException extends Exception {

	protected final List<String> context = new ArrayList();
	protected boolean isWarning = false;
	/**
	 * This element normally contains a reference to the initial statement in GAML
	 */
	private ISyntacticElement source = null;

	/**
	 * Instantiates a new gaml exception.
	 * 
	 * @param message the message
	 */
	protected GamlException(final String message) {
		super(message);
	}

	public Object getStatement() {
		if ( source == null ) { return null; }
		return source.getUnderlyingElement(facet);
	}

	/**
	 * @param string
	 * @param sourceInformation
	 */
	public GamlException(final String string, final ISyntacticElement sourceInformation) {
		this(string);
		addSource(sourceInformation);
	}

	public GamlException(final String string, final ISyntacticElement sourceInformation,
		final boolean warning) {
		this(string);
		addSource(sourceInformation);
		isWarning = warning;
	}

	public GamlException(final String string, final Throwable ex) {
		this(string);
		if ( ex != null ) {
			addContext(ex.toString());
		}
	}

	/**
	 * Adds a context.
	 * 
	 * @param c the c
	 */
	public void addContext(final String c) {
		context.add(c);
	}

	@Override
	public String toString() {
		return getMessage();
	}

	public void addSource(final ISyntacticElement cur) {
		if ( source == null ) {
			source = cur;
			ErrorCollector collect = cur.getErrorCollector();
			if ( collect != null ) {
				collect.add(this);
			}
		}
	}

	/**
	 * @return always false
	 */
	public boolean isWarning() {
		return isWarning;
	}

	/**
	 * @return
	 */
	public long getCycle() {
		return 0;
	}

	public String getContextAsLine() {
		StringBuilder sb = new StringBuilder();
		for ( String s : context ) {
			sb.append(s).append(" / ");
		}
		return sb.toString();
	}

	public List<String> getContextAsList() {
		return context;
	}

	private String facet;

	/**
	 * @param key
	 */
	public void setFacetOfInterest(final String key) {
		facet = key;
	}

	/**
	 * @param b
	 */
	public void setWarning(final boolean b) {
		isWarning = true;
	}

}
