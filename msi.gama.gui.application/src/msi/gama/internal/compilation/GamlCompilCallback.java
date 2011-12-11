/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.compilation;

import java.util.Set;
import msi.gama.factories.DescriptionFactory;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.ISymbol;
import msi.gama.kernel.ModelFileManager;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.gaml.descript.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.Convert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Auto Compilation (backgroung, in ValidationJob) called by Xtext validator
 * @author Pierrick
 * 
 */
public class GamlCompilCallback implements IUpdateOnChange {

	protected boolean isActive = false;
	private ISymbol symbol = null;
	private String filePath;

	// http://eclipse.org/articles/Article-Builders/builders.html
	@Override
	public void update(final Resource r) throws Exception {
		isActive = false;
		filePath = Convert.getPath(r);
		// test if the file being updated really being edited
		// FIXME this can freeze GAMA (!)
		// OutputManager.run(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// IEditorPart editor =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// .getActiveEditor();
		// if ( editor == null ) { return; }
		// IEditorInput activeEditorInput = editor.getEditorInput();
		// if ( activeEditorInput instanceof FileEditorInput ) {
		// IFile activeFile = ((FileEditorInput) activeEditorInput).getFile();
		// if ( Convert.getPath(activeFile).equals(filePath) ) {
		// isActive = true;
		// }
		// }
		// } catch (Exception e) {}
		// }
		// });
		// if ( !isActive ) { return; }

		// GUI.debug("Starting validation thread, try to init...");
		if ( init(r) ) {
			symbol = process();
			// GUI.debug("GAMA compile OK : " + symbol);
		}
	}

	public ISymbol getSymbol() {
		return symbol;
	}

	@Override
	public Set<String> getVarContext(final EObject context) throws Exception {
		Resource r = context.eResource();
		if ( Convert.getPath(r).equals(filePath) ) {
			// cf meeting of 10/02/11 with Patrick, here we'll filter the variables
			// get the object behind the "." if there is one, and get a list of variables
			// then return new HashSet containing the available variables
			// Set<String> s = new HashSet<String>();
			// if (symbol != null)
			// IDescription d = symbol.getDescription();
			// d is the GAMA Description of the current model
			if ( context instanceof MemberRefP ) {
				// MemberRefP mrp = (MemberRefP) context;
				// Expression e = mrp.getLeft();
				// e contains the left member expression
				// if ( e instanceof VariableRef ) {
				// String leftVarName = ((VariableRef) e).getRef().getName();
				// }
			} else if ( context instanceof MemberRefR ) {
				// MemberRefR mrr = (MemberRefR) context;
				// Expression e = mrr.getLeft();
				// e contains the left member expression
				// if ( e instanceof VariableRef ) {
				// String leftVarName = ((VariableRef) e).getRef().getName();
				// }
			} else {
				EObject tmp = context;
				while (!(tmp instanceof Statement)) {
					tmp = tmp.eContainer();
				}
				// Statement stm = (Statement) tmp;
				// stm is the statement where the variable is about to be written
			}
			// return s;
		}
		return null;
	}

	public ISymbol getSymbol(final Resource r) throws Exception {
		if ( Convert.getPath(r).equals(filePath) ) { return symbol; }
		return null;
	}

	private boolean init(final Resource r) {
		try {
			// cf r.isModified() r.isTrackingModification()
			// inject XML in kernel after Gaml/Xtext process/convert
			ModelFileManager.getInstance().addDocs(Convert.getDocMap(r));
			return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * process the jdom representation of the in-validation gaml model, kernel parse, send errors
	 * back to the validator
	 * @param r Resource to process
	 * @return true if there was no errors in the compilation
	 * @throws Exception
	 */
	private ISymbol process() throws Exception {
		try {
			// kernel parse & compile
			// GUI.debug("GAMA parsing & compile : " + filePath);
			return DescriptionFactory.getModelFactory().compileFile(filePath);
		} catch (GamlException e) { // we should catch a list of errors...
			// GUI.debug("GAMA error: " + e.getSuperMessage());
			Statement stm = (Statement) e.getStatement();
			if ( stm == null ) {
				GUI.warn("null Statement in auto-compil exception !");
				e.printStackTrace();
			}
			// convert GamlException to GamlDescriptError
			throw new GamlDescriptError("builder: " + e.getSuperMessage(), stm);
		} finally {
			ModelFileManager.getInstance().clearDocs();
		}
	}
}
