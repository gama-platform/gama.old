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
package msi.gama.lang.gaml.validation;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.utils.GamlToSyntacticElements;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator implements IGamlBuilder {

	// private IModel lastModel;
	// private Resource lastResource;
	private volatile boolean isRunning;
	private final Set<IBuilderListener> listeners;

	public GamlJavaValidator() {
		GAMA.setGamlBuilder(this);
		listeners = new HashSet();
	}

	@Override
	public boolean addListener(final IBuilderListener l) {
		return listeners.add(l);
	}

	@Override
	public boolean removeListener(final IBuilderListener l) {
		return listeners.remove(l);
	}

	private void fireBuildStarted(final Resource r) {
		for ( IBuilderListener l : listeners ) {
			l.beforeBuilding(r);
		}
	}

	private void fireBuildEnded(final Model m, final IModel result) {
		// System.out.println("Informing of the end of the build");
		for ( IBuilderListener l : new ArrayList<IBuilderListener>(listeners) ) {
			l.afterBuilding(m.eResource(), result);
		}
	}

	private void waitForContributions() {
		while (!GamaBundleLoader.contributionsLoaded) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void waitForPrevious() {
		while (isRunning) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Check
	public void checkModel(final Model m) {
		if ( !GamaBundleLoader.contributionsLoaded || isRunning ) { return; }
		// waitForContributions();
		// waitForPrevious();
		if ( m == null ) { return; }
		isRunning = true;
		ErrorCollector collect = new ErrorCollector();
		Resource r = m.eResource();
		fireBuildStarted(r);
		IModel lastModel = null;
		try {
			Map<Resource, ISyntacticElement> elements =
				GamlToSyntacticElements.buildSyntacticTree(r, collect);
			if ( !collect.hasErrors() ) {
				System.out.println("No errors in syntactic tree");
				ModelStructure ms = new ModelStructure(r, elements, collect);
				lastModel = (IModel) DescriptionFactory.getModelFactory().compile(ms, collect);
				if ( collect.hasErrors() ) {
					lastModel = null;
					// System.out.println("End compilation of " + m.getName());
				}
			}
		} catch (GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Compilation was aborted");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			collectErrors(collect);
			fireBuildEnded(m, lastModel);
			isRunning = false;
		}
	}

	private void collectErrors(final ErrorCollector collect) {
		for ( GamlCompilationError e : collect.getWarnings() ) {
			warning(e.toString(), (EObject) e.getStatement(), null, 0);
		}
		for ( GamlCompilationError e : collect.getErrors() ) {
			error(e.toString(), (EObject) e.getStatement(), null, 0);
		}
		// if ( collect.hasErrors() ) {
		// lastModel = null;
		// }
	}

	// @Override
	// public IModel build(final Resource r) {
	// // System.out.println("Programmatic call to validation of " +
	// // ((Model) r.getContents().get(0)).getName());
	// EObject myModel = r.getContents().get(0);
	// Diagnostician.INSTANCE.validate(myModel);
	// IModel m = lastModel;
	// // lastResource =
	// // lastModel = null;
	// return m;
	// }
	//
	// /**
	// * @see
	// msi.gama.common.interfaces.IGamlBuilder#getLastBuild(org.eclipse.emf.ecore.resource.Resource)
	// */
	// @Override
	// public IModel getLastBuild(final Resource r) {
	// if ( lastResource == r ) { return lastModel; }
	// return null;
	// }

}
