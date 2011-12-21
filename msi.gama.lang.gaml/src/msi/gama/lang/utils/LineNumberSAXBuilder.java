/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils;

import java.io.*;
import msi.gama.lang.utils.*;
import org.jdom.*;
import org.jdom.input.*;
import org.xml.sax.*;

/**
 * This builder works in parallell with {@link LineNumberElement} to provide
 * each element with information on its beginning and ending line number in the
 * corresponding source. This only works for SAX parsers that supply that
 * information, and since this is optional, there are no guarantees.
 * <p>
 * Note that this builder always creates its own for each build, thereby cancelling any previous
 * call to setFactory.
 * <p>
 * All elements created are instances of {@link LineNumberElement}. No other construct currently
 * receive line number information.
 * 
 * @author Per Norrman
 */
public class LineNumberSAXBuilder extends SAXBuilder {

	/**
	 * Instantiates a new line number sax builder.
	 * @see org.jdom.input.SAXBuilder.SAXBuilder#SAXBuilder(boolean validate)
	 */
	public LineNumberSAXBuilder(final boolean validate) {
		super(validate);
	}

	/**
	 * Instantiates a new line number sax builder.
	 * @see org.jdom.input.SAXBuilder.SAXBuilder#SAXBuilder()
	 */
	public LineNumberSAXBuilder() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdom.input.SAXBuilder#createContentHandler()
	 */
	@Override
	protected SAXHandler createContentHandler() {
		return new MySAXHandler(new MyFactory());
	}

	/**
	 * A factory for creating My objects.
	 */
	private static class MyFactory extends DefaultJDOMFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jdom.DefaultJDOMFactory#element(java.lang.String)
		 */
		@Override
		public LineNumberElement element(final String name) {
			return new LineNumberElement(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jdom.DefaultJDOMFactory#element(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public LineNumberElement element(final String name, final String prefix, final String uri) {
			return new LineNumberElement(name, prefix, uri);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jdom.DefaultJDOMFactory#element(java.lang.String,
		 * org.jdom.Namespace)
		 */
		@Override
		public LineNumberElement element(final String name, final Namespace namespace) {
			return new LineNumberElement(name, namespace);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jdom.DefaultJDOMFactory#element(java.lang.String,
		 * java.lang.String)
		 */
		@Override
		public LineNumberElement element(final String name, final String uri) {
			return new LineNumberElement(name, uri);
		}

	}

	/**
	 * The Class MySAXHandler.
	 */
	private static class MySAXHandler extends SAXHandler {

		/**
		 * Instantiates a new my sax handler.
		 * 
		 * @param f
		 *            the f
		 */
		public MySAXHandler(final JDOMFactory f) {
			super(f);
		}

		/**
		 * override.
		 * 
		 * @param arg0
		 *            the arg0
		 * @param arg1
		 *            the arg1
		 * @param arg2
		 *            the arg2
		 * @param arg3
		 *            the arg3
		 * 
		 * @throws SAXException
		 *             the SAX exception
		 */
		@Override
		public void startElement(final String arg0, final String arg1, final String arg2,
			final Attributes arg3) throws SAXException {
			super.startElement(arg0, arg1, arg2, arg3);
			final Locator l = getDocumentLocator();
			if ( l != null ) {
				((LineNumberElement) getCurrentElement()).setStartLine(l.getLineNumber());
				((LineNumberElement) getCurrentElement()).setStartColumn(l.getColumnNumber());
			}
		}

		/**
		 * override.
		 * 
		 * @param arg0
		 *            the arg0
		 * @param arg1
		 *            the arg1
		 * @param arg2
		 *            the arg2
		 * 
		 * @throws SAXException
		 *             the SAX exception
		 */
		@Override
		public void endElement(final String arg0, final String arg1, final String arg2)
			throws SAXException {
			final Locator l = getDocumentLocator();
			if ( l != null ) {
				((LineNumberElement) getCurrentElement()).setEndLine(l.getLineNumber());
				((LineNumberElement) getCurrentElement()).setEndColumn(l.getColumnNumber());
			}

			super.endElement(arg0, arg1, arg2);
		}

	}

	@Override
	public Document build(final File file) throws JDOMException, IOException {
		final Document d = super.build(file);
		if ( d.getRootElement() != null ) {
			((LineNumberElement) d.getRootElement()).propagateFileName(file.getAbsolutePath());
		}
		return d;
	}

	public ISyntacticElement parse(final File file) throws Exception {
		return new XmlSyntacticElement((LineNumberElement) build(file).getRootElement());
	}

}
