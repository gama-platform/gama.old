/*********************************************************************************************
 *
 * 'GamaDXFFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package miat.gama.extension.ifcfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import ifc2x3javatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCartesianPoint;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCurve;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDirection;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLocalPlacement;
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
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpace;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc4javatoolbox.ifcmodel.IfcModel;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
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
@file (
		name = "ifc",
		extensions = { "ifc" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { "ifc", IConcept.FILE })
public class GamaIFCFile extends GamaGeometryFile {


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

	public GamaPoint toPoint(final IfcDirection direction) {
		if (direction != null) {
			if (direction.getDirectionRatios().size() > 2)
				return new GamaPoint(direction.getDirectionRatios().get(0).value,
						direction.getDirectionRatios().get(1).value, direction.getDirectionRatios().get(2).value);
			return new GamaPoint(direction.getDirectionRatios().get(0).value,
					direction.getDirectionRatios().get(1).value);
		}
		return null;
	}

	public GamaPoint toPoint(final IfcCartesianPoint point) {
		if (point != null) {
			if (point.getCoordinates().size() > 2)
				return new GamaPoint(point.getCoordinates().get(0).value, point.getCoordinates().get(1).value,
						point.getCoordinates().get(2).value);
			return new GamaPoint(point.getCoordinates().get(0).value, point.getCoordinates().get(1).value);
		}
		return null;
	}

	public class Axe {
		public GamaPoint origin;
		GamaPoint xDir;
		GamaPoint yDir;
		GamaPoint zDir;
		
		public Axe() {
			origin = new GamaPoint(0,0,0);
			xDir = new GamaPoint(1,0,0);
			yDir = new GamaPoint(0,1,0);
			zDir = new GamaPoint(0,0,1);
		}
		public Axe(Axe pa) {
			origin = new GamaPoint(pa.origin);
			xDir = new GamaPoint(pa.xDir);
			yDir = new GamaPoint(pa.yDir);
			zDir = new GamaPoint(pa.zDir);
		}
		
		public GamaPoint toNewRef(Coordinate pt, boolean normalize) {
			GamaPoint nPt = new GamaPoint();
			nPt.x = pt.x * xDir.x + pt.y * yDir.x + pt.z * zDir.x;
			nPt.y = pt.x * xDir.y + pt.y * yDir.y + pt.z * zDir.y;
			nPt.z = pt.x * xDir.z + pt.y * yDir.z + pt.z * zDir.z;
			if (normalize) {
				double dist = Math.sqrt((nPt.x * nPt.x) + (nPt.y * nPt.y) + (nPt.z * nPt.z)) ;
				nPt.x /= dist;nPt.y /= dist;nPt.z /= dist;
			}
			return nPt;		
		}
		
		public void addTranslation(GamaPoint transl) {
			GamaPoint newPt = toNewRef(transl, false);
			origin.x += newPt.x;
			origin.y += newPt.y;
			origin.z += newPt.z;
		}
		
		public void addRotation(GamaPoint xVector) {
			xDir = toNewRef(xVector, true);
			yDir = (GamaPoint) Spatial.Transformations.rotated_by(GAMA.getRuntimeScope(), xVector, 90).getLocation();
		}
		public void addRotation(GamaPoint xVector, GamaPoint zVector) {
			xDir = toNewRef(xVector, true);
			zDir = toNewRef(zVector, true);
			yDir = new GamaPoint(-1 *(xDir.y * zDir.z - xDir.z * zDir.y),-1 *(xDir.z * zDir.x - xDir.x * zDir.z),-1 * (xDir.x * zDir.y - xDir.y * zDir.x));
		}
		
		public void transform(IShape shape) {
			shape.getInnerGeometry().apply((final Coordinate p) -> {
				GamaPoint np = toNewRef(p, false);
				p.x = np.x + origin.x;
				p.y = np.y+ origin.y;
				p.z = np.z+ origin.z;
			});
		}
		
		public void update(List<IfcAxis2Placement> axispls, boolean reverse) {
			if (reverse) Collections.reverse(axispls);
			for (IfcAxis2Placement ap : axispls) update(ap);
		}
		
		public void update(IfcAxis2Placement axispl) {
			if (axispl instanceof IfcAxis2Placement2D) {
				final IfcAxis2Placement2D axispl2D = (IfcAxis2Placement2D) axispl;
				final GamaPoint loc = toPoint(axispl2D.getLocation());
				addTranslation(loc);
				if (axispl2D.getRefDirection() != null) {
					final GamaPoint dir = toPoint(axispl2D.getRefDirection());
					addRotation(dir);
				}
			} else if (axispl instanceof IfcAxis2Placement3D) {
				final IfcAxis2Placement3D axispl3D = (IfcAxis2Placement3D) axispl;
				final GamaPoint loc = toPoint(axispl3D.getLocation());
				addTranslation(loc);
				if (axispl3D.getRefDirection() != null) {
					final GamaPoint dir = toPoint(axispl3D.getRefDirection());
					final GamaPoint axis = toPoint(axispl3D.getAxis());
					addRotation(dir,axis);
				}	
			}
		}
		@Override
		public String toString() {
			return "Axe [origin=" + origin + ", xDir=" + xDir + ", yDir=" + yDir + ", zDir=" + zDir + "]";
		}
		
		
	}
	
	

	public IShape toGeom(final IScope scope, final Collection<IfcCartesianPoint> line, final boolean polygon) {
		final List<IShape> pts = new ArrayList<IShape>();
		for (final IfcCartesianPoint pt : line) {
			pts.add(toPoint(pt));
		}
		return polygon ? GamaGeometryType.buildPolygon(pts) : GamaGeometryType.buildPolyline(pts);
	}


	public IShape createOpening(final IScope scope, final IfcOpeningElement o) {
		if ( o.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, o.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		for (final IfcRepresentation rep : o.getRepresentation().getRepresentations()) {
		for (final IfcRepresentationItem item : rep.getItems()) {
			if (item instanceof IfcExtrudedAreaSolid) {
				final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
				if(solid.getPosition() != null) {
					newAxe.update(solid.getPosition());
				}
				final Double depth = solid.getDepth().value;
				if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
					final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
					final Double height = profil.getXDim().value;
					final Double width = profil.getYDim().value;
						IShape box = Spatial.Creation.box(scope, height,width, depth);
						box.setAttribute(IKeyword.NAME, o.getName().getDecodedValue());
						newAxe.transform(box);
						addAttribtutes(o, box);

						return box;
					}

				}
			}
		}
		return null;
	}
	
	public Double defineDoorDepth(final IScope scope, final IfcObjectPlacement placement,Map<IfcProduct, Double> depths) {
		if (placement instanceof IfcLocalPlacement) {
			final IfcObjectPlacement pla = ((IfcLocalPlacement) placement).getPlacementRelTo();
			if (pla.getPlacesObject_Inverse() != null) {
				for (IfcProduct p: pla.getPlacesObject_Inverse()) {
					if (depths.containsKey(p)) return depths.get(p);
				}
			}
			if (pla != null) {
				return defineDoorDepth(scope, pla, depths);
			}
		} 
		return null;
	}

	
	public IShape createDoor(final IScope scope, final IfcDoor d,Map<IfcProduct, Double> depths) {
		if ( d.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, d.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		double height = d.getOverallHeight().value;
		double width = d.getOverallWidth().value;
		Double depth = defineDoorDepth(scope, d.getObjectPlacement(), depths);
		if (depth == 0.0) depth = width/10.0;
		IShape box = Spatial.Creation.box(scope, width,depth,height);
		IList<IShape> pts = GamaListFactory.create();
		pts.add(new GamaPoint(-depth/2.0, 0)); pts.add(new GamaPoint(depth/2.0, 0.0));
		IShape line = Spatial.Creation.line(scope, pts);
		box.setAttribute(IKeyword.NAME, d.getName().getDecodedValue());

		box = Spatial.Transformations.translated_by(scope, box,
				new GamaPoint(line.getLocation().getX() - line.getPoints().get(0).getX(),
						line.getLocation().getY() - line.getPoints().get(0).getY()));
	
		addAttribtutes(d, box);
		newAxe.transform(box);
		
		return box;
	}

	public IShape createWindow(final IScope scope, final IfcWindow d,Map<IfcProduct, Double> depths) {
		if ( d.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, d.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		double height = d.getOverallHeight().value;
		double width = d.getOverallWidth().value;
		Double depth = defineDoorDepth(scope, d.getObjectPlacement(), depths);
		if (depth == null) depth = width/10.0;
		IShape box = Spatial.Creation.box(scope, width,depth,height);
		IList<IShape> pts = GamaListFactory.create();
		pts.add(new GamaPoint(-width/2.0, 0.0)); pts.add(new GamaPoint(width/2.0, 0.0));
		IShape line = Spatial.Creation.line(scope, pts);
		box.setAttribute(IKeyword.NAME, d.getName().getDecodedValue());
		box = Spatial.Transformations.translated_by(scope, box,
				new GamaPoint(line.getLocation().getX() - line.getPoints().get(0).getX(),
						2 *depth ));
	
		addAttribtutes(d, box);
		newAxe.transform(box);
		
		return box;
	}
	public IShape createWall(final IScope scope, final IfcWall w, final Map<IfcProduct, Double> depths) {
		if ( w.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, w.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		final IList<IShape> linePts = GamaListFactory.create(Types.POINT);

		for (final IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : r.getItems()) {
				if (!(item instanceof IfcPolyline))
					continue;
				final IfcPolyline lineItem = (IfcPolyline) item;
				for (final IfcCartesianPoint pt : lineItem.getPoints()) {
					linePts.add(toPoint(pt));
				}
			}
		}
		IShape line = Spatial.Creation.line(scope, linePts);
		newAxe.transform(line);
		for (final IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : r.getItems()) {
				if (!(item instanceof IfcExtrudedAreaSolid))
					continue;
				final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
				final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
				final Double width = profil.getXDim().value;
				final Double height = profil.getYDim().value;
				final Double depth = solid.getDepth().value;
				depths.put(w,height);
				
				IShape box = Spatial.Creation.box(scope, width, height, depth);
				newAxe.transform(box);
				
				box = Spatial.Transformations.translated_by(scope, box,
						new GamaPoint(line.getLocation().getX() - line.getPoints().get(0).getX(),
								line.getLocation().getY() - line.getPoints().get(0).getY()));
				box.setAttribute(IKeyword.NAME, w.getName().getDecodedValue());
				addAttribtutes(w, box);
				return box;
			}
		}
		return null;
	}

	public IShape createSlab(final IScope scope, final IfcSlab s) {
		if ( s.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, s.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		for (final IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : rep.getItems()) {

				if (item instanceof IfcExtrudedAreaSolid) {
					final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					final Double depth = solid.getDepth().value;
					if(solid.getPosition() != null) {
						newAxe.update(solid.getPosition());
					}
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						final Double width = profil.getXDim().value;
						final Double height = profil.getYDim().value;
						IShape box = Spatial.Creation.box(scope, width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						newAxe.transform(box);
						addAttribtutes(s, box);
						box = Spatial.Transformations.translated_by(scope, box, new GamaPoint(0,0,-depth));
						return box;
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) {
						final IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						final IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							IShape shape = toGeom(scope, ((IfcPolyline) curve).getPoints(), true);
							shape.setDepth(depth);
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							addAttribtutes(s, shape);
							newAxe.transform(shape);
							return shape;
						}
						return null;
					}
				}
			}
		}
		return null;
	}
	
	
	public IShape createSpace(final IScope scope, final IfcSpace s) {
		if ( s.getObjectPlacement() == null) return null;
		Axe newAxe = new Axe();
		List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, s.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		
		for (final IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : rep.getItems()) {
				if (item instanceof IfcExtrudedAreaSolid) {
					final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					if(solid.getPosition() != null) {
						newAxe.update(solid.getPosition());
					}
					final Double depth = solid.getDepth().value;
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						final Double width = profil.getXDim().value;
						final Double height = profil.getYDim().value;
						IShape box = Spatial.Creation.box(scope, width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						newAxe.transform(box);
						addAttribtutes(s, box);
						return box;
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) {
						final IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						final IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							IShape shape = toGeom(scope, ((IfcPolyline) curve).getPoints(), true);
							shape.setDepth(depth);
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							newAxe.transform(shape);
							
							addAttribtutes(s, shape);
							return shape;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	public void addAttribtutes(final IfcProduct product, final IShape shape) {
		shape.setAttribute("type", product.getClass().getSimpleName());
		if (product.getIsDefinedBy_Inverse() == null)
			return;
		for (final IfcRelDefines rd : product.getIsDefinedBy_Inverse()) {
			if (rd instanceof IfcRelDefinesByProperties) {
				final IfcRelDefinesByProperties rlp = (IfcRelDefinesByProperties) rd;
				if (rlp.getRelatingPropertyDefinition() instanceof IfcPropertySet) {
					final IfcPropertySet ps = (IfcPropertySet) rlp.getRelatingPropertyDefinition();
					for (final IfcProperty p : ps.getHasProperties()) {
						if (p instanceof IfcPropertySingleValue) {
							shape.setAttribute(p.getName().getDecodedValue(),
									((IfcPropertySingleValue) p).getNominalValue().toString());

						}
					}
				}
			}
		}
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);

		try {
			final IfcModel ifcModel = new IfcModel();
			ifcModel.readStepFile(getFile(scope));
		} catch (final Exception e) {
			final ifc2x3javatoolbox.ifcmodel.IfcModel ifcModel = new ifc2x3javatoolbox.ifcmodel.IfcModel();
			try {
				ifcModel.readStepFile(getFile(scope));
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
			
			Map<IfcProduct, Double> depths = new Hashtable<>();
			final Collection<IfcWall> walls = ifcModel.getCollection(IfcWall.class);
			for (final IfcWall w : walls) {
				final IShape g = createWall(scope, w, depths);
				if (g != null)
					geoms.add(g);
			}
			
			final Collection<IfcSlab> slabs = ifcModel.getCollection(IfcSlab.class);
			for (final IfcSlab s : slabs) {
				final IShape g = createSlab(scope, s);
				if (g != null)
					geoms.add(g);
			}
			
			final Collection<IfcSpace> spaces = ifcModel.getCollection(IfcSpace.class);
			for (final IfcSpace s : spaces) {
				final IShape g = createSpace(scope, s);
				if (g != null)
					geoms.add(g);
			}
			
			final Collection<IfcOpeningElement> opening = ifcModel.getCollection(IfcOpeningElement.class);
			for (final IfcOpeningElement o : opening) {
				final IShape g = createOpening(scope, o);
				if (g != null)
					geoms.add(g);
			}
			

			final Collection<IfcDoor> doors = ifcModel.getCollection(IfcDoor.class);
			for (final IfcDoor s : doors) {
				final IShape g = createDoor(scope, s, depths);
				if (g != null)
					geoms.add(g);
			}
			final Collection<IfcWindow> windows = ifcModel.getCollection(IfcWindow.class);
			for (final IfcWindow s : windows) {
				final IShape g = createWindow(scope, s, depths);
				if (g != null)
					geoms.add(g);
			}
			setBuffer(geoms);
		}

	}
	
	public void relatedTo(final IScope scope, final IfcObjectPlacement placement, List<IfcAxis2Placement> aps) {
		if (placement instanceof IfcLocalPlacement) {
			final IfcObjectPlacement pla = ((IfcLocalPlacement) placement).getPlacementRelTo();
			final IfcAxis2Placement axispl = ((IfcLocalPlacement) placement).getRelativePlacement();
			aps.add(axispl);
			if (pla != null) {
				relatedTo(scope, pla, aps);
			}
		} 
	}
	
	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		boolean didFillBuffer = false; 
		if (getBuffer() == null) {
			fillBuffer(scope);
			didFillBuffer = true;
		}
		if (getBuffer() == null)
			return null;
		Envelope3D env = GeometryUtils.computeEnvelopeFrom(scope, getBuffer());
		
		if (didFillBuffer) {
			GamaPoint vect = new GamaPoint(-env.getMinX(), -env.getMinY(), -env.getMinZ());
			IList<IShape> newBuffer  = GamaListFactory.create();
			for (IShape buff: getBuffer()) {
				newBuffer.add(Spatial.Transformations.translated_by(scope, buff,vect));
			}
			setBuffer(newBuffer);
			env = GeometryUtils.computeEnvelopeFrom(scope, getBuffer());
		}
		return env;
	}
}
