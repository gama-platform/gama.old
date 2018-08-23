/*********************************************************************************************
 *
 * IDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;

import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.common.interfaces.ITyped;
import msi.gama.util.ICollector;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.SymbolSerializer.ModelSerializer;
import msi.gaml.descriptions.SymbolSerializer.SpeciesSerializer;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.descriptions.SymbolSerializer.VarSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 31 ao�t 2010.
 *
 * @todo Description
 */
@SuppressWarnings ({ "rawtypes" })
public interface IDescription
		extends IGamlDescription, IKeyword, ITyped, IDisposable, IVarDescriptionProvider, IBenchmarkable {

	/**
	 * The Constant SYMBOL_SERIALIZER.
	 */
	public static final SymbolSerializer<SymbolDescription> SYMBOL_SERIALIZER = new SymbolSerializer<>();

	/**
	 * The Constant VAR_SERIALIZER.
	 */
	public static final VarSerializer VAR_SERIALIZER = new VarSerializer();

	/**
	 * The Constant SPECIES_SERIALIZER.
	 */
	public static final SpeciesSerializer SPECIES_SERIALIZER = new SpeciesSerializer();

	/**
	 * The Constant MODEL_SERIALIZER.
	 */
	public static final ModelSerializer MODEL_SERIALIZER = new ModelSerializer();

	/**
	 * The Constant STATEMENT_SERIALIZER.
	 */
	public static final StatementSerializer STATEMENT_SERIALIZER = new StatementSerializer();

	/**
	 * The Constant TO_NAME.
	 */
	public static final Function<? super IDescription, ? extends String> TO_NAME = input -> input.getName();

	/**
	 * The Constant TO_CLASS.
	 */
	static final Function<TypeDescription, Class<? extends ISkill>> TO_CLASS = input -> input.getJavaBase();

	/**
	 * The Interface DescriptionVisitor.
	 *
	 * @param <T>
	 *            the generic type
	 */
	@FunctionalInterface
	public static interface DescriptionVisitor<T extends IDescription> extends TObjectProcedure<T> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see gnu.trove.procedure.TObjectProcedure#execute(java.lang.Object)
		 */
		@Override
		default boolean execute(final T desc) {
			return visit(desc);
		}

		/**
		 * Visit.
		 *
		 * @param desc
		 *            the desc
		 * @return true, if successful
		 */
		public abstract boolean visit(T desc);

	}

	/**
	 * The Interface IFacetVisitor.
	 */
	@FunctionalInterface
	public static interface IFacetVisitor
			extends TObjectObjectProcedure<String, IExpressionDescription>, BiConsumer<String, IExpressionDescription> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see gnu.trove.procedure.TObjectObjectProcedure#execute(java.lang.Object, java.lang.Object)
		 */
		@Override
		default boolean execute(final String name, final IExpressionDescription exp) {
			return visit(name, exp);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
		 */
		@Override
		default void accept(final String name, final IExpressionDescription exp) {
			visit(name, exp);
		}

		/**
		 * Returns whether or not the visit should continue after this facet.
		 *
		 * @param name
		 *            the name
		 * @param exp
		 *            the exp
		 * @return true, if successful
		 */
		public abstract boolean visit(String name, IExpressionDescription exp);
	}

	/**
	 * The Constant VALIDATING_VISITOR.
	 */
	public static final DescriptionVisitor VALIDATING_VISITOR = desc -> {
		if (desc.validate() == null) { return false; }
		return true;

	};

	/**
	 * The Constant DISPOSING_VISITOR.
	 */
	public static final DescriptionVisitor DISPOSING_VISITOR = desc -> {
		desc.dispose();
		return true;

	};

	/**
	 * Error.
	 *
	 * @param message
	 *            the message
	 */
	public void error(final String message);

	/**
	 * Error.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 */
	public void error(final String message, String code);

	/**
	 * Error.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 * @param element
	 *            the element
	 * @param data
	 *            the data
	 */
	public void error(final String message, String code, String element, String... data);

	/**
	 * Error.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 * @param element
	 *            the element
	 * @param data
	 *            the data
	 */
	public void error(final String message, String code, EObject element, String... data);

	/**
	 * Warning.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 */
	public void warning(final String message, String code);

	/**
	 * Warning.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 * @param element
	 *            the element
	 * @param data
	 *            the data
	 */
	public void warning(final String message, String code, String element, String... data);

	/**
	 * Warning.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 * @param element
	 *            the element
	 * @param data
	 *            the data
	 */
	public void warning(final String message, String code, EObject element, String... data);

	/**
	 * Gets the keyword.
	 *
	 * @return the keyword
	 */
	public abstract String getKeyword();

	/**
	 * Gets the model description.
	 *
	 * @return the model description
	 */
	public abstract ModelDescription getModelDescription();

	/**
	 * Gets the species context.
	 *
	 * @return the species context
	 */
	public abstract SpeciesDescription getSpeciesContext();

	/**
	 * Sets the enclosing description.
	 *
	 * @param desc
	 *            the new enclosing description
	 */
	public abstract void setEnclosingDescription(final IDescription desc);

	/**
	 * Gets the underlying element.
	 *
	 * @param facet
	 *            the facet
	 * @return the underlying element
	 */
	public abstract EObject getUnderlyingElement(Object facet);

	/**
	 * Gets the underlying element.
	 *
	 * @return the underlying element
	 */
	default EObject getUnderlyingElement() {
		return getUnderlyingElement(null);
	}

	/**
	 * Gets the meta.
	 *
	 * @return the meta
	 */
	public abstract SymbolProto getMeta();

	/**
	 * Gets the enclosing description.
	 *
	 * @return the enclosing description
	 */
	public abstract IDescription getEnclosingDescription();

	/**
	 * Gets the description declaring var.
	 *
	 * @param name
	 *            the name
	 * @return the description declaring var
	 */
	public abstract IVarDescriptionProvider getDescriptionDeclaringVar(final String name);

	/**
	 * Gets the description declaring action.
	 *
	 * @param name
	 *            the name
	 * @param superInvocation
	 *            the super invocation
	 * @return the description declaring action
	 */
	public abstract IDescription getDescriptionDeclaringAction(final String name, boolean superInvocation);

	/**
	 * Gets the children with keyword.
	 *
	 * @param keyword
	 *            the keyword
	 * @return the children with keyword
	 */
	public abstract Iterable<IDescription> getChildrenWithKeyword(String keyword);

	/**
	 * Gets the own children.
	 *
	 * @return the own children
	 */
	public abstract Iterable<IDescription> getOwnChildren();

	/**
	 * Gets the child with keyword.
	 *
	 * @param keyword
	 *            the keyword
	 * @return the child with keyword
	 */
	public abstract IDescription getChildWithKeyword(String keyword);

	/**
	 * Gets the type named.
	 *
	 * @param s
	 *            the s
	 * @return the type named
	 */
	public abstract IType getTypeNamed(String s);

	/**
	 * Gets the species description.
	 *
	 * @param actualSpecies
	 *            the actual species
	 * @return the species description
	 */
	public abstract SpeciesDescription getSpeciesDescription(String actualSpecies);

	/**
	 * Gets the action.
	 *
	 * @param name
	 *            the name
	 * @return the action
	 */
	public abstract ActionDescription getAction(String name);

	/**
	 * Gets the validation context.
	 *
	 * @return the validation context
	 */
	public abstract ValidationContext getValidationContext();

	/**
	 * Copy.
	 *
	 * @param into
	 *            the into
	 * @return the i description
	 */
	public abstract IDescription copy(IDescription into);

	/**
	 * Validate.
	 *
	 * @return the i description
	 */
	public abstract IDescription validate();

	/**
	 * Compile.
	 *
	 * @return the i symbol
	 */
	public abstract ISymbol compile();

	/**
	 * Gets the kind.
	 *
	 * @return the kind
	 */
	public int getKind();

	/**
	 * Checks if is built in.
	 *
	 * @return true, if is built in
	 */
	public boolean isBuiltIn();

	/**
	 * Gets the origin name.
	 *
	 * @return the origin name
	 */
	public abstract String getOriginName();

	/**
	 * Sets the origin name.
	 *
	 * @param name
	 *            the new origin name
	 */
	public abstract void setOriginName(String name);

	/**
	 * Sets the defining plugin.
	 *
	 * @param plugin
	 *            the new defining plugin
	 */
	public abstract void setDefiningPlugin(String plugin);

	/**
	 * Info.
	 *
	 * @param s
	 *            the s
	 * @param code
	 *            the code
	 * @param facet
	 *            the facet
	 * @param data
	 *            the data
	 */
	public abstract void info(final String s, final String code, final String facet, final String... data);

	/**
	 * Info.
	 *
	 * @param s
	 *            the s
	 * @param code
	 *            the code
	 * @param facet
	 *            the facet
	 * @param data
	 *            the data
	 */
	public abstract void info(final String s, final String code, final EObject facet, final String... data);

	/**
	 * Info.
	 *
	 * @param message
	 *            the message
	 * @param code
	 *            the code
	 */
	public abstract void info(final String message, final String code);

	/**
	 * Reset origin name.
	 */
	public void resetOriginName();

	/**
	 * Manipulates var.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean manipulatesVar(final String name);

	/**
	 * Gets the litteral.
	 *
	 * @param name
	 *            the name
	 * @return the litteral
	 */
	public String getLitteral(String name);

	/**
	 * Gets the facet expr.
	 *
	 * @param strings
	 *            the strings
	 * @return the facet expr
	 */
	public IExpression getFacetExpr(final String... strings);

	/**
	 * Checks for facet.
	 *
	 * @param until
	 *            the until
	 * @return true, if successful
	 */
	public boolean hasFacet(String until);

	/**
	 * Gets the facet.
	 *
	 * @param string
	 *            the string
	 * @return the facet
	 */
	public IExpressionDescription getFacet(String string);

	/**
	 * Gets the facet.
	 *
	 * @param strings
	 *            the strings
	 * @return the facet
	 */
	public IExpressionDescription getFacet(String... strings);

	/**
	 * Sets the facet.
	 *
	 * @param string
	 *            the string
	 * @param exp
	 *            the exp
	 */
	public void setFacet(String string, IExpressionDescription exp);

	/**
	 * Sets the facet.
	 *
	 * @param item
	 *            the item
	 * @param exp
	 *            the exp
	 */
	public void setFacet(String item, IExpression exp);

	/**
	 * Removes the facets.
	 *
	 * @param strings
	 *            the strings
	 */
	public void removeFacets(String... strings);

	/**
	 * Returns whether or not the visit has been completed.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public default boolean visitFacets(final IFacetVisitor visitor) {
		return visitFacets(null, visitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.interfaces.IBenchmarkable#getNameForBenchmarks()
	 */
	@Override
	default String getNameForBenchmarks() {
		final StringBuilder sb = new StringBuilder();
		getSerializer().serializeNoRecursion(sb, this, false);
		return sb.toString();
	}

	/**
	 * Collect used vars of.
	 *
	 * @param species
	 *            the species
	 * @param result
	 *            the result
	 */
	public default void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<VariableDescription> result) {
		this.visitFacets((name, exp) -> {
			final IExpression expression = exp.getExpression();
			if (expression != null) {
				expression.collectUsedVarsOf(species, result);
			}
			return true;
		});
		this.visitOwnChildren(desc -> {
			desc.collectUsedVarsOf(species, result);
			return true;
		});
	}

	/**
	 * Visit facets.
	 *
	 * @param facets
	 *            the facets
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean visitFacets(Set<String> facets, IFacetVisitor visitor);

	/**
	 * Visit children.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean visitChildren(DescriptionVisitor visitor);

	/**
	 * Visit own children recursively.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean visitOwnChildrenRecursively(DescriptionVisitor visitor);

	/**
	 * Visit own children.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean visitOwnChildren(DescriptionVisitor visitor);

	/**
	 * Document.
	 *
	 * @param s
	 *            the s
	 * @param desc
	 *            the desc
	 */
	public void document(EObject s, IGamlDescription desc);

	/**
	 * Gets the facets.
	 *
	 * @return the facets
	 */
	public Facets getFacets();

	/**
	 * Attach alternate var description provider.
	 *
	 * @param vp
	 *            the vp
	 */
	public void attachAlternateVarDescriptionProvider(final IVarDescriptionProvider vp);

	/**
	 * Replace children with.
	 *
	 * @param array
	 *            the array
	 */
	public void replaceChildrenWith(Iterable<IDescription> array);

	/**
	 * Checks if is documenting.
	 *
	 * @return true, if is documenting
	 */
	public boolean isDocumenting();

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder();

	/**
	 * Gets the serializer.
	 *
	 * @return the serializer
	 */
	public SymbolSerializer getSerializer();

}