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

import java.io.*;
import java.net.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.descript.GamlXtextException;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.GamlToSyntacticElements;
import msi.gaml.compilation.GamlException;
import msi.gaml.factories.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;

public class GamlJavaValidator extends AbstractGamlJavaValidator {

	// TODO Move the codes of errors to GamlXtextException in order to make the link with
	// QuickFixes

	public static final String QF_NOTFACETOFKEY = "NOTFACETOFKEY";
	public static final String QF_UNKNOWNFACET = "UNKNOWNFACET";
	public static final String QF_KEYHASNOFACET = "KEYHASNOFACET";
	public static final String QF_NOTKEYOFCONTEXT = "NOTKEYOFCONTEXT";
	public static final String QF_NOTKEYOFMODEL = "NOTKEYOFMODEL";
	public static final String QF_INVALIDSETVAR = "INVALIDSETVAR";
	public static final String QF_BADEXPRESSION = "QF_BADEXPRESSION";
	private static Map<Resource, IModel> MODELS = new HashMap();

	private static volatile boolean canRun;
	private static volatile boolean isRunning;

	@Check
	public void checkModel(final Model m) {
		if ( m == null ) { return; }
		ErrorCollector collect = new ErrorCollector();
		System.out.println("Validator checking " + m.getName());
		IModel compiledModel = null;
		Resource r = m.eResource();
		MODELS.remove(r);
		if ( canRun && !isRunning ) {
			isRunning = true;
			try {
				URL url = FileLocator.resolve(new URL(r.getURI().toString()));
				String filePath = new File(url.getFile()).getAbsolutePath();
				Map<String, ISyntacticElement> elements = buildSyntacticTree(r, collect);
				if ( collect.getErrors().isEmpty() ) {
					ModelStructure ms = new ModelStructure(filePath, elements, collect);
					compiledModel =
						(IModel) DescriptionFactory.getModelFactory().compile(ms, collect);
				}
			} catch (Exception e1) {
				System.out.println("An exception has occured in the validation process:");
				e1.printStackTrace();
			} finally {
				isRunning = false;
			}
		}
		if ( collect.getErrors().isEmpty() ) {
			MODELS.put(r, compiledModel);
		} else {
			for ( GamlException e : collect.getErrors() ) {
				if ( e.isWarning() ) {
					warning(e.getMessage(), (EObject) e.getStatement(), null, 0);
				} else {
					error(e.getMessage(), (EObject) e.getStatement(), null, 0);
				}
			}
		}
	}

	public static void validate(final Resource resource) {
		EObject myModel = resource.getContents().get(0);
		Diagnostician.INSTANCE.validate(myModel);
	}

	public static boolean isBuilding() {
		return isRunning;
	}

	public static void canRun(final boolean b) {
		canRun = b;
	}

	public static IModel getCompiledModel(final Resource resource) {
		return MODELS.get(resource);
	}

	public static Map<String, ISyntacticElement> buildSyntacticTree(final Resource r,
		final ErrorCollector collect) {
		Map<String, ISyntacticElement> docs = new HashMap();
		buildRecursiveSyntacticTree(docs, r, collect);
		return docs;
	}

	private static void buildRecursiveSyntacticTree(final Map<String, ISyntacticElement> docs,
		final Resource r, final ErrorCollector collect) {
		Model m = (Model) r.getContents().get(0);
		URL url;
		try {
			url = FileLocator.resolve(new URL(r.getURI().toString()));
		} catch (MalformedURLException e) {
			collect.add(new GamlXtextException(e));
			return;
		} catch (IOException e) {
			collect.add(new GamlXtextException(e));
			return;
		}
		String path = new File(url.getFile()).getAbsolutePath();
		docs.put(path, GamlToSyntacticElements.doConvert(m, collect));
		for ( Import imp : m.getImports() ) {
			String importUri = imp.getImportURI();
			if ( !importUri.startsWith("platform:") ) {
				URI iu = URI.createURI(importUri).resolve(r.getURI());
				if ( iu != null && !iu.isEmpty() && EcoreUtil2.isValidUri(r, iu) ) {
					Resource ir = r.getResourceSet().getResource(iu, true);
					if ( ir != r ) {
						try {
							url = FileLocator.resolve(new URL(ir.getURI().toString()));
						} catch (MalformedURLException e) {
							collect.add(new GamlXtextException(e));
							continue;
						} catch (IOException e) {
							collect.add(new GamlXtextException(e));
							continue;
						}
						path = new File(url.getFile()).getAbsolutePath();
						if ( !docs.containsKey(path) ) {
							buildRecursiveSyntacticTree(docs, ir, collect);
						}
					}
				}
			}
		}
	}

}
