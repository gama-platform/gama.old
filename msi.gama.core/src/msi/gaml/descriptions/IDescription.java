/*********************************************************************************************
 *
 *
 * 'IDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import msi.gama.common.interfaces.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 31 ao�t 2010
 *
 * @todo Description
 *
 */
public interface IDescription extends IGamlDescription, IKeyword, ITyped, IDisposable, IGamlable {

	public void error(final String message);

	public void error(final String message, String code);

	public void error(final String message, String code, String element, String ... data);

	public void error(final String message, String code, EObject element, String ... data);

	public void warning(final String message, String code);

	public void warning(final String message, String code, String element, String ... data);

	public void warning(final String message, String code, EObject element, String ... data);

	public abstract String getKeyword();

	public abstract ModelDescription getModelDescription();

	public abstract void setEnclosingDescription(final IDescription desc);

	public abstract EObject getUnderlyingElement(Object facet);

	public abstract SymbolProto getMeta();

	public abstract Facets getFacets();

	public abstract IDescription getEnclosingDescription();

	public abstract IDescription getDescriptionDeclaringVar(final String name);

	public abstract IDescription getDescriptionDeclaringAction(final String name);
	
	public abstract Iterable<IDescription> getChildrenWithKeyword(String keyword);
	
	public abstract IDescription getChildWithKeyword(String keyword);

	public abstract IExpression getVarExpr(final String name);

	public abstract SpeciesDescription getSpeciesContext();

	public abstract IExpression addTemp(IDescription declaration, final String name, final IType type);

	public abstract List<IDescription> getChildren();

	public abstract void addChildren(List<IDescription> children);

	public abstract IDescription addChild(IDescription child);

	public abstract IType getTypeNamed(String s);

	public abstract void copyTempsAbove();

	public abstract SpeciesDescription getSpeciesDescription(String actualSpecies);

	public abstract StatementDescription getAction(String name);

	public abstract ErrorCollector getErrorCollector();

	public abstract IDescription copy(IDescription into);

	public abstract IDescription validate();

	public abstract ISymbol compile();

	public int getKind();

	public boolean isBuiltIn();

	public abstract String getOriginName();

	public abstract void setOriginName(String name);

	public abstract void setDefiningPlugin(String plugin);

	public abstract void info(final String s, final String code, final String facet, final String ... data);

	public abstract void info(final String s, final String code, final EObject facet, final String ... data);

	public abstract void info(final String message, final String code);

	public void resetOriginName();

	public boolean isDocumenting();

}