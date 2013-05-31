/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.io.IOException;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

public class GisUtils {

	static final boolean DEBUG = false; // Change DEBUG = false for release version

	Envelope translationEnvelope;
	private  static GeometryCoordinateSequenceTransformer transformer;
	private GeometryCoordinateSequenceTransformer inverseTransformer;
	private CoordinateReferenceSystem crsInit;

	public void init(Envelope bounds) {
		// TODO NECESSARY ?
		translationEnvelope = new ReferencedEnvelope(bounds, crsInit);
	}

	CoordinateFilter gisToAbsolute = new CoordinateFilter() {

		@Override
		public void filter(Coordinate coord) {
			if ( translationEnvelope == null ) { return; }
			coord.x -= translationEnvelope.getMinX();
			coord.y = -coord.y + translationEnvelope.getHeight() + translationEnvelope.getMinY();
		}
	};

	CoordinateFilter absoluteToGis = new CoordinateFilter() {

		@Override
		public void filter(Coordinate coord) {
			if ( translationEnvelope == null ) { return; }
			coord.x += translationEnvelope.getMinX();
			coord.y = -coord.y + translationEnvelope.getHeight() + translationEnvelope.getMinY();
		}

	};

	public void setTransformCRS(MathTransform t) {
		if ( t != null ) {
			transformer = new GeometryCoordinateSequenceTransformer();
			transformer.setMathTransform(t);
			try {
				inverseTransformer = new GeometryCoordinateSequenceTransformer();
				inverseTransformer.setMathTransform(t.inverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		} else {
			transformer = null;
			inverseTransformer = null;
		}
	}

	public Geometry transform(Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
		if ( transformer != null ) {
			try {
				geom = transformer.transform(g);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		geom.apply(gisToAbsolute);
		return geom;
	}

	public Envelope transform(Envelope g) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	public Geometry inverseTransform(Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
		geom.apply(absoluteToGis);
		if ( inverseTransformer != null ) {
			try {
				geom = inverseTransformer.transform(g);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return geom;
	}

	public boolean transforms() {
		return transformer != null;
	}

	public void setTransformCRS(final ShpFiles shpf, final double latitude, final double longitude) throws IOException {
		if ( transforms() ) { return; }
		PrjFileReader prjreader = new PrjFileReader(shpf);
		MathTransform transfCRS = null;
		try {
			crsInit = null;
			try {
				crsInit = CRS.parseWKT(prjreader.getCoodinateSystem().toWKT());
				// begin
				// ---------------------------------------------------------------------------------------------
				// Thai.truongminh@gmail.com
				// 18-sep-2012: for create agen from:list
				// for tracing only

				if ( DEBUG ) {
					GuiUtils.informConsole("GisUtil.CRS=" + crsInit.toString());
					// ---------------------------------------------------------------------------------------------
					// end
				}
			} catch (FactoryException e2) {
				e2.printStackTrace();
			}
			ProjectedCRS projectd = CRS.getProjectedCRS(crsInit);
			if ( projectd == null ) {
				System.out.println("NOT PROJECTED");
				try {
					int index = (int) (0.5 + (latitude + 180.0) / 360 * 60);
					boolean north = longitude > 0;
					int wgs84utm = 32600 + index + (north ? 0 : 100);
					CoordinateReferenceSystem crs = CRS.decode("EPSG:" + wgs84utm);
					transfCRS = CRS.findMathTransform(crsInit, crs);
					System.out.println("decodedcrs : " + crs);
				} catch (NoSuchAuthorityCodeException e) {
					System.out.println("WARNING : STILL NOT PROJECTED");
				} catch (FactoryException e) {
					System.out.println("WARNING : STILL NOT PROJECTED");
				}
			} else {
				System.out.println(" IT IS ALREADY PROJECTED" + projectd.toWKT());
			}
		} finally {
			prjreader.close();
			setTransformCRS(transfCRS);
		}
	}

	public void setTransformCRS(final CoordinateReferenceSystem crsI, final double latitude, final double longitude) {
		if ( transforms() ) { return; }
		MathTransform transfCRS = null;
		crsInit = crsI;
		ProjectedCRS projectd = CRS.getProjectedCRS(crsInit);
		if ( projectd == null ) {
			System.out.println("NOT PROJECTED");
			try {
				int index = (int) (0.5 + (latitude + 180.0) / 360 * 60);
				boolean north = longitude > 0;
				int wgs84utm = 32600 + index + (north ? 0 : 100);
				CoordinateReferenceSystem crs = CRS.decode("EPSG:" + wgs84utm);
				transfCRS = CRS.findMathTransform(crsInit, crs);
				System.out.println("decodedcrs : " + crs);
			} catch (NoSuchAuthorityCodeException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			} catch (FactoryException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			}
		} else {
			System.out.println(" IT IS ALREADY PROJECTED" + projectd.toWKT());
		}
		setTransformCRS(transfCRS);
	}

	// Begin
	// -----------------------------------------------------------------------------------
	public void setTransformCRS(final String coordinateRS, final double latitude, final double longitude) {
		if ( transforms() ) { return; }
		MathTransform transfCRS = null;
		crsInit = null;
		try {
			crsInit = CRS.parseWKT(coordinateRS);
			if ( DEBUG ) {
				GuiUtils.informConsole("GisUtil.getTransformCRS:" + crsInit.toString());
			}
		} catch (FactoryException e2) {
			e2.printStackTrace();
		}
		ProjectedCRS projectd = CRS.getProjectedCRS(crsInit);
		if ( projectd == null ) {
			System.out.println("NOT PROJECTED");
			try {
				int index = (int) (0.5 + (latitude + 180.0) / 360 * 60);
				boolean north = longitude > 0;
				int wgs84utm = 32600 + index + (north ? 0 : 100);
				CoordinateReferenceSystem crs = CRS.decode("EPSG:" + wgs84utm);
				transfCRS = CRS.findMathTransform(crsInit, crs);
				System.out.println("decodedcrs : " + crs);
			} catch (NoSuchAuthorityCodeException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			} catch (FactoryException e) {
				System.out.println("WARNING : STILL NOT PROJECTED");
			}
		} else {
			System.out.println(" IT IS ALREADY PROJECTED" + projectd.toWKT());
		}
		setTransformCRS(transfCRS);

	}

	public void setTransformCRS(final String srid, final boolean longitudeFirst, final double latitude,
		final double longitude) {
		if ( transforms() ) { return; }
		crsInit = null;
		try {
			crsInit = CRS.decode("EPSG:" + srid, longitudeFirst);
			setTransformCRS(crsInit.toWKT(), latitude, longitude);
			if ( DEBUG ) {
				GuiUtils.informConsole("GisUtil.setTransformCRS:" + crsInit.toString());
			}
		} catch (FactoryException e2) {
			e2.printStackTrace();
		}
	}

	public CoordinateReferenceSystem getCrs() {
		return crsInit;
	}

}
