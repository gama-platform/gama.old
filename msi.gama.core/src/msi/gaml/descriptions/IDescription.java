/*********************************************************************************************
 *
 *
 * 'IDescription.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.common.interfaces.ITyped;
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
public interface IDescription extends IGamlDescription, IKeyword, ITyped, IDisposable {

	public static final SymbolSerializer<SymbolDescription> SYMBOL_SERIALIZER = new SymbolSerializer<>();
	public static final VarSerializer VAR_SERIALIZER = new VarSerializer();
	public static final SpeciesSerializer SPECIES_SERIALIZER = new SpeciesSerializer();
	public static final ModelSerializer MODEL_SERIALIZER = new ModelSerializer();
	public static final StatementSerializer STATEMENT_SERIALIZER = new StatementSerializer();
	public static final Function<? super IDescription, ? extends String> TO_NAME = input -> input.getName();

	static final Function<TypeDescription, Class<? extends ISkill>> TO_CLASS = input -> input.getJavaBase();

	public static abstract class DescriptionVisitor<T extends IDescription> implements TObjectProcedure<T> {

		@Override
		public boolean execute(final T desc) {
			return visit(desc);
		}

		public abstract boolean visit(T desc);

	}

	public static abstract class FacetVisitor implements TObjectObjectProcedure<String, IExpressionDescription>,
			BiConsumer<String, IExpressionDescription> {

		@Override
		public final boolean execute(final String name, final IExpressionDescription exp) {
			return visit(name, exp);
		}

		@Override
		public final void accept(final String name, final IExpressionDescription exp) {
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

	public static final DescriptionVisitor VALIDATING_VISITOR = new DescriptionVisitor<IDescription>() {

		@Override
		public boolean visit(final IDescription desc) {
			if (desc.validate() == null) // TODO Verify this.
				return false;
			return true;

		}
	};

	public static final DescriptionVisitor DISPOSING_VISITOR = new DescriptionVisitor<IDescription>() {

		@Override
		public boolean visit(final IDescription desc) {
			desc.dispose();
			return true;

		}
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

	public abstract SymbolProto getMeta();

	public abstract IDescription getEnclosingDescription();

	public abstract IDescription getDescriptionDeclaringVar(final String name);

	public abstract IDescription getDescriptionDeclaringAction(final String name);

	public abstract Iterable<IDescription> getChildrenWithKeyword(String keyword);

	public abstract Iterable<IDescription> getOwnChildren();

	public default Iterable<IDescription> getChildren() {
		final IDescription enclosing = getEnclosingDescription();
		if (enclosing == null)
			return getOwnChildren();
		return Iterables.concat(enclosing.getChildren(), getOwnChildren());
	}

	public abstract IDescription getChildWithKeyword(String keyword);

	/**
	 * If asField is true, then should not return a GlobalVarExpression, but a normal var expression
	 * 
	 * @param name
	 * @param asField
	 * @return
	 */
	public abstract IExpression getVarExpr(final String name, boolean asField);

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

	public boolean hasAttribute(String name);

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
	public default boolean visitFacets(final FacetVisitor visitor) {
		return visitFacets(null, visitor);
	}

	public boolean visitFacets(Set<String> facets, FacetVisitor visitor);

	public boolean visitChildren(DescriptionVisitor visitor);

	public boolean visitOwnChildren(DescriptionVisitor visitor);

	void computeStats(FacetVisitor proc, int[] facetNumber, int[] descWithNoFacets, int[] descNumber);

	public void document(EObject s, IGamlDescription desc);

	public Facets getFacets();

}