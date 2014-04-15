/*********************************************************************************************
 * 
 * 
 * 'IExpressionCompiler.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.*;
import msi.gaml.types.Signature;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 28 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpressionCompiler<T> {

	public static final List<String> RESERVED = Arrays.asList(IKeyword.THE, IKeyword.FALSE, IKeyword.TRUE,
		IKeyword.NULL, IKeyword.MYSELF, IKeyword.MY, IKeyword.HIS, IKeyword.HER, IKeyword.THEIR, IKeyword.ITS,
		IKeyword.USER_LOCATION);
	public static final List<String> IGNORED = Arrays.asList(IKeyword.THE, IKeyword.THEIR, IKeyword.HIS, IKeyword.ITS,
		IKeyword.HER);
	public static final THashMap<String, Map<Signature, OperatorProto>> OPERATORS = new THashMap();
	public static final Set<String> ITERATORS = new THashSet();

	public abstract IExpression compile(final IExpressionDescription s, final IDescription parsingContext);

	Map<String, IExpressionDescription> parseArguments(StatementDescription action, EObject eObject,
		IDescription context);

	// hqnghi 11/Oct/13 two method for compiling models directly from files
	public abstract ModelDescription createModelDescriptionFromFile(String filepath);

	public abstract IModel createModelFromFile(String filepath);

	// end-hqnghi

	/*
	 * Remove context-dependant information from the parser
	 */
	public abstract void reset();

	/**
	 * @param context
	 * @param facet
	 * @return
	 */
	public abstract EObject getFacetExpression(IDescription context, EObject facet);

}