package msi.gama.lang.gaml.descript;

//import org.eclipse.emf.ecore.util.EcoreUtil;

import msi.gama.lang.gaml.gaml.Statement;

public class GamlDescriptError extends Exception { // implements Cloneable {
	private static final long serialVersionUID = 1L;
	private Statement statement;
	private boolean isWarning;

	public GamlDescriptError(String message, Statement statement) {
		super(message);
		this.statement = statement;
		this.isWarning = false;
	}

	public GamlDescriptError(String message, Statement statement, boolean isWarning) {
		super(message);
		this.statement = statement;
		this.isWarning = isWarning;
	}

	public Statement getStatement() {
		return statement;
	}

	public boolean isWraning() {
		return isWarning;
	}
/*
	protected Object clone() throws CloneNotSupportedException {
		GamlDescriptionError clone = (GamlDescriptionError) super.clone();
		clone.isWarning = this.isWarning;
		clone.statement = EcoreUtil.copy(this.statement);
		return clone;
	}
*/
}
