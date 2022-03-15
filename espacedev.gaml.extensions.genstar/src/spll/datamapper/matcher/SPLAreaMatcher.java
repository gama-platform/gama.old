package spll.datamapper.matcher;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.datamapper.variable.SPLVariable;

public class SPLAreaMatcher implements ISPLMatcher<SPLVariable, Double> {

	private double area;
	
	private final SPLVariable variable;

	private AGeoEntity<? extends IValue> entity;
	
	protected SPLAreaMatcher(AGeoEntity<? extends IValue> entity, SPLVariable variable){
		this(entity, variable, 1d);
	}
	
	protected SPLAreaMatcher(AGeoEntity<? extends IValue> entity, SPLVariable variable, double area){
		this.entity = entity;
		this.variable = variable;
		this.area = area;
	}
	
	@Override
	public boolean expandValue(Double area){
		this.area += area;
		return true;
	}
	
	@Override
	public Double getValue(){
		return area;
	}
	
	@Override
	public SPLVariable getVariable(){
		return variable;
	}

	@Override
	public String getName() {
		return variable.getName();
	}

	@Override
	public AGeoEntity<? extends IValue> getEntity() {
		return entity;
	}
	
	// -------------------------------------------------- //
	
	@Override
	public String toString() {
		return entity.getGenstarName()+" => ["+getVariable()+" = "+area+"]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(area);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
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
		SPLAreaMatcher other = (SPLAreaMatcher) obj;
		if (Double.doubleToLongBits(area) != Double.doubleToLongBits(other.area))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}
	
	
	
}
