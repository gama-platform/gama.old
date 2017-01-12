/*********************************************************************************************
 *
 * 'GamaDXFFile.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package miat.gama.extension.ifcfile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;

import ifc2x3javatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBoundingBox;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCartesianPoint;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCurve;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDirection;
import ifc2x3javatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import ifc2x3javatoolbox.ifc2x3tc1.IfcFace;
import ifc2x3javatoolbox.ifc2x3tc1.IfcFaceOuterBound;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLengthMeasure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLocalPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPolyLoop;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPolyline;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelReferencedInSpatialStructure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentationItem;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSlab;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWallStandardCase;
import ifc4javatoolbox.ifcmodel.IfcModel;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Spatial;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "ifc", extensions = {
		"ifc" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
				"ifc", IConcept.FILE })
public class GamaIFCFile extends GamaGeometryFile {

	@operator(value = "elelel")
	public static String lalalala(final GamaColor c1, final GamaColor c2) {
		return "lelelel";
	}
	public GamaIFCFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}



	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.create();
	}

	public GamaPoint toPoint(IfcDirection direction) {
		if (direction != null) {
			if (direction.getDirectionRatios().size() > 2)
				return new GamaPoint(direction.getDirectionRatios().get(0).value,direction.getDirectionRatios().get(1).value, direction.getDirectionRatios().get(2).value);
			return new GamaPoint(direction.getDirectionRatios().get(0).value,direction.getDirectionRatios().get(1).value);
		}
		return null;
	}
	
	public GamaPoint toPoint(IfcCartesianPoint point) {
		if (point != null) {
			if (point.getCoordinates().size() > 2)
				return new GamaPoint(point.getCoordinates().get(0).value,point.getCoordinates().get(1).value,point.getCoordinates().get(2).value);
			return new GamaPoint(point.getCoordinates().get(0).value,point.getCoordinates().get(1).value);
		}
		return null;
	}

	public List<GamaPoint> updateLocAxisDir(IfcAxis2Placement3D position, GamaPoint loc , GamaPoint axis,GamaPoint direction) {
		List<GamaPoint> points = new ArrayList<GamaPoint>();
		if (position == null) {points.add(loc);points.add(axis);points.add(direction);return points;}
		GamaPoint locS = toPoint(position.getLocation());
		if (locS != null) {
			if (loc == null) {loc = new GamaPoint(locS.toCoordinate());}
			else {loc.x += locS.x;loc.y += locS.y;loc.z += locS.z;}}
		
		GamaPoint axisS = toPoint(position.getAxis());
		if (axisS != null) {
			if (axis == null) {axis = new GamaPoint(axisS.toCoordinate());}
			else{axis.x += axisS.x;axis.y += axisS.y;axis.z += axisS.z;}}
		
		GamaPoint directionS = toPoint(position.getRefDirection());
		if (directionS != null) {
			if (direction == null) {direction = new GamaPoint(directionS.toCoordinate());}
			else {direction.x += directionS.x;direction.y += directionS.y;direction.z += directionS.z;}}
		points.add(loc);points.add(axis);points.add(direction);
		return points;
	}
	
	public List<GamaPoint> updateLocAxisDir(IfcAxis2Placement2D position, GamaPoint loc , GamaPoint axis,GamaPoint direction) {
		List<GamaPoint> points = new ArrayList<GamaPoint>();
		if (position == null) {points.add(loc);points.add(axis);points.add(direction);return points;}
		GamaPoint locS = toPoint(position.getLocation());
		if (locS != null) {
			if (loc == null) {loc = new GamaPoint(locS.toCoordinate());}
			else {loc.x += locS.x;loc.y += locS.y;loc.z += locS.z;}}
		
		GamaPoint directionS = toPoint(position.getRefDirection());
		if (directionS != null) {
			if (direction == null) {direction = new GamaPoint(directionS.toCoordinate());}
			else {direction.x += directionS.x;direction.y += directionS.y;direction.z += directionS.z;}}	
		points.add(loc);points.add(axis);points.add(direction);
		return points;
	}
	
	public IShape toGeom(IScope scope, Collection<IfcCartesianPoint> line) {
		 List<IShape> pts = new ArrayList<IShape>();
		 for (IfcCartesianPoint pt : line) {
			 pts.add(toPoint(pt));
		 }	
		 return GamaGeometryType.buildPolyline(pts);
	}
	
	public IShape createWall(final IScope scope, IfcWall w){
		GamaPoint loc = null;
		GamaPoint axis = null;
		GamaPoint direction = null;
		IfcObjectPlacement pla = w.getObjectPlacement();
		if (pla instanceof IfcLocalPlacement) {
			IfcLocalPlacement locpla = (IfcLocalPlacement) pla;
			List<GamaPoint> pts = updateLocAxisDir((IfcAxis2Placement3D) locpla.getRelativePlacement(), loc, axis, direction);
			loc = pts.get(0);axis = pts.get(1);direction = pts.get(2);
		}
		IList<IShape> linePts = GamaListFactory.create(Types.POINT);
		for (IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item: r.getItems()) {
				if (!(item instanceof IfcPolyline)) continue;
				IfcPolyline lineItem = (IfcPolyline) item;
				for (IfcCartesianPoint pt : lineItem.getPoints()) {
					linePts.add(toPoint(pt));		
				}
			}
		}
		IShape line = Spatial.Creation.line(scope, linePts);
		if (direction != null) {
			 line = Spatial.Transformations.rotated_by(scope, line, Math.toDegrees(Math.acos(direction.x)) * (direction.y < 0 ? -1 : 1));
		}
		for (IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item: r.getItems()) {
				if (!(item instanceof IfcExtrudedAreaSolid)) continue;
				IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
				List<GamaPoint> pts = updateLocAxisDir(solid.getPosition(), loc, axis, direction);
				loc = pts.get(0);axis = pts.get(1);direction = pts.get(2);
				IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
				Double width = profil.getXDim().value;
				Double height = profil.getYDim().value;
				Double depth = solid.getDepth().value;
				IShape box = Spatial.Creation.box(scope,width, height, depth);
				if (loc != null) box.setLocation(loc);
				if (direction != null) box = Spatial.Transformations.rotated_by(scope, box, Math.toDegrees(Math.acos(direction.x)) * (direction.y < 0 ? -1 : 1));
				box = Spatial.Transformations.translated_by(scope, box, new GamaPoint(line.getLocation().getX() - line.getPoints().get(0).getX(), line.getLocation().getY() - line.getPoints().get(0).getY()));
				box.setAttribute(IKeyword.NAME, w.getName().getDecodedValue());
				return box;
			}
		 }
		 return null;
	}
	
	public IShape createSlab(final IScope scope, IfcSlab s){
		 GamaPoint loc = null;
		 GamaPoint axis = null;
		 GamaPoint direction = null;
		 
		for (IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item : rep.getItems()) {
				if (item instanceof IfcExtrudedAreaSolid) {
					IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					List<GamaPoint> pts = updateLocAxisDir(solid.getPosition(), loc, axis, direction);
					loc = pts.get(0);axis = pts.get(1);direction = pts.get(2);
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						Double width = profil.getXDim().value;
						Double height = profil.getYDim().value;
						Double depth = solid.getDepth().value;
						IShape box = Spatial.Creation.box(scope,width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						if (loc != null) box.setLocation(loc);
						if (direction != null) box = Spatial.Transformations.rotated_by(scope, box, Math.toDegrees(Math.acos(direction.x)) * (direction.y < 0 ? -1 : 1));
						return box;
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) { 
						IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							IShape shape = toGeom(scope,((IfcPolyline)curve).getPoints());
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							if (loc != null) shape.setLocation(loc);
							if (direction != null) shape = Spatial.Transformations.rotated_by(scope, shape, Math.toDegrees(Math.acos(direction.x)) * (direction.y < 0 ? -1 : 1));
							return shape;
						} 
						return null;
					}
				}
			}
		}
		return null;
	}
		
		
	

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);
		
		try {
			IfcModel ifcModel = new IfcModel();
			ifcModel.readStepFile(getFile(scope));
		} catch (final Exception e) {
			ifc2x3javatoolbox.ifcmodel.IfcModel ifcModel = new ifc2x3javatoolbox.ifcmodel.IfcModel();
			try {
				ifcModel.readStepFile(getFile(scope));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Collection<IfcSlab> slabs = ifcModel.getCollection(IfcSlab.class);
			 for (IfcSlab s : slabs) {
				 IShape g = createSlab(scope,s);
				 if (g != null) geoms.add(g);
		 	}
			 Collection<IfcWall> walls = ifcModel.getCollection(IfcWall.class);
				
			 for (IfcWall w : walls) {
				 IShape g = createWall(scope,w);
				 if (g != null) geoms.add(g);
			 }
			
			 Collection<IfcPolyLoop> polys = ifcModel.getCollection(IfcPolyLoop.class);
			 for (IfcPolyLoop obj : polys) {
				 IShape shape = toGeom(scope,obj.getPolygon());
				 geoms.add(shape);	
			 }
			 
			 final IList<IShape> geoms2 = GamaListFactory.create(Types.GEOMETRY);
				
			 Envelope env = GeometryUtils.computeEnvelopeFrom(scope, geoms);
			for (IShape geom : geoms) {
				geoms2.add(Spatial.Transformations.translated_by(scope, geom, new GamaPoint(- 1 * env.getMinX(), - 1* env.getMinY())));
			}
			 Envelope env2 = GeometryUtils.computeEnvelopeFrom(scope, geoms2);
			 setBuffer(geoms2);
		}
		
			
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if (getBuffer() == null) 
			fillBuffer(scope);
		if (getBuffer() == null) return null;
		Envelope env = GeometryUtils.computeEnvelopeFrom(scope, getBuffer());
		invalidateContents();
		return env;
	}
}
