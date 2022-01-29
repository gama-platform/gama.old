/*******************************************************************************************************
 *
 * DXFParser.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.parser.dxf.DXFHandler;
import msi.gama.ext.kabeja.parser.dxf.filter.DXFStreamFilter;
import msi.gama.ext.kabeja.tools.CodePageParser;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFParser implements HandlerManager, Handler, Parser, DXFHandler {

	/** The Constant PARSER_NAME. */
	public final static String PARSER_NAME = "DXFParser";

	/** The Constant EXTENSION. */
	public final static String EXTENSION = "dxf";

	/** The Constant SECTION_START. */
	private final static String SECTION_START = "SECTION";

	/** The Constant SECTION_END. */
	private final static String SECTION_END = "ENDSEC";

	/** The Constant END_STREAM. */
	private final static String END_STREAM = "EOF";

	/** The Constant COMMAND_CODE. */
	private final static int COMMAND_CODE = 0;

	/** The Constant DEFAULT_ENCODING. */
	public static final String DEFAULT_ENCODING = "";

	/** The doc. */
	protected DXFDocument doc;

	/** The handlers. */
	protected Hashtable<String, DXFSectionHandler> handlers = new Hashtable<>();

	/** The current handler. */
	protected DXFSectionHandler currentHandler;

	/** The line. */
	private String line;

	/** The stream filters. */
	protected List<DXFStreamFilter> streamFilters = new ArrayList<>();

	/** The filter. */
	protected DXFHandler filter;

	/** The key. */
	// some parse flags
	private boolean key = false;

	/** The sectionstarts. */
	private boolean sectionstarts = false;

	/** The linecount. */
	private int linecount;

	/** The parse. */
	private boolean parse = false;

	/**
	 * Instantiates a new DXF parser.
	 */
	public DXFParser() {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#parse(java.lang.String)
	 */
	@Override
	public void parse(final String file) throws ParseException {
		parse(file, DEFAULT_ENCODING);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#parse(java.lang.String, java.lang.String)
	 */
	@Override
	public void parse(final String file, final String encoding) throws ParseException {
		try {
			parse(new FileInputStream(file), encoding);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#parse(java.io.InputStream, java.lang.String)
	 */
	@Override
	public void parse(final InputStream input, String encoding) throws ParseException {
		String currentKey = "";
		key = false;
		linecount = 0;
		parse = false;

		// initialize
		doc = new DXFDocument();
		doc.setProperty(DXFDocument.PROPERTY_ENCODING, encoding);
		// the StreamFilters
		this.buildFilterChain();

		@SuppressWarnings ("resource") BufferedReader in = null;
		try {
			if ("".equals(encoding)) {
				BufferedInputStream buf = new BufferedInputStream(input);
				buf.mark(9000);

				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(buf));
					CodePageParser p = new CodePageParser();
					encoding = p.parseEncoding(r);
					buf.reset();
					in = new BufferedReader(new InputStreamReader(buf, encoding));
				} catch (IOException e1) {
					buf.reset();
					in = new BufferedReader(new InputStreamReader(buf));
				}
			} else {
				in = new BufferedReader(new InputStreamReader(input, encoding));
			}

			key = true;
			sectionstarts = false;

			while ((line = in.readLine()) != null) {
				linecount++;

				if (key) {
					currentKey = line;
					key = false;
				} else {
					int keyCode = Integer.parseInt(currentKey.trim());
					// the filter chain
					filter.parseGroup(keyCode, new DXFValue(line.trim()));
					// parseGroup(currentKey, line);
					key = true;
				}
			}

			in.close();

			in = null;

			// finish last parsing
			if (parse) { currentHandler.endSection(); }
		} catch (IOException ioe) {
			throw new ParseException(ioe.toString());
		}
	}

	@Override
	public void parseGroup(final int keyCode, final DXFValue value) throws ParseException {
		// System.out.println(""+keyCode);
		// System.out.println(" "+value.getValue());
		try {
			if (sectionstarts) {
				sectionstarts = false;

				if (handlers.containsKey(value.getValue())) {
					currentHandler = handlers.get(value.getValue());
					parse = true;
					currentHandler.setDXFDocument(doc);
					currentHandler.startSection();
				} else {
					parse = false;
				}

				return;
			}

			if (keyCode == COMMAND_CODE && SECTION_START.equals(value.getValue()) && !sectionstarts) {
				sectionstarts = true;
			}

			if (keyCode == COMMAND_CODE && SECTION_END.equals(value.getValue())) {
				if (parse) { currentHandler.endSection(); }

				parse = false;

				return;
			}

			if (parse) { currentHandler.parseGroup(keyCode, value); }
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ParseException("Line: " + linecount + " unsupported groupcode: " + key + " for value:" + value,
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#getDocument()
	 */
	@Override
	public DXFDocument getDocument() { return doc; }

	/**
	 * Adds the DXF section handler.
	 *
	 * @param handler
	 *            the handler
	 */
	public void addDXFSectionHandler(final DXFSectionHandler handler) {
		handler.setDXFDocument(doc);
		handlers.put(handler.getSectionKey(), handler);
	}

	@Override
	public void addHandler(final Handler handler) {
		addDXFSectionHandler((DXFSectionHandler) handler);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.Handler#releaseDXFDocument()
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#releaseDXFDocument()
	 */
	@Override
	public void releaseDXFDocument() {
		this.doc = null;

		Iterator i = handlers.values().iterator();

		while (i.hasNext()) {
			Handler handler = (Handler) i.next();
			handler.releaseDXFDocument();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.Handler#setDXFDocument(de.miethxml.kabeja.dxf.DXFDocument)
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.parser.Parser#setDXFDocument(org.kabeja.dxf.DXFDocument)
	 */
	@Override
	public void setDXFDocument(final DXFDocument doc) { this.doc = doc; }

	@Override
	public boolean supportedExtension(final String extension) {
		return EXTENSION.equals(extension.toLowerCase());
	}

	/**
	 * Adds the DXF stream filter.
	 *
	 * @param filter
	 *            the filter
	 */
	public void addDXFStreamFilter(final DXFStreamFilter filter) {
		this.streamFilters.add(filter);
	}

	/**
	 * Removes the DXF stream filter.
	 *
	 * @param filter
	 *            the filter
	 */
	public void removeDXFStreamFilter(final DXFStreamFilter filter) {
		this.streamFilters.remove(filter);
	}

	/**
	 * Builds the filter chain.
	 */
	protected void buildFilterChain() {
		// build the chain from end to start
		// the parser itself is the last element
		// in the chain
		DXFHandler handler = this;

		for (int i = this.streamFilters.size() - 1; i >= 0; i--) {
			DXFStreamFilter f = this.streamFilters.get(i);
			f.setDXFHandler(handler);
			handler = f;
		}

		// the first is used filter and if no filter
		// the parser itself is the filter
		this.filter = handler;
	}

	@Override
	public String getName() { return PARSER_NAME; }
}
