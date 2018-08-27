/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGMLFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.gml.GMLReceiver;
import org.opengis.feature.Feature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file (
		name = "gml",
		extensions = { "gml" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GML, IConcept.FILE },
		doc = @doc ("Represents a Geography Markup Language (GMF) file as defined by the Open Geospatial Consortium"))
@SuppressWarnings ({ "unchecked" })
public class GamaGMLFile extends GamaGisFile {

	CoordinateReferenceSystem crs = null;
	IList<IShape> shapes = null;
	Envelope3D  env = null;
	
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaGMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	public GamaGMLFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	public GamaGMLFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	public GamaGMLFile(final IScope scope, final String pathName, final boolean with3D) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null, with3D);
	}

	public GamaGMLFile(final IScope scope, final String pathName, final Integer code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	public GamaGMLFile(final IScope scope, final String pathName, final String code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
		for (IShape shape : shapes) {
			getBuffer().add(shape);
		}
		shapes.clear();
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		return GamaListFactory.create();
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		// PROBLEM: how to get the crs of the file?
		return crs;
	}

	
	protected void readShapes(final IScope scope) {
		scope.getGui().getStatus(scope).beginSubStatus("Reading file" + getName(scope));
		final File file = getFile(scope);
		shapes = GamaListFactory.create(Types.GEOMETRY);
		int size = 0;
		try {
			
			 // saxExample start
			 InputSource input = new InputSource(new FileReader(file));
			 DefaultFeatureCollection collection = new DefaultFeatureCollection();
		        GMLReceiver receiver=new GMLReceiver(collection);
		        GMLFilterFeature filterFeature = new GMLFilterFeature(receiver);
		        
		        GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFeature);
		        GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);
		       
		        try {
		            // parse xml
		            XMLReader reader = XMLReaderFactory.createXMLReader();
		            reader.setContentHandler(filterDocument);
		            reader.parse(input);
		        } catch (Exception e) {
		            throw new RuntimeException(e);
		        }

		    crs =   collection.getSchema().getCoordinateReferenceSystem();
		    env = Envelope3D.of(collection.getBounds());
		 	size = collection.size();
			int index = 0;
			computeProjection(scope, env);
			
			SimpleFeatureIterator it = collection.features();
				while (it.hasNext()) {
					index++;
					if (index % 20 == 0)
						scope.getGui().getStatus(scope).setSubStatusCompletion(index / size);
					final Feature feature = it.next();
					
					Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
					if (g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */ ) {
						
						g = gis.transform(g);
						if (!with3D) {
							g.apply(ZERO_Z);
							g.geometryChanged();
						}
						shapes.add(new GamaGisGeometry(g, feature));
					} else if (g == null) {
						// See Issue 725
						GAMA.reportError(scope,
								GamaRuntimeException
										.warning("GamaShapeFile.fillBuffer; geometry could not be added  as it is "
												+ "nil: " + feature.getIdentifier(), scope),
								false);
					}
				}
			
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			scope.getGui().getStatus(scope).endSubStatus("Reading file " + getName(scope));
		}
		if (size > shapes.size()) {
			GAMA.reportError(scope, GamaRuntimeException.warning("Problem with file " + getFile(scope) + ": only "
					+ shapes.size() + " of the " + size + " geometries could be added", scope), false);
		}
		
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (env == null) {
			readShapes(scope);
		}
		return env;
	}

}
