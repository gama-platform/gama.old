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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;

import ifc2x3javatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCartesianPoint;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCurve;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDirection;
import ifc2x3javatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import ifc2x3javatoolbox.ifc2x3tc1.IfcGridPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLocalPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcOpeningElement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPolyline;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProperty;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySet;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySingleValue;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefines;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByProperties;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentationItem;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSlab;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc4javatoolbox.ifcmodel.IfcModel;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.AffineTransform3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.fastmaths.FastMath;
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
		//System.out.println("updateLocAxisDir locS: " + locS);
		if (locS != null) {
			if (loc == null) {loc = new GamaPoint(locS.toCoordinate());}
			else {loc.x += locS.x;loc.y += locS.y;loc.z += locS.z;}}
		
		GamaPoint axisS = toPoint(position.getAxis());
		if (axisS != null) {
			double angleX = Math.toDegrees(Math.asin((axisS.x))) ;
			double angleY = Math.toDegrees(Math.asin(axisS.y)) ;
			if (axis == null) {
				axis = new GamaPoint(angleX,angleY,axisS.z);}
			else {
				axis.x += angleX;axis.y += angleY;axis.z += axisS.z;
			}
		}
		
		GamaPoint directionS = toPoint(position.getRefDirection());
		if (directionS != null) {
			double dist = Math.sqrt(directionS.x * directionS.x + directionS.y * directionS.y);
			if (dist == 0) dist = 1;
			//System.out.println("updateLocAxisDir dist2: " + dist);
			//System.out.println("directionS: " + directionS);
			double angle = Math.toDegrees(Math.acos(directionS.x/dist)) * (directionS.y < 0 ? -1 : 1);
			//System.out.println("angle: " + angle);
			
			double angleZ = 0;//-1 *Math.toDegrees(Math.asin(directionS.z)) ;
			
			if (direction == null) {
				direction = new GamaPoint(angle,0,angleZ);}
			else {
				direction.x += angle;direction.z += angleZ;
			}
		}
		points.add(loc);points.add(axis);points.add(direction);
		return points;
	}
	
	public List<GamaPoint> updateLocAxisDir(IfcAxis2Placement2D position, GamaPoint loc , GamaPoint axis,GamaPoint direction) {
		List<GamaPoint> points = new ArrayList<GamaPoint>();
		if (position == null) {points.add(loc);points.add(axis);points.add(direction);return points;}
		GamaPoint locS = toPoint(position.getLocation());
		//System.out.println("updateLocAxisDir locS: " + locS);
		if (locS != null) {
			if (loc == null) {loc = new GamaPoint(locS.toCoordinate());}
			else {loc.x += locS.x;loc.y += locS.y;loc.z += locS.z;}}
		
		GamaPoint directionS = toPoint(position.getRefDirection());
		if (directionS != null) {
			double dist = Math.sqrt(directionS.x * directionS.x + directionS.y * directionS.y);
			//System.out.println("updateLocAxisDir dist: " + dist);
			if (dist == 0) dist = 1;
			double angle = Math.toDegrees(Math.acos(directionS.x/dist)) * (directionS.y < 0 ? -1 : 1);
			//System.out.println("angle: " + angle);
			
			double angleZ = 0;//-1 * Math.toDegrees(Math.asin(directionS.z)) ;
			if (direction == null) {
				direction = new GamaPoint(angle,0,angleZ);}
			else {
				direction.x += angle;direction.z += angleZ;
			}}
		points.add(loc);points.add(axis);points.add(direction);
		return points;
	}
	
	public void relatedTo(IScope scope,  IfcObjectPlacement placement, List<GamaPoint> points) {
		if ( placement instanceof IfcLocalPlacement) {
			IfcObjectPlacement pla = ((IfcLocalPlacement)placement).getPlacementRelTo();
			IfcAxis2Placement axispl = ((IfcLocalPlacement) placement).getRelativePlacement();
			if (axispl instanceof IfcAxis2Placement2D) {
				IfcAxis2Placement2D axispl2D = (IfcAxis2Placement2D) axispl;
				GamaPoint loc = toPoint(axispl2D.getLocation());
				points.get(0).x += loc.x;points.get(0).y += loc.y;points.get(0).z += loc.z;
			//	System.out.println("loc: " + loc);
				if (axispl2D.getRefDirection() != null) {
					GamaPoint dir = toPoint(axispl2D.getRefDirection());
					double dist = 1;// Math.sqrt(dir.x * dir.x + dir.z * dir.z );
				//	System.out.println("dist: " + dist);
					if (dist == 0) dist = 1;
					double angle = Math.toDegrees(Math.acos(dir.x/dist)) * (dir.y < 0 ? -1 : 1);
					double angleZ =0;// -1 * Math.toDegrees(Math.asin(dir.z)) ;
				//	System.out.println("angle: " + angle);
					points.get(2).x += angle;points.get(2).z += angleZ;
				}
				
			} else if (axispl instanceof IfcAxis2Placement3D) {
				IfcAxis2Placement3D axispl3D = (IfcAxis2Placement3D) axispl;
				GamaPoint loc = toPoint(axispl3D.getLocation());
			//	System.out.println("loc3D: " + loc);
				points.get(0).x += loc.x;points.get(0).y += loc.y;points.get(0).z += loc.z;
				if (axispl3D.getRefDirection() != null) {
					GamaPoint dir = toPoint(axispl3D.getRefDirection());
			//		System.out.println("dir: "+ dir);
					double dist = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
			//		System.out.println("dist2: " + dist);
					if (dist == 0) dist = 1;
					double angle = Math.toDegrees(Math.acos(dir.x/dist)) * (dir.y < 0 ? -1 : 1);
					
					double angleZ =0;// -1 * Math.toDegrees(Math.asin(dir.z)) ;
			//		System.out.println("angle: " + angle);
					points.get(2).x += angle;points.get(2).z += angleZ;
					
				}
				if (axispl3D.getAxis() != null) {
					GamaPoint ax = toPoint(axispl3D.getAxis());
					double angleX = Math.toDegrees(Math.asin((ax.x))) ;
					double angleY = Math.toDegrees(Math.asin(ax.y)) ;
					points.get(1).x += angleX;points.get(1).y += angleY;points.get(1).z += ax.z;
				}
			}
			
			if (pla != null) {
				relatedTo(scope,  pla, points) ;
			}
		} else if (placement instanceof IfcGridPlacement){
			
		}
	//	System.out.println("points.get(2): " + points.get(2));
	}
	
	
	public IShape toGeom(IScope scope, Collection<IfcCartesianPoint> line, boolean polygon) {
		 List<IShape> pts = new ArrayList<IShape>();
		 for (IfcCartesianPoint pt : line) {
			 pts.add(toPoint(pt));
		 }	
		 return polygon? GamaGeometryType.buildPolygon(pts): GamaGeometryType.buildPolyline(pts);
	}
	
	public Integer directionInDegreesTo(final IScope scope, final IShape g1, final IShape g2) {
		final ILocation source = g1.getLocation();
		final ILocation target = g2.getLocation();
		final double x2 = target.getX();
		final double y2 = target.getY();
		final double dx = x2 - source.getX();
		final double dy = y2 - source.getY();
		final double result = Maths.atan2(dy, dx);
		return Maths.checkHeading((int) result);
	}
	
	public IShape createOpening2(final IScope scope, IfcOpeningElement o, Map<String, IShape> ws){
		//boolean isOpening = o.getObjectType().getDecodedValue().equals("Opening");
	//	System.out.println("\n Opening "+ o.getName() + " creation");
		IShape w = ws.get(o.getName().getDecodedValue());
		GamaPoint loc = w != null ? (GamaPoint) w.getPoints().get(0): new GamaPoint(0,0,0);
		GamaPoint axis = new GamaPoint(0,0,0);
		GamaPoint direction = new GamaPoint(0,0,0);
		
		for (IfcRepresentation rep : o.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item : rep.getItems()) {
				if (item instanceof IfcExtrudedAreaSolid) {
					IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					
					//
					List<GamaPoint> pts = updateLocAxisDir(solid.getPosition(), loc, axis, direction);
				//	System.out.println(o.getName() + " solid : " + pts);
					loc = pts.get(0);axis = pts.get(1);direction = pts.get(2);
					Double depth = solid.getDepth().value;
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						Double height = profil.getXDim().value;
						Double width = profil.getYDim().value;
				//		System.out.println(o.getName() + " -> " + "width:" + width + " height:" + height + " depth:" + depth );
						IShape box = Spatial.Creation.box(scope,width,depth,height );
						box.setAttribute(IKeyword.NAME, o.getName().getDecodedValue());
						if (loc != null) box.setLocation(loc);
						ILocation pt0 = w.getPoints().get(0);
						double tow =directionInDegreesTo(scope,  pt0, w.getPoints().get(1));
						//box = Spatial.Transformations.rotated_by(scope, box, tow);
					//	System.out.println(o.getName() +  " -> " + direction + " tow: " + tow );
						box.getGeometry().getInnerGeometry().apply(AffineTransform3D.createRotationOz(FastMath.toRadians(-90), pt0.getX(), pt0.getY()));
						box.setLocation(box.getPoints().get(1));
						addAttribtutes(o,box);
						return box;
					} 
				}
			}
		}
		return null;
	}
	
	
	public IShape createOpening(final IScope scope, IfcOpeningElement o){
		//boolean isOpening = o.getObjectType().getDecodedValue().equals("Opening");
	//	System.out.println("\n Opening "+ o.getName() + " creation");
		GamaPoint loc = new GamaPoint(0,0,0);
		GamaPoint axis = new GamaPoint(0,0,0);;
		GamaPoint direction = new GamaPoint(0,0,0);
		
		List<GamaPoint> points = new ArrayList<GamaPoint>();
		points.add(loc); points.add(axis); points.add(direction);
		relatedTo(scope, o.getObjectPlacement(),points);
	//	System.out.println(o.getName() + " points : " + points);
		for (IfcRepresentation rep : o.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item : rep.getItems()) {
				if (item instanceof IfcExtrudedAreaSolid) {
					IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					List<GamaPoint> pts = updateLocAxisDir(solid.getPosition(), loc, axis, direction);
				//	System.out.println(o.getName() + " solid : " + pts);
					loc = pts.get(0);axis = pts.get(1);direction = pts.get(2);
					Double depth = solid.getDepth().value;
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						Double width = profil.getXDim().value;
						Double height = profil.getYDim().value;
						
						IShape box = Spatial.Creation.box(scope,height,depth, width);
						box.setAttribute(IKeyword.NAME, o.getName().getDecodedValue());
						if (loc != null) box.setLocation(loc);
					//	System.out.println(o.getName() + " ici -> " + loc);
					//	System.out.println(o.getName() + " la -> " + box.getLocation());
						
						//loc.x -= width;
						//loc.z -= height/2.0;
						
						if (direction != null) {
							box = Spatial.Transformations.rotated_by(scope, box, direction.x);
							box = Spatial.Transformations.rotated_by(scope, box, direction.z, new GamaPoint(1,0,0));
						}
						addAttribtutes(o,box);
						
						return box;
					} 
					
					
					
				}
			}
		}
		return null;
	}

	public IShape createWall(final IScope scope, IfcWall w){
		GamaPoint loc = new GamaPoint(0,0,0);
		 GamaPoint axis = new GamaPoint(0,0,0);;
		 GamaPoint direction = new GamaPoint(0,0,0);
		 List<GamaPoint> points = new ArrayList<GamaPoint>();
		 points.add(loc); points.add(axis); points.add(direction);
		 relatedTo(scope, w.getObjectPlacement(),points);
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
			 line = Spatial.Transformations.rotated_by(scope, line, direction.x);
			 line = Spatial.Transformations.rotated_by(scope, line, direction.z, new GamaPoint(1,0,0));
			 
		}
		for (IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item: r.getItems()) {
				if (!(item instanceof IfcExtrudedAreaSolid)) continue;
				IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
				IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
				Double width = profil.getXDim().value;
				Double height = profil.getYDim().value;
				Double depth = solid.getDepth().value;
				
				IShape box = Spatial.Creation.box(scope,width, height, depth);
				if (loc != null) box.setLocation(loc);
				if (direction != null) {
					box = Spatial.Transformations.rotated_by(scope, box, direction.x);
					box = Spatial.Transformations.rotated_by(scope, box, direction.z, new GamaPoint(1,0,0));
				}
				box = Spatial.Transformations.translated_by(scope, box, new GamaPoint(line.getLocation().getX() - line.getPoints().get(0).getX(), line.getLocation().getY() - line.getPoints().get(0).getY()));
				box.setAttribute(IKeyword.NAME, w.getName().getDecodedValue());
				addAttribtutes(w,box);
				return box;
			}
		 }
		 return null;
	}
	
	public IShape createSlab(final IScope scope, IfcSlab s){
		for (IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (IfcRepresentationItem item : rep.getItems()) {
				
				if (item instanceof IfcExtrudedAreaSolid) {
					 GamaPoint loc = new GamaPoint(0,0,0);
					 GamaPoint axis = new GamaPoint(0,0,0);;
					 GamaPoint direction = new GamaPoint(0,0,0);
					 List<GamaPoint> points = new ArrayList<GamaPoint>();
					 points.add(loc); points.add(axis); points.add(direction);
					 relatedTo(scope, s.getObjectPlacement(),points);
					
					IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					List<GamaPoint> pts = updateLocAxisDir(solid.getPosition(), loc, axis, direction);
					loc = pts.get(0);
					axis = pts.get(1);direction = pts.get(2);
					Double depth = solid.getDepth().value;
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						Double width = profil.getXDim().value;
						Double height = profil.getYDim().value;
						IShape box = Spatial.Creation.box(scope,width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						if (axis != null){
							box = Spatial.Transformations.rotated_by(scope, box, axis.x, new GamaPoint(0,-1,0));
							box = Spatial.Transformations.rotated_by(scope, box, axis.y, new GamaPoint(-1,0,0));
						}
						if (loc != null) box.setLocation(loc);
						if (direction != null) {
							box = Spatial.Transformations.rotated_by(scope, box, direction.x);
							box = Spatial.Transformations.rotated_by(scope, box, direction.z, new GamaPoint(1,0,0));
						}
						addAttribtutes(s,box);
						return box;
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) { 
						IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							IShape shape = toGeom(scope,((IfcPolyline)curve).getPoints(), true);
							shape.setDepth(depth);
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							if (axis != null){
								shape = Spatial.Transformations.rotated_by(scope, shape, axis.x, new GamaPoint(0,-1,0));
								shape = Spatial.Transformations.rotated_by(scope, shape, axis.y, new GamaPoint(-1,0,0));
							}
							if (loc != null) shape.setLocation(loc);
							if (direction != null) {
								
								shape = Spatial.Transformations.rotated_by(scope, shape, direction.x);
								shape = Spatial.Transformations.rotated_by(scope, shape, direction.z, new GamaPoint(1,0,0));
							}
							
							addAttribtutes(s,shape);
							return shape;
						} 
						return null;
					}
				}
			}
		}
		return null;
	}
		
		
	
	public void addAttribtutes(IfcProduct product, IShape shape) {
		 shape.setAttribute("type", product.getClass().getSimpleName());
		 if (product.getIsDefinedBy_Inverse() == null) return;
		 for (IfcRelDefines rd: product.getIsDefinedBy_Inverse()) {
			if (rd instanceof IfcRelDefinesByProperties) {
				 IfcRelDefinesByProperties rlp = (IfcRelDefinesByProperties) rd;
				 if (rlp.getRelatingPropertyDefinition() instanceof IfcPropertySet) {
					 IfcPropertySet ps = (IfcPropertySet) rlp.getRelatingPropertyDefinition() ;
					 for (IfcProperty p: ps.getHasProperties()) {
						 if (p instanceof IfcPropertySingleValue) {
							 shape.setAttribute(p.getName().getDecodedValue(), ((IfcPropertySingleValue) p).getNominalValue().toString());
							 
						 }
					 }
				 }
			 }
		 }
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		Set<IfcObject> already = new HashSet<IfcObject>();
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
				 already.add(s);
				 IShape g = createSlab(scope,s);
				 if (g != null) geoms.add(g);
		 	}
			 Collection<IfcWall> walls = ifcModel.getCollection(IfcWall.class);
			 Map<String, IShape> ws = new HashMap<>(); 
			 for (IfcWall w : walls) {
				 already.add(w);
				 IShape g = createWall(scope,w);
				 ws.put(w.getName().getDecodedValue(), g);
				 if (g != null) geoms.add(g);
			 }
			 
			 Collection<IfcOpeningElement> openings = ifcModel.getCollection(IfcOpeningElement.class);
			 for (IfcOpeningElement o : openings) {
				 already.add(o);
				 IShape g = createOpening(scope,o);
				 if (g != null) geoms.add(g);
				
			 }
			
			 setBuffer(geoms);
			 
			 /* Collection<IfcPolyLoop> polys = ifcModel.getCollection(IfcPolyLoop.class);
			 for (IfcPolyLoop obj : polys) {
				 IShape shape = toGeom(scope,obj.getPolygon(), false); 
				 geoms.add(shape);	
			 }*/
			 
			 /* final IList<IShape> geoms2 = GamaListFactory.create(Types.GEOMETRY);
			
			 Envelope env = GeometryUtils.computeEnvelopeFrom(scope, geoms);
			for (IShape geom : geoms) {
				System.out.println(geom.getAttribute("name") + " -> " + geom.getLocation());
				IShape g = Spatial.Transformations.translated_by(scope, geom, new GamaPoint(- 1 * env.getMinX(), - 1* env.getMinY()));
				//g.setLocation(new GamaPoint(g.getLocation().getX(),g.getLocation().getY(), geom.getLocation().getZ()));
				geoms2.add(g);
				
			}
			System.out.println("----------------");
			for (IShape g: geoms2) {
				System.out.println(g.getAttribute("name") + " -> " + g.getLocation());
			}
			setBuffer(geoms2);*/
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
