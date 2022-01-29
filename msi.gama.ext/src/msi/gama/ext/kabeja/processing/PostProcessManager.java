/*******************************************************************************************************
 *
 * PostProcessManager.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import msi.gama.ext.kabeja.dxf.DXFDocument;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class PostProcessManager {

	/** The processors. */
	private final ArrayList<PostProcessor> processors = new ArrayList<>();

	/**
	 * Adds the post processor.
	 *
	 * @param pp
	 *            the pp
	 */
	public void addPostProcessor(final PostProcessor pp) {
		processors.add(pp);
	}

	/**
	 * Adds the post processor.
	 *
	 * @param classname
	 *            the classname
	 */
	public void addPostProcessor(final String classname) {
		try {
			PostProcessor pp = (PostProcessor) this.getClass().getClassLoader().loadClass(classname).newInstance();
			addPostProcessor(pp);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Process.
	 *
	 * @param doc
	 *            the doc
	 * @param context
	 *            the context
	 * @throws ProcessorException
	 *             the processor exception
	 */
	public void process(final DXFDocument doc, final Map context) throws ProcessorException {
		Iterator i = processors.iterator();

		while (i.hasNext()) {
			PostProcessor pp = (PostProcessor) i.next();
			pp.process(doc, context);
		}
	}
}
