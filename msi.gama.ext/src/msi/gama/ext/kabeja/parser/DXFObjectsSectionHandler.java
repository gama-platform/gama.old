/*******************************************************************************************************
 *
 * DXFObjectsSectionHandler.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser;

import java.util.HashMap;

import msi.gama.ext.kabeja.parser.objects.DXFObjectHandler;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFObjectsSectionHandler extends AbstractSectionHandler implements HandlerManager {

	/** The section key. */
	private static String SECTION_KEY = "OBJECTS";

	/** The Constant OBJECT_START. */
	public static final int OBJECT_START = 0;

	/** The handlers. */
	private final HashMap<String, DXFObjectHandler> handlers = new HashMap<>();

	/** The handler. */
	private DXFObjectHandler handler;

	/** The parse object. */
	private boolean parseObject = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.DXFSectionHandler#endSection()
	 */
	@Override
	public void endSection() {
		this.endObject();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.DXFSectionHandler#getSectionKey()
	 */
	@Override
	public String getSectionKey() { return SECTION_KEY; }

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.DXFSectionHandler#parseGroup(int, de.miethxml.kabeja.parser.DXFValue)
	 */
	@Override
	public void parseGroup(final int groupCode, final DXFValue value) {
		if (groupCode == OBJECT_START) {
			this.endObject();

			if (this.handlers.containsKey(value.getValue())) {
				this.parseObject = true;
				this.handler = handlers.get(value.getValue());
				this.handler.setDXFDocument(this.doc);
				this.handler.startObject();
			} else {
				this.parseObject = false;
			}
		} else if (this.parseObject) { this.handler.parseGroup(groupCode, value); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.DXFSectionHandler#startSection()
	 */
	@Override
	public void startSection() {
		this.parseObject = false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.Handler#releaseDXFDocument()
	 */
	@Override
	public void releaseDXFDocument() {
		this.doc = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.HandlerManager#addHandler(de.miethxml.kabeja.parser.Handler)
	 */
	@Override
	public void addHandler(final Handler handler) {
		DXFObjectHandler h = (DXFObjectHandler) handler;
		h.setDXFDocument(this.doc);
		this.handlers.put(h.getObjectType(), h);
	}

	/**
	 * End object.
	 */
	protected void endObject() {
		if (this.parseObject) {
			// finish the old parsing object
			this.handler.endObject();
			this.doc.addDXFObject(handler.getDXFObject());
		}
	}
}
