/*********************************************************************************************
 *
 *
 * 'IExpressionDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlable;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The class IExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public interface IExpressionDescription extends IGamlable {

	public static interface IExpressionVisitor {

		boolean visit(IExpression exp);
	}

	public abstract void setExpression(final IExpression expr);

	public abstract IExpression compile(final IDescription context);

	public abstract IExpression getExpression();

	public abstract IExpressionDescription compileAsLabel();

	public abstract boolean equalsString(String o);

	public void dispose();

	public EObject getTarget();

	public void setTarget(EObject target);

	public boolean isConst();

	public Set<String> getStrings(IDescription context, boolean skills);

	public abstract IExpressionDescription cleanCopy();

	public abstract IType getDenotedType(IDescription context);

	public abstract void collectMetaInformation(GamlProperties meta);

}