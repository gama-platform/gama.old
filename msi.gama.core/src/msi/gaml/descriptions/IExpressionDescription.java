/*******************************************************************************************************
 *
 * msi.gaml.descriptions.IExpressionDescription.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlable;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The class IExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public interface IExpressionDescription extends IGamlable, IDisposable {

	public abstract void setExpression(final IExpression expr);

	public abstract IExpression compile(final IDescription context);

	public abstract IExpression getExpression();

	public abstract IExpressionDescription compileAsLabel();

	public abstract boolean equalsString(String o);

	public EObject getTarget();

	public void setTarget(EObject target);

	public boolean isConst();

	public Collection<String> getStrings(IDescription context, boolean skills);

	public abstract IExpressionDescription cleanCopy();

	public abstract IType<?> getDenotedType(IDescription context);

	// public abstract void collectMetaInformation(GamlProperties meta);

}