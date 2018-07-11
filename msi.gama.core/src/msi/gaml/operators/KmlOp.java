package msi.gaml.operators;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gaml.types.GamaKmlExport;

public class KmlOp {

	@operator (
			value = "add_geometry",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "the kml export manager with new geometry: take the following argument: (kml, geometry,linewidth, linecolor,fillcolor)",
			see = { "add_3Dmodel", "add_icon", "add_label"})
	public static GamaKmlExport addShape(final IScope scope, final GamaKmlExport kml, final IShape shape,
			double lineWidth, GamaColor lineColor, GamaColor fillColor) throws GamaRuntimeException {
		if (kml == null || shape == null) return kml;
		GamaDate currentDate = scope.getClock().getCurrentDate();
		String styleName = shape.stringValue(scope) + ":" + currentDate.toString(); 
		kml.defStyle(styleName, lineWidth, lineColor,fillColor);
		GamaDate endDate = Dates.plusDuration(scope, currentDate, scope.getClock().getStepInSeconds());
		kml.addGeometry(scope, shape.toString(), currentDate, endDate, shape, styleName, shape.getDepth() == null ? 0.0 : shape.getDepth());
		return kml;
	}
	
	@operator (
			value = "add_3Dmodel",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "the kml export manager with new 3D model: take the following argument: (kml, location (point),orientation (float), scale (float), file_path (string))",
			see = { "add_geometry", "add_icon", "add_label"})
	public static GamaKmlExport add3DModel(final IScope scope, final GamaKmlExport kml, final GamaPoint loc,
			double orientation, double scale, String file) throws GamaRuntimeException {
		if (kml == null || loc == null || file == null || file.isEmpty()) return kml;
		GamaDate currentDate = scope.getClock().getCurrentDate();
		GamaDate endDate = Dates.plusDuration(scope, currentDate, scope.getClock().getStepInSeconds());
		 kml.add3DModel(scope, loc, orientation, scale, currentDate, endDate, file);
		return kml;
	}
	
	@operator (
			value = "add_icon",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "the kml export manager with new icons: take the following argument: (kml, location (point),orientation (float), scale (float), file_path (string))",
			see = { "add_geometry", "add_icon"})
	public static GamaKmlExport addIcon(final IScope scope, final GamaKmlExport kml, final GamaPoint loc,
			double orientation, double scale, String file) throws GamaRuntimeException {
		if (kml == null || loc == null || file == null || file.isEmpty()) return kml;
		String styleName = loc.stringValue(scope) + ":" + loc.toString(); 
		kml.defIconStyle(styleName,FileUtils.constructAbsoluteFilePath(scope, file, true),scale, orientation);  
		
		GamaDate currentDate = scope.getClock().getCurrentDate();
		GamaDate endDate = Dates.plusDuration(scope, currentDate, scope.getClock().getStepInSeconds());
		kml.addLabel(scope, loc, currentDate, endDate, "", "", styleName);
		return kml;
	}
}
