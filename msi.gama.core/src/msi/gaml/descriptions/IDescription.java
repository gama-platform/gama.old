/*********************************************************************************************
 *
 * 'IDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

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
 * Written by drogoul Modified on 31 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
public interface IDescription
		extends IGamlDescription, IKeyword, ITyped, IDisposable, IVarDescriptionProvider, IBenchmarkable {

	public static final SymbolSerializer<SymbolDescription> SYMBOL_SERIALIZER = new SymbolSerializer<>();
	public static final VarSerializer VAR_SERIALIZER = new VarSerializer();
	public static final SpeciesSerializer SPECIES_SERIALIZER = new SpeciesSerializer();
	public static final ModelSerializer MODEL_SERIALIZER = new ModelSerializer();
	public static final StatementSerializer STATEMENT_SERIALIZER = new StatementSerializer();
	public static final Function<? super IDescription, ? extends String> TO_NAME = input -> input.getName();
	static final Function<TypeDescription, Class<? extends ISkill>> TO_CLASS = input -> input.getJavaBase();

	@FunctionalInterface
	public static interface DescriptionVisitor<T extends IDescription> extends TObjectProcedure<T> {

		@Override
		default boolean execute(final T desc) {
			return visit(desc);
		}

		public abstract boolean visit(T desc);

	}

	@FunctionalInterface
	public static interface IFacetVisitor
			extends TObjectObjectProcedure<String, IExpressionDescription>, BiConsumer<String, IExpressionDescription> {

		@Override
		default boolean execute(final String name, final IExpressionDescription exp) {
			return visit(name, exp);
		}

		@Override
		default void accept(final String name, final IExpressionDescription exp) {
			visit(name, exp);
		}

		/**
		 * Returns whether or not the visit should continue after this facet
		 * 
		 * @param name
		 * @param exp
		 * @return
		 */
		public abstract boolean visit(String name, IExpressionDescription exp);
	}

	public static final DescriptionVisitor VALIDATING_VISITOR = desc -> {
		if (desc.validate() == null) { return false; }
		return true;

	};

	public static final DescriptionVisitor DISPOSING_VISITOR = desc -> {
		desc.dispose();
		return true;

	};

	public void error(final String message);

	public void error(final String message, String code);

	public void error(final String message, String code, String element, String... data);

	public void error(final String message, String code, EObject element, String... data);

	public void warning(final String message, String code);

	public void warning(final String message, String code, String element, String... data);

	public void warning(final String message, String code, EObject element, String... data);

	public abstract String getKeyword();

	public abstract ModelDescription getModelDescription();

	public abstract SpeciesDescription getSpeciesContext();

	public abstract void setEnclosingDescription(final IDescription desc);

	public abstract EObject getUnderlyingElement(Object facet);

	default EObject getUnderlyingElement() {
		return getUnderlyingElement(null);
	}

	public abstract SymbolProto getMeta();

	public abstract IDescription getEnclosingDescription();

	public abstract IVarDescriptionProvider getDescriptionDeclaringVar(final String name);

	public abstract IDescription getDescriptionDeclaringAction(final String name, boolean superInvocation);

	public abstract Iterable<IDescription> getChildrenWithKeyword(String keyword);

	public abstract Iterable<IDescription> getOwnChildren();

	public default Iterable<IDescription> getChildren() {
		final IDescription enclosing = getEnclosingDescription();
		if (enclosing == null) { return getOwnChildren(); }
		return Iterables.concat(enclosing.getChildren(), getOwnChildren());
	}

	public abstract IDescription getChildWithKeyword(String keyword);

	public abstract IType getTypeNamed(String s);

	public abstract SpeciesDescription getSpeciesDescription(String actualSpecies);

	public abstract ActionDescription getAction(String name);

	public abstract ValidationContext getValidationContext();

	public abstract IDescription copy(IDescription into);

	public abstract IDescription validate();

	public abstract ISymbol compile();

	public int getKind();

	public boolean isBuiltIn();

	public abstract String getOriginName();

	public abstract void setOriginName(String name);

	public abstract void setDefiningPlugin(String plugin);

	public abstract void info(final String s, final String code, final String facet, final String... data);

	public abstract void info(final String s, final String code, final EObject facet, final String... data);

	public abstract void info(final String message, final String code);

	public void resetOriginName();

	public boolean manipulatesVar(final String name);

	public String getLitteral(String name);

	public IExpression getFacetExpr(final String... strings);

	public boolean hasFacet(String until);

	public IExpressionDescription getFacet(String string);

	public IExpressionDescription getFacet(String... strings);

	public void setFacet(String string, IExpressionDescription exp);

	public void setFacet(String item, IExpression exp);

	public void removeFacets(String... strings);

	/**
	 * Returns whether or not the visit has been completed
	 * 
	 * @param visitor
	 * @return
	 */
	public default boolean visitFacets(final IFacetVisitor visitor) {
		return visitFacets(null, visitor);
	}

	@Override
	default String getNameForBenchmarks() {
		final StringBuilder sb = new StringBuilder();
		getSerializer().serializeNoRecursion(sb, this, false);
		return sb.toString();
	}

	public default void collectUsedVarsOf(final IDescription species, final ICollector<VariableDescription> result) {
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

	public boolean visitFacets(Set<String> facets, IFacetVisitor visitor);

	public boolean visitChildren(DescriptionVisitor visitor);

	public boolean visitOwnChildrenRecursively(DescriptionVisitor visitor);

	public boolean visitOwnChildren(DescriptionVisitor visitor);

	// void computeStats(FacetVisitor proc, int[] facetNumber, int[] descWithNoFacets, int[] descNumber);

	public void document(EObject s, IGamlDescription desc);

	public Facets getFacets();

	// public boolean isSynthetic();

	public void attachAlternateVarDescriptionProvider(final IVarDescriptionProvider vp);

	public void replaceChildrenWith(Iterable<IDescription> array);

	public boolean isDocumenting();

	public int getOrder();

	public SymbolSerializer getSerializer();

}