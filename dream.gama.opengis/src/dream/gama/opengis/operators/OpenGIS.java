/*********************************************************************************************
 *
 *
 * 'OpenGIS.java', in plugin 'dream.gama.opengis', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package dream.gama.opengis.operators;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.List;
import javax.imageio.ImageIO;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.swing.wms.WMSLayerChooser;
import org.geotools.xml.XSISAXHandler;
import org.geotools.xml.gml.GMLComplexTypes;
import org.opengis.feature.simple.SimpleFeatureType;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.types.Types;

public class OpenGIS {

	@operator(value = "image_from_wms", can_be_const = true)
	@doc(value = "WMS: A simple call to WMS")
	public static IGamaFile read_wms(final IScope scope, final String fakeFile, final String slink, final Integer epsg,
		final Integer width, final Integer height, final Double bbox1, final Double bbox2, final Double bbox3,
		final Double bbox4) {
		if ( slink == null ) { return null; }

		if ( scope == null ) { return null; }
		BufferedImage image = null;

		URL url;
		try {
			url = new URL(slink);
			WebMapServer wms = new WebMapServer(url);
			// WARNING: Use of SWING objects is not a good idea (it introduces problems)
			List<org.geotools.data.ows.Layer> wmsLayers = WMSLayerChooser.showSelectLayer(wms);
			org.geotools.data.wms.request.GetMapRequest request = wms.createGetMapRequest();

			for ( org.geotools.data.ows.Layer wmsLayer : wmsLayers ) {
				request.addLayer(wmsLayer);
			}

			request.setVersion("1.1.0");
			request.setFormat("image/jpeg");
			request.setDimensions("" + width, "" + height);
			request.setTransparent(false);
			request.setSRS("EPSG:" + epsg);
			request.setBBox("" + bbox1 + "," + bbox2 + "," + bbox3 + "," + bbox4);

			GetMapResponse response = wms.issueRequest(request);
			InputStream is = response.getInputStream();
			image = ImageIO.read(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GamaImageFile gif = new GamaImageFile(scope, fakeFile);
		gif.setImage(scope, image);
		return gif;
	}

	@operator(value = "image_from_direct_wms", can_be_const = true)
	@doc(value = "WMS: A simple call to WMS")
	public static IGamaFile read_wms_direct(final IScope scope, final String fakeFile, final String slink) {
		if ( slink == null ) { return null; }

		if ( scope == null ) { return null; }
		BufferedImage image = null;

		URL url;
		try {
			url = new URL(slink);
			image = ImageIO.read(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GamaImageFile gif = new GamaImageFile(scope, fakeFile);
		gif.setImage(scope, image);
		return gif;
	}

	@operator(value = "gml_from_wfs", can_be_const = true)
	@doc(value = "WMS: A simple call to WFS/GML2")
	public static IList<IList<Object>> read_wfs(final IScope scope, final String fakeFile, final String schemaLink,
		final String gmlLink)
	// public static int read_wfs(final IScope scope, final String fakeFile,final String schemaLink, final String
	// gmlLink)
	{
		IList<IList<Object>> featureList = GamaListFactory.create(Types.LIST);

		if ( schemaLink == null || gmlLink == null ) { return null; }
		if ( scope == null ) { return null; }

		org.xml.sax.XMLReader reader = null;
		try {
			reader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e2) {
			e2.printStackTrace();
		}

		URI schemaLoc = null;
		try {
			schemaLoc = new java.net.URI(schemaLink);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		XSISAXHandler schemaHandler = new XSISAXHandler(schemaLoc);
		reader.setContentHandler(schemaHandler);

		try {
			reader.parse(new InputSource(new URL(schemaLoc.toString()).openConnection().getInputStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleFeatureType ft = null;
		try {
			ft = GMLComplexTypes.createFeatureType(schemaHandler.getSchema().getElements()[0]);
		} catch (SAXException e1) {
			e1.printStackTrace();
		}

		/*
		 * Object types[] = ft.getTypes().toArray();
		 *
		 * try{
		 * //create the parser with the gml 2.0 configuration
		 * org.geotools.xml.Configuration configuration = new org.geotools.gml2.GMLConfiguration();
		 * org.geotools.xml.Parser parser = new org.geotools.xml.Parser( configuration );
		 *
		 * URL gmlURL = new java.net.URL(gmlLink);
		 *
		 * InputStream stream = null;
		 * stream = gmlURL.openStream();
		 * org.geotools.GML gml = new org.geotools.GML(GML.Version.GML2);
		 * SimpleFeatureIterator iter = gml.decodeFeatureIterator(stream);
		 *
		 * while (iter.hasNext()) {
		 *
		 * GamaList<Object> row = new GamaList<Object>();
		 * final SimpleFeature feature = iter.next();
		 *
		 * for(int cnt = 0; cnt < ft.getTypes().size(); cnt++)
		 * {
		 * Object obj=feature.getAttribute(ft.getTypes().get(cnt).getName());
		 *
		 * if(obj instanceof MultiPolygon){
		 * Geometry g = ((MultiPolygon)obj).getGeometryN(0);
		 * row.add(new GamaGisGeometry(g, feature));
		 *
		 * // Geometry g = (MultiPolygon) obj;
		 * // g = scope.getSimulationScope().getProjectionFactory().getWorld().transform(g);
		 * // row.add(g);
		 * }else{
		 *
		 * row.add(obj);
		 * }
		 * }
		 * featureList.add(row);
		 * }
		 * }
		 * catch(Exception ex)
		 * {
		 * ex.printStackTrace();
		 * }
		 */
		return featureList;
	}

	@operator(value = "read_json_rest", can_be_const = true)
	@doc(value = "REST: Read data from RESTService")
	public static IList<IList<Object>> read_json_rest(final IScope scope, final String fakeFile,
		final String restLink) {
		IList<IList<Object>> featureList = GamaListFactory.create(Types.LIST);

		return featureList;
	}

}
