/*********************************************************************************************
 *
 *
 * 'GamaSVGFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.awt.Shape;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import com.vividsolutions.jts.awt.ShapeReader;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaSVGFile. Only loads vector shapes right now (and none of the
 * associated elements: textures, colors, fonts, etc.)
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
@file(name = "svg", extensions = "svg", buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
		IConcept.SVG })
public class GamaSVGFile extends GamaGeometryFile {

	GamaPoint size;

	public GamaSVGFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaSVGFile(final IScope scope, final String pathName, final GamaPoint size) throws GamaRuntimeException {
		super(scope, pathName);
		this.size = size;
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return getBuffer().get(0);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.create();
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		try {
			final BufferedReader in = new BufferedReader(new FileReader(getFile(scope)));
			final SVGUniverse svg = SVGCache.getSVGUniverse();
			final URI uri = svg.loadSVG(in, getPath(scope));
			final SVGDiagram diagram = svg.getDiagram(uri);
			final Shape shape = diagram.getRoot().getShape();
			final Geometry geom = ShapeReader.read(shape, 1.0, GeometryUtils.FACTORY); // flatness
																						// =
																						// ??
			// We center and scale the shape in the same operation
			final Envelope env = geom.getEnvelopeInternal();
			// GamaPoint translation = new GamaPoint(-env.getWidth() / 2,
			// -env.getHeight() / 2);
			final IShape gs = new GamaShape(null, geom, null, new GamaPoint(0, 0), size, true);
			// gs.setLocation(new GamaPoint(0, 0));
			// gs.setLocation(translation);
			// if ( size != null ) {
			// gs = Spatial.Transformations.scaled_to(scope, gs, size);
			// }
			setBuffer(GamaListFactory.createWithoutCasting(Types.GEOMETRY, gs));
		} catch (final FileNotFoundException e) {
			throw GamaRuntimeException.create(e, scope);
			// e.printStackTrace();
		}
	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {

	}

}
