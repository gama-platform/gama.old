/*******************************************************************************************************
 *
 * ProcessingManager.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.parser.ParseException;
import msi.gama.ext.kabeja.parser.Parser;
import msi.gama.ext.kabeja.processing.event.ProcessingListener;
import msi.gama.ext.kabeja.xml.SAXFilter;
import msi.gama.ext.kabeja.xml.SAXGenerator;
import msi.gama.ext.kabeja.xml.SAXSerializer;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessingManager {

	/** The saxfilters. */
	private final Map<String, SAXFilter> saxfilters = new HashMap<>();

	/** The saxserializers. */
	private final Map<String, SAXSerializer> saxserializers = new HashMap<>();

	/** The postprocessors. */
	private final Map<String, PostProcessor> postprocessors = new HashMap<>();

	/** The pipelines. */
	private final Map<String, ProcessPipeline> pipelines = new HashMap<>();

	/** The saxgenerators. */
	private final Map<String, SAXGenerator> saxgenerators = new HashMap<>();

	/** The parsers. */
	private final List<Parser> parsers = new ArrayList<>();

	/**
	 * Adds the SAX filter.
	 *
	 * @param filter
	 *            the filter
	 * @param name
	 *            the name
	 */
	public void addSAXFilter(final SAXFilter filter, final String name) {
		this.saxfilters.put(name, filter);
	}

	/**
	 * Gets the SAX filter.
	 *
	 * @param name
	 *            the name
	 * @return the SAX filter
	 */
	public SAXFilter getSAXFilter(final String name) {
		return this.saxfilters.get(name);
	}

	/**
	 * Gets the SAX filters.
	 *
	 * @return the SAX filters
	 */
	public Map getSAXFilters() { return this.saxfilters; }

	/**
	 * Adds the SAX serializer.
	 *
	 * @param serializer
	 *            the serializer
	 * @param name
	 *            the name
	 */
	public void addSAXSerializer(final SAXSerializer serializer, final String name) {
		this.saxserializers.put(name, serializer);
	}

	/**
	 * Gets the SAX serializer.
	 *
	 * @param name
	 *            the name
	 * @return the SAX serializer
	 */
	public SAXSerializer getSAXSerializer(final String name) {
		return this.saxserializers.get(name);
	}

	/**
	 * Gets the SAX serializers.
	 *
	 * @return the SAX serializers
	 */
	public Map getSAXSerializers() { return this.saxserializers; }

	/**
	 * Adds the post processor.
	 *
	 * @param pp
	 *            the pp
	 * @param name
	 *            the name
	 */
	public void addPostProcessor(final PostProcessor pp, final String name) {
		this.postprocessors.put(name, pp);
	}

	/**
	 * Adds the parser.
	 *
	 * @param parser
	 *            the parser
	 */
	public void addParser(final Parser parser) {
		this.parsers.add(parser);
	}

	/**
	 * Gets the parsers.
	 *
	 * @return the parsers
	 */
	public List getParsers() { return this.parsers; }

	/**
	 * Gets the parser.
	 *
	 * @param extension
	 *            the extension
	 * @return the parser
	 */
	protected Parser getParser(final String extension) {
		Iterator i = this.parsers.iterator();

		while (i.hasNext()) {
			Parser parser = (Parser) i.next();

			if (parser.supportedExtension(extension)) return parser;
		}

		return null;
	}

	/**
	 * Gets the post processor.
	 *
	 * @param name
	 *            the name
	 * @return the post processor
	 */
	public PostProcessor getPostProcessor(final String name) {
		return this.postprocessors.get(name);
	}

	/**
	 * Gets the post processors.
	 *
	 * @return the post processors
	 */
	public Map getPostProcessors() { return this.postprocessors; }

	/**
	 * Adds the process pipeline.
	 *
	 * @param pp
	 *            the pp
	 */
	public void addProcessPipeline(final ProcessPipeline pp) {
		pp.setProcessorManager(this);
		this.pipelines.put(pp.getName(), pp);
	}

	/**
	 * Gets the process pipeline.
	 *
	 * @param name
	 *            the name
	 * @return the process pipeline
	 */
	public ProcessPipeline getProcessPipeline(final String name) {
		return this.pipelines.get(name);
	}

	/**
	 * Gets the process pipelines.
	 *
	 * @return the process pipelines
	 */
	public Map getProcessPipelines() { return this.pipelines; }

	/**
	 * Process.
	 *
	 * @param stream
	 *            the stream
	 * @param extension
	 *            the extension
	 * @param context
	 *            the context
	 * @param pipeline
	 *            the pipeline
	 * @param out
	 *            the out
	 * @throws ProcessorException
	 *             the processor exception
	 */
	public void process(final InputStream stream, final String extension, final Map context, final String pipeline,
			final OutputStream out) throws ProcessorException {
		Parser parser = this.getParser(extension);

		if (parser != null) {
			try {
				parser.parse(stream, null);

				DXFDocument doc = parser.getDocument();
				this.process(doc, context, pipeline, out);
			} catch (ParseException e) {
				throw new ProcessorException(e);
			}
		}
	}

	/**
	 * Process.
	 *
	 * @param doc
	 *            the doc
	 * @param context
	 *            the context
	 * @param pipeline
	 *            the pipeline
	 * @param out
	 *            the out
	 * @throws ProcessorException
	 *             the processor exception
	 */
	public void process(final DXFDocument doc, final Map context, final String pipeline, final OutputStream out)
			throws ProcessorException {
		if (!this.pipelines.containsKey(pipeline))
			throw new ProcessorException("No pipeline found for name:" + pipeline);
		ProcessPipeline pp = this.pipelines.get(pipeline);
		pp.prepare();
		pp.process(doc, context, out);
	}

	/**
	 * Process.
	 *
	 * @param doc
	 *            the doc
	 * @param context
	 *            the context
	 * @param pipeline
	 *            the pipeline
	 * @param sourceFile
	 *            the source file
	 * @throws ProcessorException
	 *             the processor exception
	 */
	public void process(final DXFDocument doc, final Map context, final String pipeline, final String sourceFile)
			throws ProcessorException {
		if (!this.pipelines.containsKey(pipeline))
			throw new ProcessorException("No pipeline found for name:" + pipeline);
		try {
			ProcessPipeline pp = this.pipelines.get(pipeline);
			String suffix = pp.getSAXSerializer().getSuffix();
			String file = sourceFile.substring(0, sourceFile.lastIndexOf('.') + 1) + suffix;
			FileOutputStream out = new FileOutputStream(file);
			process(doc, context, pipeline, out);
		} catch (FileNotFoundException e) {
			throw new ProcessorException(e);
		}
	}

	/**
	 * Adds the SAX generator.
	 *
	 * @param saxgenerator
	 *            the saxgenerator
	 * @param name
	 *            the name
	 */
	public void addSAXGenerator(final SAXGenerator saxgenerator, final String name) {
		this.saxgenerators.put(name, saxgenerator);
	}

	/**
	 * Gets the SAX generator.
	 *
	 * @param name
	 *            the name
	 * @return the SAX generator
	 */
	public SAXGenerator getSAXGenerator(final String name) {
		return this.saxgenerators.get(name);
	}

	/**
	 * Gets the SAX generators.
	 *
	 * @return the SAX generators
	 */
	public Map getSAXGenerators() { return this.saxgenerators; }

	/**
	 * Adds the processing listener.
	 *
	 * @param l
	 *            the l
	 */
	public void addProcessingListener(final ProcessingListener l) {}

	/**
	 * Removes the processing listener.
	 *
	 * @param l
	 *            the l
	 */
	public void removeProcessingListener(final ProcessingListener l) {}
}
