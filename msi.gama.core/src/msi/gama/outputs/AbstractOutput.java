/*********************************************************************************************
 * 
 *
 * 'AbstractOutput.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
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

	private IScope scope;
	boolean paused = false;
	boolean open = false;
	private Integer nextRefreshTime;
	private int refreshRate;
	//hqnghi: identify experiment name 
	private String expName = "";
	
	public String getExpName() {
		return expName;
	}

	public void setExpName(String expName) {
		this.expName = expName;
	}

	//end-hqnghi
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

	private boolean isUserCreated = true;

	@Override
	public final boolean isUserCreated() {
		return isUserCreated;
	}

	@Override
	public final void setUserCreated(final boolean isUserCreated) {
		this.isUserCreated = isUserCreated;
	}

	@Override
	public boolean init(final IScope scope) {
//		setScope(scope.copy());
		//hqnghi: if experiment is not blank, it mean that output is come frome other experiment 
		if (expName.equals("")) {
			setScope(scope.copy());
		}else {
			setScope(GAMA.getController(expName).getExperiment()
					.getAgent()
					.getSimulation().getScope());
		}
		// GuiUtils.informConsole("scope " + expName);
		//end-hqnghi
		final IExpression refresh = getFacet(IKeyword.REFRESH_EVERY);
		if ( refresh != null ) {
			setRefreshRate(Cast.asInt(getScope(), refresh.value(getScope())));
		}
		setNextTime(getScope().getClock().getCycle());
		return true;
	}

	@Override
	public void close() {
		pause();
		setOpen(false);
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
	public abstract boolean step(IScope scope);

	@Override
	public abstract void update() throws GamaRuntimeException;

	void setOpen(final boolean open) {
		this.open = open;
	}

	public void setPaused(final boolean suspended) {
		paused = suspended;
	}

	private void reschedule() {
		setNextTime(getScope().getClock().getCycle() + getRefreshRate());
	}

	@Override
	public void setNextTime(final Integer i) {
		nextRefreshTime = i;
	}

	@Override
	public long getNextTime() {
		return nextRefreshTime;
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



	public void setScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public IScope getScope() {
		return scope;
	}

}
