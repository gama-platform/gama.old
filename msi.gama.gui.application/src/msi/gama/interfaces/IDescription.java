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
package msi.gama.interfaces;

import java.util.List;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;

/**
 * Written by drogoul Modified on 31 aožt 2010
 * 
 * @todo Description
 * 
 */
public interface IDescription {

	public abstract String getKeyword();

	public abstract String getName();

	public abstract IType getType();

	public abstract IType getContentType();

	public abstract ModelDescription getModelDescription();

	public abstract void setSuperDescription(final IDescription desc) throws GamlException;

	public abstract ISyntacticElement getSourceInformation();

	public abstract Facets getFacets();

	public abstract IDescription getSuperDescription();

	public abstract IDescription getDescriptionDeclaringVar(final String name); // TODO MOVE TO
																				// ExecutionContextDescription

	public abstract IDescription getDescriptionDeclaringAction(final String name); // TODO MOVE TO
																					// ExecutionContextDescription

	public abstract IDescription getDescriptionDeclaringAspect(final String name); // TODO MOVE TO
																					// ExecutionContextDescription

	public abstract IExpression getVarExpr(final String name, IExpressionFactory f);

	public abstract SpeciesDescription getSpeciesContext();

	public abstract IExpression addTemp(final String name, final IType type,
		final IType contentType, IExpressionFactory f);

	public abstract IDescription shallowCopy(final IDescription superDescription)
		throws GamlException;

	public abstract void dispose();

	public abstract List<IDescription> getChildren();

	public abstract void addChildren(List<IDescription> children) throws GamlException;

	public abstract IDescription addChild(IDescription child) throws GamlException;

	public abstract IType getTypeOf(String s);

	public abstract void copyTempsAbove();
}