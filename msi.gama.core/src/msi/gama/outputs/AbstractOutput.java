/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class AbstractOutput.
 * 
 * @author drogoul
 */
@inside(symbols = IKeyword.OUTPUT)
public abstract class AbstractOutput extends Symbol implements IOutput {

	protected IOutputManager outputManager;
	private IScope scope;
	boolean paused = false;
	private boolean isPermanent = false;
	boolean open = false;
	private int nextRefreshTime;
	private int refreshRate;

	public AbstractOutput(final IDescription desc) {
		super(desc);
		name = getLiteral(IKeyword.NAME);
		if ( name != null ) {
			name = name.replace(':', '_').replace('/', '_').replace('\\', '_');
			if ( name.length() == 0 ) {
				name = "output";
			}
		}
		setRefreshRate(1);
	}

	@Override
	public void init(final IScope scope) {
		setScope(scope.copy());
		outputManager = GAMA.getExperiment().getOutputManager();
		IExpression refresh = getFacet(IKeyword.REFRESH_EVERY);
		if ( refresh != null ) {
			setRefreshRate(Cast.asInt(getScope(), refresh.value(getScope())));
		}
		setNextTime(getScope().getClock().getCycle());
	}

	@Override
	public void close() {
		pause();
		setOpen(false);
	}

	@Override
	public boolean isClosed() {
		return !isOpen();
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void open() {
		setOpen(true);
	}

	@Override
	public void pause() {
		setPaused(true);
	}

	@Override
	public void resume() {
		setPaused(false);
		reschedule();
	}

	@Override
	public int getRefreshRate() {
		return refreshRate;
	}

	@Override
	public void setRefreshRate(final int refresh) {
		refreshRate = refresh;
	}

	@Override
	public abstract void step(IScope scope);

	@Override
	public abstract void update() throws GamaRuntimeException;

	void setOpen(final boolean open) {
		this.open = open;
	}

	public void setPaused(final boolean suspended) {
		paused = suspended;
	}

	@Override
	public void schedule() throws GamaRuntimeException {
		setNextTime(getScope().getClock().getCycle());
		outputManager.scheduleOutput(this);
	}

	private void reschedule() {
		setNextTime(getScope().getClock().getCycle() + getRefreshRate());
	}

	@Override
	public void setNextTime(final int i) {
		nextRefreshTime = i;
	}

	@Override
	public long getNextTime() {
		return nextRefreshTime;
	}

	@Override
	public boolean isPermanent() {
		return isPermanent;
	}

	public void setPermanent(final boolean batchOutput) {
		isPermanent = batchOutput;
	}

	@Override
	public String toGaml() {
		// To redefine for outputs that need to be saved
		return null;
	}

	@Override
	public boolean isUserCreated() {
		// To redefine for outputs that need to be maintained
		return false;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {

	}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getId() {
		return getName(); // by default
	}

	protected void setScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(scope);
		}
		this.scope = scope;
	}

	@Override
	public IScope getScope() {
		return scope;
	}

}
