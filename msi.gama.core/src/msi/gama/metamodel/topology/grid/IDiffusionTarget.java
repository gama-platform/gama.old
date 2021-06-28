package msi.gama.metamodel.topology.grid;

import msi.gama.runtime.IScope;

public interface IDiffusionTarget {

	int getCols(IScope scope);

	int getRows(IScope scope);

	int getNbNeighbours();

	double getValueAtIndex(IScope scope, int i, String var_diffu);

	void setValueAtIndex(IScope scope, int i, String var_diffu, double valToPut);

	void getValuesInto(IScope scope, String varName, double minValue, double[] input);

}
