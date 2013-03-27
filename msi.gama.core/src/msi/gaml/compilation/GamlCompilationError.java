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

import static msi.gama.common.interfaces.IGamlIssue.GENERAL;
import msi.gama.common.interfaces.ISyntacticElement;

/**
 * The Class GamlException.
 */
public class GamlCompilationError {

	protected boolean isWarning = false;
	protected final String message;
	protected String code;
	protected String[] data;
	// protected IDescription desc;
	/**
	 * This element normally contains a reference to the initial statement in GAML
	 */
	private ISyntacticElement source = null;

	public GamlCompilationError(final String s) {
		message = s;
		code = GENERAL;
	}

	public Object getStatement() {
		if ( source == null ) { return null; }
		Object o = source.getUnderlyingElement(facet);
		if ( o == null ) { return source; }
		return o;
	}

	public GamlCompilationError(final String string, final ISyntacticElement sourceInformation) {
		this(string);
		addSource(sourceInformation);
	}

	public GamlCompilationError(final String string, final ISyntacticElement sourceInformation,
		final boolean isWarning) {
		this(string);
		addSource(sourceInformation);
		setWarning(isWarning);
	}

	/**
	 * 
	 * @param string
	 * @param code, see IGamlIssue
	 * @param sourceInformation
	 * @param warning
	 * @param facet
	 * @param data
	 */
	public GamlCompilationError(final String string, final String code,
		final ISyntacticElement sourceInformation, final boolean warning, final Object facet,
		final String ... data) {
		this(string);
		isWarning = warning;
		this.facet = facet;
		this.code = code;
		this.data = data;
		addSource(sourceInformation);
	}

	public String[] getData() {
		return data;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return message;
	}

	public void addSource(final ISyntacticElement cur) {
		if ( source == null ) {
			source = cur;
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
