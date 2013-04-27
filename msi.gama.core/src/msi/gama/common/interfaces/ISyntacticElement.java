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
package msi.gama.common.interfaces;

import java.util.List;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul
 * Modified on 18 nov. 2011
 * 
 * An interface to manipulate syntactic elements (either String elements or EObjects)
 * 
 */
public interface ISyntacticElement {

	public static final int IS_GLOBAL = 0;
	public static final int IS_SPECIES = 1;
	public static final int IS_EXPERIMENT = 2;

	String getKeyword();

	IExpressionDescription getFacet(String name);

	String getLabel(String name);

	Facets getFacets();

	void setFacet(String facet, IExpressionDescription expr);

	List<ISyntacticElement> getChildren();

	EObject getElement();

	void setKeyword(String string);

	void addChild(ISyntacticElement element);

	boolean isSynthetic();

	public abstract List<ISyntacticElement> getSpeciesChildren();

	public abstract String getName();

	public abstract void setCategory(final int cat);

	public abstract boolean isExperiment();

	public abstract boolean isGlobal();

	public abstract boolean isSpecies();

}
