/*********************************************************************************************
 * 
 * 
 * 'GamaShapeFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul
 * Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
@file(name = "dxf",
	extensions = { "dxf" },
	buffer_type = IType.LIST,
	buffer_content = IType.GEOMETRY,
	buffer_index = IType.INT)
public class GamaDXFFile extends GamaGeometryFile {

		GamaPoint size;
		
		public GamaDXFFile(final IScope scope, final String pathName) throws GamaRuntimeException {
			super(scope, pathName);
		}

		public GamaDXFFile(final IScope scope, final String pathName, final GamaPoint size) throws GamaRuntimeException {
			super(scope, pathName);
			this.size = size;
		}

		@Override
		protected IShape buildGeometry(final IScope scope) {
			return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
		}

		@Override
		public IList<String> getAttributes(final IScope scope) {
			// TODO are there attributes ?
			return GamaListFactory.EMPTY_LIST;
		}
		
		
		
		
		public IShape createPolyline(IScope scope, IList pts) {
			if (pts.isEmpty()) return null;
	    	IShape shape =  GamaGeometryType.buildPolyline(pts);
			if (shape != null) {
				if ( size != null ) {
					return Spatial.Transformations.scaled_to(scope, shape, size);
				}
				return shape;
		    }
			return null;
		}
		
		public IShape manageObj(IScope scope, DXFLine obj) {
			if (obj == null) return null;
			IList list = GamaListFactory.create(Types.POINT);
			double x_t = obj.getDXFDocument().getBounds().getMinimumX();
			double y_t = obj.getDXFDocument().getBounds().getMinimumY();
			list.add(new GamaPoint(obj.getStartPoint().getX() - x_t,obj.getStartPoint().getY() - y_t,obj.getStartPoint().getZ()));
			list.add(new GamaPoint(obj.getEndPoint().getX() - x_t,obj.getEndPoint().getY() - y_t,obj.getEndPoint().getZ()));
			IShape line = createPolyline(scope,list);
			line.setAttribute("layer", obj.getLayerName());
			line.setAttribute("id", obj.getID());
			line.setAttribute("scale_factor", obj.getLinetypeScaleFactor());
			line.setAttribute("thickness", obj.getThickness());
			line.setAttribute("is_visibile", obj.isVisibile());
			line.setAttribute("is_omit", obj.isOmitLineType());
			return line;
		}
		public IShape manageObj(IScope scope, DXFPolyline obj) {
			if (obj == null) return null;
			double x_t = obj.getDXFDocument().getBounds().getMinimumX();
			double y_t = obj.getDXFDocument().getBounds().getMinimumY();
			
			IList list = GamaListFactory.create(Types.POINT);
			Iterator it = obj.getVertexIterator();
	    	while(it.hasNext()) {
	    		DXFVertex vertex = (DXFVertex)it.next();
	    		list.add(new GamaPoint(vertex.getX() - x_t, vertex.getY() - y_t, vertex.getZ()));
	    	}
	    	IShape shape = createPolyline(scope,list);
	    	shape.setAttribute("layer", obj.getLayerName());
	    	shape.setAttribute("id", obj.getID());
	    	shape.setAttribute("scale_factor", obj.getLinetypeScaleFactor());
	    	shape.setAttribute("thickness", obj.getThickness());
	    	shape.setAttribute("is_visibile", obj.isVisibile());
	    	shape.setAttribute("is_omit", obj.isOmitLineType());
	    	return shape;
		}

		public IList<IShape> defineGeoms(IScope scope, List objs){
			if (objs != null) {
				IList<IShape> geoms = GamaListFactory.create();
				for (Object obj : objs) {
					if (obj instanceof DXFLine) {IShape g = manageObj(scope, (DXFLine) obj); if (g != null) geoms.add(g);}
					if (obj instanceof DXFPolyline) {IShape g = manageObj(scope, (DXFPolyline) obj); if (g != null) geoms.add(g);}
				}
				return geoms;
			}
			return GamaListFactory.EMPTY_LIST;
		}
		protected void fillBuffer(IScope scope, DXFDocument doc ) {
			IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
			
			Iterator it =    doc.getDXFLayerIterator();
			while (it.hasNext()) {
		    	DXFLayer layer = (DXFLayer) it.next();
		    	geoms.addAll(defineGeoms(scope,layer.getDXFEntities(DXFConstants.ENTITY_TYPE_3DFACE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_3DSOLID)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_ARC)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_BODY)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_ELLIPSE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_HATCH)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_IMAGE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_INSERT)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LEADER)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_MLINE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_POINT)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_POLYLINE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_RAY)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_SHAPE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_SOLID)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_SPLINE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_TRACE)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_VERTEX)));
		    	geoms.addAll(defineGeoms(scope, layer.getDXFEntities(DXFConstants.ENTITY_TYPE_XLINE)));
		    }
		    	
		      
		 setBuffer(geoms);
		}
		@Override
		protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
			if (getBuffer() != null)  return ;
			Parser parser = ParserBuilder.createDefaultParser();
			try {
				   final InputStream in = new FileInputStream(getFile());
				   parser.parse(in, DXFParser.DEFAULT_ENCODING);
						
			       //get the documnet and the layer
			       DXFDocument doc = parser.getDocument();
			       fillBuffer(scope, doc);
			 } catch (Exception e) {
		    	  e.printStackTrace();
		      } 
		}

		@Override
		protected void flushBuffer() throws GamaRuntimeException {}

	
		@Override
		public Envelope computeEnvelope(final IScope scope) {
			Parser parser = ParserBuilder.createDefaultParser();
			   try {
				   final InputStream in = new FileInputStream(getFile());
					
			       //parse
			       parser.parse(in, DXFParser.DEFAULT_ENCODING);
						
			       //get the documnet and the layer
			       DXFDocument doc = parser.getDocument();
			       Envelope env = new Envelope(0, doc.getBounds().getMaximumX() - doc.getBounds().getMinimumX(), 0, doc.getBounds().getMaximumY() - doc.getBounds().getMinimumY());
			      
			     return env;
			   } catch (Exception e) {
				   
			    	  e.printStackTrace();
			    	  return null;
			      }
		}
}
