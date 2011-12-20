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
package msi.gaml.descriptions;

import java.util.List;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlException;
import msi.gaml.expressions.*;
import msi.gaml.types.IType;

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

	public abstract IDescription getModelDescription();

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

	public abstract IExpression getVarExpr(final String name, IExpressionFactory factory);

	public abstract IDescription getSpeciesContext();

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

	public abstract IDescription getSpeciesDescription(String actualSpecies);

	public abstract IDescription getAction(String name);

	public abstract IDescription getWorldSpecies();
}