package gospl.distribution.exception;

import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;

public class IllegalControlTotalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IllegalControlTotalException(AControl<? extends Number> control, AControl<? extends Number> controlAtt) {
		super("Two "+AControl.class.getSimpleName()+" are incompatible: "+control+" & "+controlAtt);
	}

	public IllegalControlTotalException(String message, AFullNDimensionalMatrix<? extends Number> matrix) {
		super(message+"\n"+matrix.toString());
	}


}
