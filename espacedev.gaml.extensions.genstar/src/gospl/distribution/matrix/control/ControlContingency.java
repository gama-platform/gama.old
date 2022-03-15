package gospl.distribution.matrix.control;

public class ControlContingency extends AControl<Integer> {

	public ControlContingency(Integer control) {
		super(control);
	}

	@Override
	public AControl<Integer> add(AControl<? extends Number> controlCombiner) {
		this.setValue(this.getValue().intValue() + controlCombiner.getValue().intValue());
		return this;
	}
	
	@Override
	public AControl<Integer> add(Integer controlCombiner) {
		this.setValue(this.getValue().intValue() + controlCombiner);
		return this;
	}
	
	@Override
	public AControl<Integer> multiply(AControl<? extends Number> controlMultiplier) {
		this.setValue(Math.round(Math.round(this.getValue().intValue() * controlMultiplier.getValue().doubleValue())));
		return this;
	}

	@Override
	public AControl<Integer> multiply(Integer controlMultiplier) {
		this.setValue(this.getValue().intValue() * controlMultiplier);
		return this;
	}
	
	@Override
	public Integer getSum(AControl<? extends Number> controlSum) {
		return this.getValue().intValue() + controlSum.getValue().intValue();
	}
	
	@Override
	public Integer getDiff(AControl<? extends Number> controlDiff) {
		return this.getValue().intValue() - controlDiff.getValue().intValue();
	}

	@Override
	public Integer getRowProduct(AControl<? extends Number> controlProd) {
		return this.getValue().intValue() * controlProd.getValue().intValue();
	}

	@Override
	public Integer getRoundedProduct(AControl<? extends Number> controlProd) {
		return Math.round(Math.round(this.getValue().intValue() * controlProd.getValue().doubleValue()));
	}

	@Override
	public boolean equalsVal(AControl<Integer> val, double delta) {
		return Math.abs(this.getValue() - val.getValue()) <= this.getValue() * delta;
	}
	
	@Override
	public boolean equalsCastedVal(AControl<? extends Number> val, double delta) {
		return Math.abs(Math.abs(this.getValue() - val.getValue().doubleValue())) <= this.getValue() * delta;
	}

	@Override
	public int compareTo(AControl<Integer> o) {
		if(getValue() > o.getValue())
			return 1;
		if(getValue() < o.getValue())
			return -1;
		if(getValue().equals(o.getValue()) && this.hashCode() != o.hashCode())
			return this.hashCode() < o.hashCode() ? -1 : 1;
		return 0;
	}

}
