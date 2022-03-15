package spll.datamapper.variable;

import core.metamodel.value.IValue;

public class SPLVariable implements ISPLVariable {

	private IValue value;
	private String name;
	
	public SPLVariable(IValue value, String name) {
		this.value = value;
		this.name = name;
	}
	
	@Override
	public IValue getValue() {
		return value;
	}
	
	@Override
	public String getStringValue() {
		return getValue().toString();
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	// -------------------------------------------------- //
	
	@Override
	public String toString() {
		return getName()+": "+getValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((this.getStringValue() == null) ? 0 : this.getStringValue().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SPLVariable other = (SPLVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (getStringValue() == null) {
			if (other.getStringValue() != null)
				return false;
		} else if (!getStringValue().equals(other.getStringValue()))
			return false;
		return true;
	}



}
