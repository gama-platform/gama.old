/*******************************************************************************************************
 *
 * DXFEntitiesSectionHandler.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser;

import java.util.Hashtable;
import java.util.Iterator;

import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.dxf.DXFEntity;
import msi.gama.ext.kabeja.parser.entities.DXFEntityHandler;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFEntitiesSectionHandler extends AbstractSectionHandler implements DXFSectionHandler, HandlerManager {
	
	/** The section key. */
	private static String SECTION_KEY = "ENTITIES";
	
	/** The Constant ENTITY_START. */
	public static final int ENTITY_START = 0;
	
	/** The handlers. */
	protected Hashtable<String, DXFEntityHandler> handlers = new Hashtable<>();
	
	/** The handler. */
	protected DXFEntityHandler handler = null;
	
	/** The parse entity. */
	protected boolean parseEntity = false;

	/**
	 * Instantiates a new DXF entities section handler.
	 */
	public DXFEntitiesSectionHandler() {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.SectionHandler#getSectionKey()
	 */
	@Override
	public String getSectionKey() { return SECTION_KEY; }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.SectionHandler#parseGroup(int, java.lang.String)
	 */
	@Override
	public void parseGroup(final int groupCode, final DXFValue value) {
		if (groupCode == ENTITY_START) {
			if (parseEntity) {
				if (handler.isFollowSequence()) {
					// there is a sequence like polyline
					handler.parseGroup(groupCode, value);

					return;
				}
				endEntity();
			}

			if (handlers.containsKey(value.getValue())) {
				// get handler for the new entity
				handler = handlers.get(value.getValue());
				handler.setDXFDocument(this.doc);
				handler.startDXFEntity();
				parseEntity = true;
			} else {
				// no handler found
				parseEntity = false;
			}
		} else if (parseEntity) { handler.parseGroup(groupCode, value); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.SectionHandler#setDXFDocument(org.dxf2svg.xml.DXFDocument)
	 */
	@Override
	public void setDXFDocument(final DXFDocument doc) { this.doc = doc; }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.SectionHandler#endParsing()
	 */
	@Override
	public void endSection() {
		endEntity();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.dxf2svg.parser.SectionHandler#startParsing()
	 */
	@Override
	public void startSection() {
		parseEntity = false;
	}

	/**
	 * End entity.
	 */
	protected void endEntity() {
		if (parseEntity) {
			handler.endDXFEntity();

			DXFEntity entity = handler.getDXFEntity();
			doc.addDXFEntity(entity);
		}
	}

	/**
	 * Adds the DXF entity handler.
	 *
	 * @param handler the handler
	 */
	public void addDXFEntityHandler(final DXFEntityHandler handler) {
		handler.setDXFDocument(doc);
		handlers.put(handler.getDXFEntityName(), handler);
	}

	@Override
	public void addHandler(final Handler handler) {
		addDXFEntityHandler((DXFEntityHandler) handler);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.parser.Handler#releaseDXFDocument()
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
}
