/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.parser.xml;

import java.util.List;
import msi.gama.lang.gaml.gaml.Statement;
import org.jdom.*;

/**
 * This class extends a normal Element with a traceback to its beginning and
 * endling line number, if available and reported.
 * <p>
 * Each instance is created using a factory internal to the LineNumberSAXBuilder class.
 * 
 * @author Per Norrman
 */
public class LineNumberElement extends Element {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/** The _start line. */
	private int					_startLine			= 0;

	/** The _end line. */
	private int					_endLine			= 0;

	private String				_fileName			= "";

	private int					_startColumn;
	private int					_endColumn;

	// gaml
	private Statement			gamlStatement;

	public Statement getStatement() {
		return gamlStatement;
	}

	public void setStatement(final Statement statement) {
		gamlStatement = statement;
	}

	public LineNumberElement(final String name, final Statement statement) {
		super(name);
		gamlStatement = statement;
	}

	/**
	 * Instantiates a new line number element.
	 * 
	 * @param name
	 *            the name
	 */
	public LineNumberElement(final String name) {
		super(name);
	}

	/**
	 * Instantiates a new line number element.
	 * <p>
	 * This protected constructor is provided in order to support an Element subclass that wants
	 * full control over variable initialization. It intentionally leaves all instance variables
	 * null, allowing a lightweight subclass implementation. The subclass is responsible for
	 * ensuring all the get and set methods on Element behave as documented.
	 * </p>
	 * <p>
	 * When implementing an Element subclass which doesn't require full control over variable
	 * initialization, be aware that simply calling super() (or letting the compiler add the
	 * implicit super() call) will not initialize the instance variables which will cause many of
	 * the methods to throw a NullPointerException. Therefore, the constructor for these subclasses
	 * should call one of the public constructors so variable initialization is handled
	 * automatically.
	 * </p>
	 * @deprecated shouldnt use empty constructor
	 * @see org.jdom.Element#Element()
	 */
	// @Deprecated
	// public LineNumberElement() {
	// super("z");
	// }

	/**
	 * Instantiates a new line number element.
	 * 
	 * @param name
	 *            the name
	 * @param uri
	 *            the uri
	 */
	public LineNumberElement(final String name, final String uri) {
		super(name, uri);
	}

	/**
	 * Instantiates a new line number element.
	 * 
	 * @param name
	 *            the name
	 * @param prefix
	 *            the prefix
	 * @param uri
	 *            the uri
	 */
	public LineNumberElement(final String name, final String prefix, final String uri) {
		super(name, prefix, uri);
	}

	/**
	 * Instantiates a new line number element.
	 * 
	 * @param name
	 *            the name
	 * @param namespace
	 *            the namespace
	 */
	public LineNumberElement(final String name, final Namespace namespace) {
		super(name, namespace);
	}

	/**
	 * Gets the end line.
	 * 
	 * @return the end line
	 */
	public int getEndLine() {
		return _endLine;
	}

	/**
	 * Gets the start line.
	 * 
	 * @return the start line
	 */
	public int getStartLine() {
		return _startLine;
	}

	/**
	 * Sets the end line.
	 * 
	 * @param i
	 *            the new end line
	 */
	public void setEndLine(final int i) {
		_endLine = i;
	}

	/**
	 * Sets the start line.
	 * 
	 * @param i
	 *            the new start line
	 */
	public void setStartLine(final int i) {
		_startLine = i;
	}

	/**
	 * Gets the end column.
	 * 
	 * @return the end column
	 */
	public int getEndColumn() {
		return _endColumn;
	}

	/**
	 * Gets the start column.
	 * 
	 * @return the start column
	 */
	public int getStartColumn() {
		return _startColumn;
	}

	/**
	 * Sets the end column.
	 * 
	 * @param i
	 *            the new end column
	 */
	public void setEndColumn(final int i) {
		_endColumn = i;
	}

	/**
	 * Sets the start column.
	 * 
	 * @param i
	 *            the new start column
	 */
	public void setStartColumn(final int i) {
		_startColumn = i;
	}

	public final String getFileName() {
		return _fileName;
	}

	@Override
	public LineNumberElement getChild(final String name) {
		return (LineNumberElement) super.getChild(name);
	}

	@Override
	public LineNumberElement getParentElement() {
		return (LineNumberElement) super.getParentElement();
	}

	public final void setFileName(final String fileName) {
		_fileName = fileName;
	}

	/**
	 * @param baseURI
	 */
	public void propagateFileName(final String baseURI) {
		setFileName(baseURI);
		final List l = getChildren();
		for ( final Object e : l ) {
			final LineNumberElement lne = (LineNumberElement) e;
			lne.propagateFileName(baseURI);
		}

	}

}
