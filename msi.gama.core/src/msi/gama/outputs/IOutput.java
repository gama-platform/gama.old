/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import msi.gama.common.interfaces.*;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.*;
import msi.gaml.compilation.*;

/**
 * @author drogoul
 */
public interface IOutput extends ISymbol {

	public String getId();

	public void pause();

	public void resume();

	public void open();

	public void close();

	public int getRefreshRate();

	public void setRefreshRate(int rate);

	public boolean isPaused();

	public boolean isOpen();

	public boolean isClosed();

	public boolean isPermanent();

	public void schedule() throws GamaRuntimeException;

	public void setNextTime(int l);

	public long getNextTime();

	public String toGaml();

	public boolean isUserCreated();

	public void setType(String type);

	/*
	 * Called by the scheduler to perform the internal computations
	 */
	public void compute(IScope scope, int cycle) throws GamaRuntimeException;

	/*
	 * Called by the output thread to perform the actual "update" (of views, files, etc.)
	 */
	public void update() throws GamaRuntimeException;

	void prepare(ISimulation sim) throws GamlException, GamaRuntimeException;

	public IScope getStack();

}
