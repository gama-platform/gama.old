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
import java.io.*;
import java.net.URI;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Spatial;
import msi.gaml.types.*;
import com.kitfox.svg.*;
import com.vividsolutions.jts.awt.ShapeReader;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class GamaSVGFile. Only loads vector shapes right now (and none of the associated elements: textures, colors, fonts,
 * etc.)
 * 
 * @author drogoul
 * @since 30 déc. 2013
 * 
 */
@file(name = "svg",
	extensions = "svg",
	buffer_type = IType.LIST,
	buffer_content = IType.GEOMETRY,
	buffer_index = IType.INT)
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
		return GamaListFactory.EMPTY_LIST;
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		try {
			final BufferedReader in = new BufferedReader(new FileReader(getFile()));
			SVGUniverse svg = SVGCache.getSVGUniverse();
			URI uri = svg.loadSVG(in, path);
			SVGDiagram diagram = svg.getDiagram(uri);
			Shape shape = diagram.getRoot().getShape();
			Geometry geom = ShapeReader.read(shape, 1.0, GeometryUtils.FACTORY); // flatness = ??
			IShape gs = new GamaShape(geom);
			if ( size != null ) {
				gs = Spatial.Transformations.scaled_to(scope, gs, size);
			}
			setBuffer(GamaListFactory.createWithoutCasting(Types.GEOMETRY, gs));
		} catch (FileNotFoundException e) {
			throw GamaRuntimeException.create(e, scope);
			//e.printStackTrace();
		}
	}

	@Override
	protected void flushBuffer() throws GamaRuntimeException {}

}
