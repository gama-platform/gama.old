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

import java.io.File;
import java.util.*;
import msi.gama.precompiler.ISyntacticElement;

/**
 * The Class GamlException.
 */
@SuppressWarnings("serial")
public class GamlException extends Exception {

	public static class ErrorContext {

		int lineNumber = 0;
		String fileName = "";
		String error = "error";

		public ErrorContext(final int n, final String f, final String e) {
			lineNumber = n;
			fileName = f;
			error = e;
		}

		public String editorName() {
			if ( fileName == null ) { return ""; }
			File f = new File(fileName);
			return f.getName();
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public String getFileName() {
			return fileName;
		}

		public String getError() {
			return error;
		}
	}

	public static ErrorContext errorLocation;

	protected final List<String> context = new ArrayList();

	private/** The node. */
	ISyntacticElement source = null;

	public Object getStatement() {
		if ( source != null ) { return source.getUnderlyingElement(); }
		return null;
	}

	/**
	 * Instantiates a new gaml exception.
	 * 
	 * @param message the message
	 */
	public GamlException(final String message) {
		super(message);
	}

	/**
	 * @param string
	 * @param sourceInformation
	 */
	public GamlException(final String string, final ISyntacticElement sourceInformation) {
		super(string);
		source = sourceInformation;
	}

	public GamlException(final String string, final Throwable ex) {
		super(string);
		addContext(ex.toString());
	}

	/**
	 * @param ex
	 */
	public GamlException(final Throwable ex) {
		super(ex);
	}

	/**
	 * Adds a context.
	 * 
	 * @param c the c
	 */
	public void addContext(final String c) {
		context.add(c);
		// context = context + "\r" + "(" + i++ + ") " + c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String mes = super.getMessage();
		if ( mes == null ) {
			mes = "";
		}
		return mes + getContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getSuperMessage() {
		return super.getMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return getMessage();
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public String getContext() {
		if ( errorLocation == null && source != null ) {
			String xmlContext = "";
			int n = source.getLineNumber();
			xmlContext += "line " + n;
			String fileName = source.getFilename();
			xmlContext += " of " + fileName;
			addContext(xmlContext);
			errorLocation = new ErrorContext(n, fileName, super.getMessage());
		}
		StringBuilder sb = new StringBuilder();
		for ( String s : context ) {
			sb.append(s).append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	public void addSource(final ISyntacticElement cur) {
		if ( source == null ) {
			source = cur;
		}
	}

	/**
	 * @return always false
	 */
	public boolean isWarning() {
		return false;
	}

}
