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

import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.IErrorCollector;

/**
 * The Class GamlException.
 */
public class GamlCompilationError {

	protected boolean isWarning = false;
	protected final String message;
	/**
	 * This element normally contains a reference to the initial statement in GAML
	 */
	private ISyntacticElement source = null;

	public GamlCompilationError(final String s) {
		message = s;
	}

	public Object getStatement() {
		if ( source == null ) { return null; }
		return source.getUnderlyingElement(facet);
	}

	public GamlCompilationError(final String string, final ISyntacticElement sourceInformation) {
		this(string);
		addSource(sourceInformation);
	}

	public GamlCompilationError(final String string, final ISyntacticElement sourceInformation,
		final boolean warning) {
		this(string);
		isWarning = warning;
		addSource(sourceInformation);
	}

	@Override
	public String toString() {
		return message;
	}

	public void addSource(final ISyntacticElement cur) {
		if ( source == null ) {
			source = cur;
			if ( cur == null ) { return; }
			IErrorCollector collect = cur.getErrorCollector();
			if ( collect != null ) {
				collect.add(this);
			}
		}
	}

	public boolean isWarning() {
		return isWarning;
	}

	private Object facet;

	/**
	 * @param key
	 */
	public void setObjectOfInterest(final Object key) {
		facet = key;
	}

	/**
	 * @param b
	 */
	public void setWarning(final boolean b) {
		isWarning = b;
	}

}
