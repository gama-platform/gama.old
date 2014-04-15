/*********************************************************************************************
 * 
 *
 * 'ExperimentHandler.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.experimentHandler;

import java.util.Observable;
import java.util.Observer;

import msi.gaml.expressions.IExpression;

public class ExperimentHandler extends Observable implements IExperimentHandler, IExperimentTrigger {

	@Override
	public void trigger(Object triggedElement, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerTriggedOutput(String name, Observer registredElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getOutputWithName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerTriggedExpression(IExpression exp,
			Observer registredElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getEventWithName(IExpression exp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerTriggedExperimentStatus(Observer registredElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status getSimulationStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
