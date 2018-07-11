package msi.gaml.operators;

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
			value = "a bool value, equal to the logical xor between the left-hand operand and the right-hand operand. False when they are equal",
			comment = "both operands are always casted to bool before applying the operator. Thus, an expression like 1 xor 0 is accepted and returns true.",
			see = { "or", "and", "!" })
	public static GamaKmlExport addShape(final IScope scope, final GamaKmlExport kml, final IShape shape,
			double lineWidth, GamaColor lineColor, GamaColor fillColor) throws GamaRuntimeException {
		if (kml == null || shape == null) return kml;
		GamaDate currentDate = scope.getClock().getCurrentDate();
		String styleName = shape.stringValue(scope) + ":" + currentDate.toString(); 
		kml.defStyle(styleName, lineWidth, lineColor,fillColor);
		GamaDate endDate = Dates.plusDuration(scope, currentDate, scope.getClock().getStepInSeconds());
		kml.addGeometry(scope, shape.toString(), currentDate, endDate, shape, styleName, shape.getHeight());
		return kml;
	}
}
