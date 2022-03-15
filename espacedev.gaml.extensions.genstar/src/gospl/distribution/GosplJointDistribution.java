package gospl.distribution;

import java.util.Map;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import core.util.data.GSDataParser;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.control.ControlFrequency;
import gospl.distribution.matrix.coordinate.ACoordinate;

/**
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 */
public class GosplJointDistribution extends AFullNDimensionalMatrix<Double> {
	
	private Double min = Math.pow(10, -8);
	
	protected GosplJointDistribution(Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> matrix){
		super(matrix);
	}

	public GosplJointDistribution(Set<Attribute<? extends IValue>> dimensions, GSSurveyType metaDataType) {
		super(dimensions, metaDataType);
	}

		
	// ----------------------- SETTER CONTRACT ----------------------- //
	
	
	@Override
	public boolean addValue(ACoordinate<Attribute<? extends IValue>, IValue> coordinates, AControl<? extends Number> value){
		if(matrix.containsKey(coordinates))
			return false;
		return setValue(coordinates, value);
	}
	
	@Override
	public final boolean addValue(ACoordinate<Attribute<? extends IValue>, IValue> coordinates, Double value) {
		return addValue(coordinates, new ControlFrequency(value));
	}

	@Override
	public boolean setValue(ACoordinate<Attribute<? extends IValue>, IValue> coordinate, AControl<? extends Number> value){
		if(isCoordinateCompliant(coordinate)){
			coordinate.setHashIndex(matrix.size());
			matrix.put(coordinate, new ControlFrequency(value.getValue().doubleValue()));
			return true;
		}
		return false;
	}
	

	@Override
	public final boolean setValue(ACoordinate<Attribute<? extends IValue>, IValue> coordinate, Double value) {
		return setValue(coordinate, new ControlFrequency(value));
	}
	
	// ----------------------- CONTRACT ----------------------- //
	
	@Override
	public AControl<Double> getNulVal() {
		return new ControlFrequency(0d);
	}
	

	@Override
	public AControl<Double> getIdentityProductVal() {
		return new ControlFrequency(1d);
	}
	
	@Override
	public AControl<Double> getAtomicVal() {
		return new ControlFrequency(min / this.size());
	}

	@Override
	public AControl<Double> parseVal(GSDataParser parser, String val) {
		if(!parser.getValueType(val).isNumericValue())
			return getNulVal();
		return new ControlFrequency(parser.getDouble(val));
	}

	@Override
	public void normalize() throws IllegalArgumentException {

		Double total = getVal().getValue();
		
		for (AControl<Double> c: getMatrix().values()) {
			c.multiply(1/total);
		}
			
	}
	
}
