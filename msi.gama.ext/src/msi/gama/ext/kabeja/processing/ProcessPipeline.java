/*******************************************************************************************************
 *
 * ProcessPipeline.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.processing.helper.MergeMap;
import msi.gama.ext.kabeja.tools.SAXFilterConfig;
import msi.gama.ext.kabeja.xml.SAXFilter;
import msi.gama.ext.kabeja.xml.SAXGenerator;
import msi.gama.ext.kabeja.xml.SAXSerializer;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ProcessPipeline {

	/** The manager. */
	private ProcessingManager manager;

	/** The post processor configs. */
	private final List<PostProcessorConfig> postProcessorConfigs = new ArrayList<>();

	/** The sax filter configs. */
	private final List<SAXFilterConfig> saxFilterConfigs = new ArrayList<>();

	/** The generator. */
	private SAXGenerator generator;

	/** The serializer properties. */
	private Map serializerProperties = new HashMap();

	/** The generator properties. */
	private Map generatorProperties = new HashMap();

	/** The serializer. */
	private SAXSerializer serializer;

	/** The name. */
	private String name;

	/** The description. */
	private String description = "";

	/**
	 * Process.
	 *
	 * @param doc
	 *            the doc
	 * @param context
	 *            the context
	 * @param out
	 *            the out
	 * @throws ProcessorException
	 *             the processor exception
	 */
	public void process(final DXFDocument doc, final Map context, final OutputStream out) throws ProcessorException {
		ContentHandler handler = null;

		// postprocess
		Iterator i = this.postProcessorConfigs.iterator();

		while (i.hasNext()) {
			PostProcessorConfig ppc = (PostProcessorConfig) i.next();
			PostProcessor pp = this.manager.getPostProcessor(ppc.getPostProcessorName());

			// backup the default props
			Map oldProps = pp.getProperties();
			// setup the pipepine props
			pp.setProperties(new MergeMap(ppc.getProperties(), context));
			pp.process(doc, context);
			// restore the default props
			pp.setProperties(oldProps);
		}

		List<Map> saxFilterProperties = new ArrayList<>();

		// setup saxfilters
		if (this.saxFilterConfigs.size() > 0) {
			i = saxFilterConfigs.iterator();
			SAXFilterConfig sc = (SAXFilterConfig) i.next();
			SAXFilter first = this.manager.getSAXFilter(sc.getFilterName());
			saxFilterProperties.add(new MergeMap(first.getProperties(), context));

			first.setContentHandler(this.serializer);
			handler = first;
			first.setProperties(sc.getProperties());

			while (i.hasNext()) {
				sc = (SAXFilterConfig) i.next();
				SAXFilter f = this.manager.getSAXFilter(sc.getFilterName());
				f.setContentHandler(first);
				saxFilterProperties.add(f.getProperties());
				f.setProperties(sc.getProperties());
				first = f;

			}

		} else {
			// no filter
			handler = this.serializer;
		}

		Map oldProbs = this.serializer.getProperties();
		this.serializer.setProperties(new MergeMap(this.serializerProperties, context));

		// invoke the filter and serializer
		this.serializer.setOutput(out);

		try {
			Map oldGenProps = this.generator.getProperties();
			this.generator.setProperties(this.generatorProperties);
			this.generator.generate(doc, handler, context);
			// restore the old props
			this.generator.setProperties(oldGenProps);
		} catch (SAXException e) {
			throw new ProcessorException(e);
		}

		// restore the serializer properties
		this.serializer.setProperties(oldProbs);

		// restore the filter properties
		for (int x = 0; x < saxFilterProperties.size(); x++) {
			SAXFilterConfig sc = saxFilterConfigs.get(x);
			this.manager.getSAXFilter(sc.getFilterName()).setProperties(saxFilterProperties.get(x));
		}
	}

	/**
	 * @return Returns the serializer.
	 */
	public SAXSerializer getSAXSerializer() { return serializer; }

	/**
	 * @param serializer
	 *            The serializer to set.
	 */
	public void setSAXSerializer(final SAXSerializer serializer) { this.serializer = serializer; }

	/**
	 * @return Returns the manager.
	 */
	public ProcessingManager getProcessorManager() { return manager; }

	/**
	 * @param manager
	 *            The manager to set.
	 */
	public void setProcessorManager(final ProcessingManager manager) { this.manager = manager; }

	/**
	 * @return Returns the name.
	 */
	public String getName() { return name; }

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(final String name) { this.name = name; }

	/**
	 * Prepare.
	 */
	public void prepare() {}

	/**
	 * Gets the post processor configs.
	 *
	 * @return the post processor configs
	 */
	public List getPostProcessorConfigs() { return this.postProcessorConfigs; }

	/**
	 * Adds the SAX filter config.
	 *
	 * @param config
	 *            the config
	 */
	public void addSAXFilterConfig(final SAXFilterConfig config) {
		this.saxFilterConfigs.add(config);
	}

	/**
	 * Adds the post processor config.
	 *
	 * @param config
	 *            the config
	 */
	public void addPostProcessorConfig(final PostProcessorConfig config) {
		this.postProcessorConfigs.add(config);
	}

	/**
	 * @return Returns the serializerProperties.
	 */
	public Map getSerializerProperties() { return serializerProperties; }

	/**
	 * @param serializerProperties
	 *            The serializerProperties to set.
	 */
	public void setSAXSerializerProperties(final Map serializerProperties) {
		this.serializerProperties = serializerProperties;
	}

	/**
	 * Sets the SAX generator properties.
	 *
	 * @param generatorProperties
	 *            the new SAX generator properties
	 */
	public void setSAXGeneratorProperties(final Map generatorProperties) {
		this.generatorProperties = generatorProperties;
	}

	/**
	 * Gets the SAX generator properties.
	 *
	 * @param generatorProperties
	 *            the generator properties
	 * @return the SAX generator properties
	 */
	public Map getSAXGeneratorProperties(final Map generatorProperties) {
		return this.generatorProperties;
	}

	/**
	 * Sets the SAX generator.
	 *
	 * @param generator
	 *            the new SAX generator
	 */
	public void setSAXGenerator(final SAXGenerator generator) { this.generator = generator; }

	/**
	 * Gets the SAX generator.
	 *
	 * @return the SAX generator
	 */
	public SAXGenerator getSAXGenerator() { return this.generator; }

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() { return this.description; }

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the new description
	 */
	public void setDescription(final String description) { this.description = description; }
}
