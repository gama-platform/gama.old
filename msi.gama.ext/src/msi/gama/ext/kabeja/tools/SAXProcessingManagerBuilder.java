/*******************************************************************************************************
 *
 * SAXProcessingManagerBuilder.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import msi.gama.ext.kabeja.parser.Parser;
import msi.gama.ext.kabeja.parser.SAXParserBuilder;
import msi.gama.ext.kabeja.processing.PostProcessor;
import msi.gama.ext.kabeja.processing.PostProcessorConfig;
import msi.gama.ext.kabeja.processing.ProcessPipeline;
import msi.gama.ext.kabeja.processing.ProcessingManager;
import msi.gama.ext.kabeja.xml.AggregatorGenerator;
import msi.gama.ext.kabeja.xml.SAXFilter;
import msi.gama.ext.kabeja.xml.SAXGenerator;
import msi.gama.ext.kabeja.xml.SAXSerializer;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXProcessingManagerBuilder implements ContentHandler {

	/** The xmlns kabeja processing. */
	public static String XMLNS_KABEJA_PROCESSING = "http://kabeja.org/processing/1.0";

	/** The Constant ELEMENT_CONFIGURATION. */
	public static final String ELEMENT_CONFIGURATION = "configuration";

	/** The Constant ELEMENT_PARSER. */
	public static final String ELEMENT_PARSER = "parser";

	/** The Constant ELEMENT_PARSERS. */
	public static final String ELEMENT_PARSERS = "parsers";

	/** The Constant ELEMENT_SAXSERIALIZER. */
	public static final String ELEMENT_SAXSERIALIZER = "serializer";

	/** The Constant ELEMENT_SAXSERIALIZERS. */
	public static final String ELEMENT_SAXSERIALIZERS = "serializers";

	/** The Constant ELEMENT_SAXFILTER. */
	public static final String ELEMENT_SAXFILTER = "filter";

	/** The Constant ELEMENT_FILTER. */
	public static final String ELEMENT_FILTER = "filter";

	/** The Constant ELEMENT_SAXFILTERS. */
	public static final String ELEMENT_SAXFILTERS = "filters";

	/** The Constant ELEMENT_PIPELINE. */
	public static final String ELEMENT_PIPELINE = "pipeline";

	/** The Constant ELEMENT_PIPELINES. */
	public static final String ELEMENT_PIPELINES = "pipelines";

	/** The Constant ELEMENT_SERIALIZE. */
	public static final String ELEMENT_SERIALIZE = "serialize";

	/** The Constant ELEMENT_PROPERTY. */
	public static final String ELEMENT_PROPERTY = "property";

	/** The Constant ELEMENT_POSTPROCESSOR. */
	public static final String ELEMENT_POSTPROCESSOR = "postprocessor";

	/** The Constant ELEMENT_POSTPROCESS. */
	public static final String ELEMENT_POSTPROCESS = "postprocess";

	/** The Constant ELEMENT_AGGREGATE. */
	public static final String ELEMENT_AGGREGATE = "aggregate";

	/** The Constant ELEMENT_SAXGENERATOR. */
	public static final String ELEMENT_SAXGENERATOR = "generator";

	/** The Constant ELEMENT_GENERATE. */
	public static final String ELEMENT_GENERATE = "generate";

	/** The Constant ATTRIBUTE_NAME. */
	public static final String ATTRIBUTE_NAME = "name";

	/** The Constant ATTRIBUTE_CLASS. */
	public static final String ATTRIBUTE_CLASS = "class";

	/** The Constant ATTRIBUTE_VALUE. */
	public static final String ATTRIBUTE_VALUE = "value";

	/** The Constant ATTRIBUTE_DESCRIPTION. */
	public static final String ATTRIBUTE_DESCRIPTION = "description";

	/** The manager. */
	private ProcessingManager manager;

	/** The saxfilter. */
	private SAXFilter saxfilter;

	/** The saxserializer. */
	private SAXSerializer saxserializer;

	/** The postprocessor. */
	private PostProcessor postprocessor;

	/** The saxgenerator. */
	private SAXGenerator saxgenerator;

	/** The aggregator. */
	private AggregatorGenerator aggregator;

	/** The properties. */
	private Map<String, String> properties;

	/** The name. */
	// private StringBuffer buf = new StringBuffer();
	private String name;

	/** The pipeline. */
	private ProcessPipeline pipeline;

	/** The config. */
	private boolean config = false;

	/** The aggregate. */
	private boolean aggregate = false;

	/** The parser builder. */
	protected SAXParserBuilder parserBuilder;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
		if (namespaceURI.equals(XMLNS_KABEJA_PROCESSING)) {
			if (ELEMENT_SAXFILTER.equals(localName) && this.config) {
				this.saxfilter.setProperties(properties);
				this.manager.addSAXFilter(this.saxfilter, this.name);
			} else if (ELEMENT_SAXSERIALIZER.equals(localName)) {
				this.saxserializer.setProperties(this.properties);
				this.manager.addSAXSerializer(this.saxserializer, this.name);
			} else if (ELEMENT_PIPELINE.equals(localName)) {
				this.manager.addProcessPipeline(this.pipeline);
			} else if (ELEMENT_SERIALIZE.equals(localName)) {
				this.pipeline.setSAXSerializer(this.manager.getSAXSerializer(this.name));

				this.pipeline.setSAXSerializerProperties(this.properties);
			} else if (ELEMENT_FILTER.equals(localName)) {
				SAXFilterConfig config = new SAXFilterConfig(this.properties);
				config.setFilterName(this.name);
				this.pipeline.addSAXFilterConfig(config);
			} else if (ELEMENT_POSTPROCESS.equals(localName)) {
				PostProcessorConfig config = new PostProcessorConfig(this.properties);
				config.setPostProcessorName(this.name);
				this.pipeline.addPostProcessorConfig(config);
			} else if (ELEMENT_POSTPROCESSOR.equals(localName)) {
				this.postprocessor.setProperties(this.properties);
				this.manager.addPostProcessor(this.postprocessor, this.name);
			} else if (ELEMENT_CONFIGURATION.equals(localName)) {
				this.config = false;
			} else if (ELEMENT_GENERATE.equals(localName)) {
				if (this.aggregate) {
					this.aggregator.addSAXGenerator(this.manager.getSAXGenerator(this.name));
				} else {
					this.pipeline.setSAXGeneratorProperties(this.properties);
					this.pipeline.setSAXGenerator(this.manager.getSAXGenerator(this.name));
				}
			} else if (ELEMENT_SAXGENERATOR.equals(localName)) {
				this.saxgenerator.setProperties(this.properties);
				this.manager.addSAXGenerator(this.saxgenerator, this.name);
			}
		} else if (namespaceURI.equals(SAXParserBuilder.XMLNS_KABEJA_PARSER)) {
			this.parserBuilder.endElement(namespaceURI, localName, qName);

			if (ELEMENT_PARSER.equals(localName)) {
				// finish up the ParserBuilder and
				// add the parse to ProcessManager
				this.parserBuilder.endDocument();

				Parser p = this.parserBuilder.getParser();
				this.manager.addParser(p);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@Override
	public void endPrefixMapping(final String prefix) throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
	public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void processingInstruction(final String target, final String data) throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(final Locator locator) {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@Override
	public void skippedEntity(final String name) throws SAXException {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		manager = new ProcessingManager();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 * org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(final String namespaceURI, final String localName, final String qName,
			final Attributes atts) throws SAXException {
		if (namespaceURI.equals(XMLNS_KABEJA_PROCESSING)) {
			if (ELEMENT_SAXFILTER.equals(localName) && this.config) {
				this.properties = new HashMap<>();
				name = atts.getValue(ATTRIBUTE_NAME);
				saxfilter = (SAXFilter) createInstance(atts.getValue(ATTRIBUTE_CLASS));
			} else if (ELEMENT_SAXSERIALIZER.equals(localName)) {
				this.properties = new HashMap<>();
				name = atts.getValue(ATTRIBUTE_NAME);
				saxserializer = (SAXSerializer) createInstance(atts.getValue(ATTRIBUTE_CLASS));
			} else if (ELEMENT_POSTPROCESSOR.equals(localName)) {
				this.properties = new HashMap<>();
				this.name = atts.getValue(ATTRIBUTE_NAME);

				String clazz = atts.getValue(ATTRIBUTE_CLASS);
				postprocessor = (PostProcessor) createInstance(clazz);
			} else if (ELEMENT_PIPELINE.equals(localName)) {
				this.aggregate = false;
				this.pipeline = new ProcessPipeline();
				this.pipeline.setName(atts.getValue(ATTRIBUTE_NAME));
				String des = atts.getValue(ATTRIBUTE_DESCRIPTION);
				if (des != null) { this.pipeline.setDescription(des); }
			} else if (ELEMENT_SERIALIZE.equals(localName)) {
				this.properties = new HashMap<>();
				this.name = atts.getValue(ATTRIBUTE_NAME);
			} else if (ELEMENT_FILTER.equals(localName)) {
				this.properties = new HashMap<>();
				name = atts.getValue(ATTRIBUTE_NAME);
			} else if (ELEMENT_PROPERTY.equals(localName)) {
				this.properties.put(atts.getValue(ATTRIBUTE_NAME), atts.getValue(ATTRIBUTE_VALUE));
			} else if (ELEMENT_POSTPROCESS.equals(localName)) {
				this.properties = new HashMap<>();
				name = atts.getValue(ATTRIBUTE_NAME);
			} else if (ELEMENT_CONFIGURATION.equals(localName)) {
				this.config = true;
			} else if (ELEMENT_SAXGENERATOR.equals(localName)) {
				this.properties = new HashMap<>();
				this.name = atts.getValue(ATTRIBUTE_NAME);

				String clazz = atts.getValue(ATTRIBUTE_CLASS);
				this.saxgenerator = (SAXGenerator) createInstance(clazz);
			} else if (ELEMENT_GENERATE.equals(localName)) {
				this.properties = new HashMap<>();
				this.name = atts.getValue(ATTRIBUTE_NAME);
			} else if (ELEMENT_AGGREGATE.equals(localName)) {
				this.aggregate = true;
				this.aggregator = new AggregatorGenerator();
				this.pipeline.setSAXGenerator(this.aggregator);
			}
		} else if (namespaceURI.equals(SAXParserBuilder.XMLNS_KABEJA_PARSER)) {
			if (ELEMENT_PARSER.equals(localName)) {
				this.parserBuilder = new SAXParserBuilder();
				this.parserBuilder.startDocument();
			}

			this.parserBuilder.startElement(namespaceURI, localName, qName, atts);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@Override
	public void startPrefixMapping(final String prefix, final String uri) throws SAXException {}

	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	public ProcessingManager getManager() { return this.manager; }

	/**
	 * Creates the instance.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the object
	 */
	protected Object createInstance(final String clazz) {
		try {
			Class cl = this.getClass().getClassLoader().loadClass(clazz);
			return cl.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 *
	 * @param in
	 *            the InputStream
	 * @return The ProcessingManager build from the XML description
	 */
	public static ProcessingManager buildFromStream(final InputStream in) {
		SAXProcessingManagerBuilder builder = new SAXProcessingManagerBuilder();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			// factory.setXIncludeAware(true);
			try {
				factory.setFeature("http://apache.org/xml/features/xinclude", true);
			} catch (Exception e) {
				// OK older jaxp
				System.out.println("No XInclude support (use JAXP 1.4 or later for XInclude)");
			}

			try {
				XMLReader saxparser = factory.newSAXParser().getXMLReader();

				saxparser.setContentHandler(builder);
				saxparser.parse(new InputSource(in));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		} catch (SAXException | IOException ioe) {
			ioe.printStackTrace();
		}

		return builder.getManager();
	}
}
