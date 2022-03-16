/*******************************************************************************************************
 *
 * AbstractLayerStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * GAML statement to define the properties of a layer in a display
 *
 * @todo Description
 *
 */
@inside (
		symbols = IKeyword.DISPLAY)
public abstract class AbstractLayerStatement extends Symbol implements ILayerStatement {

	/**
	 * The Class SpeciesLayerValidator.
	 */
	public static class OpenGLSpecificLayerValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Warn if not open GL.
		 *
		 * @param description
		 *            the description
		 */
		void warnIfNotOpenGL(final StatementDescription layer) {
			IDescription d = layer.getEnclosingDescription();
			if (!isOpenGL(d)) {
				layer.warning(layer.getKeyword() + " layers can only be used in OpenGL displays",
						IGamlIssue.WRONG_TYPE);
			}
		}

		/**
		 * Checks if is open GL.
		 *
		 * @param d
		 *            the d
		 * @return true, if is open GL
		 */
		private boolean isOpenGL(final IDescription d) {
			// Are we in OpenGL world ?
			IDescription display = d;
			final String type = display.getLitteral(TYPE);
			final boolean isOpenGLDefault = !"Java2D".equals(GamaPreferences.Displays.CORE_DISPLAY.getValue());

			if (type != null) return LayeredDisplayData.OPENGL.equals(type);
			final String parent = display.getLitteral(PARENT);
			if (parent == null) return isOpenGLDefault;
			display = StreamEx.of(display.getEnclosingDescription().getChildrenWithKeyword(DISPLAY).iterator())
					.findFirst(dspl -> dspl.getName().equals(parent)).get();
			if (display == null) return isOpenGLDefault;
			return isOpenGL(display);
		}

		@Override
		public void validate(final StatementDescription description) {
			warnIfNotOpenGL(description);
		}

	}

	/** The output. */
	LayeredDisplayOutput output;

	/**
	 * Checks if is to create.
	 *
	 * @return true, if is to create
	 */
	public boolean isToCreate() { return true; }

	/**
	 * Instantiates a new abstract layer statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public AbstractLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(desc.getName());
	}

	@Override
	public IExpression getRefreshFacet() { return getFacet(IKeyword.REFRESH); }

	@Override
	public int compareTo(final ILayerStatement o) {
		return Ints.compare(getOrder(), o.getOrder());
	}

	@Override
	public final boolean init(final IScope scope) {
		return _init(scope);
	}

	/**
	 * Inits the.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	protected abstract boolean _init(IScope scope);

	@Override
	public void setDisplayOutput(final IDisplayOutput out) { output = (LayeredDisplayOutput) out; }

	/**
	 * Gets the display output.
	 *
	 * @return the display output
	 */
	public LayeredDisplayOutput getDisplayOutput() { return output; }

	/**
	 * Gets the layered display data.
	 *
	 * @return the layered display data
	 */
	public LayeredDisplayData getLayeredDisplayData() {
		if (output == null) return null;
		return output.getData();
	}

	@Override
	public final boolean step(final IScope scope) throws GamaRuntimeException {
		if (!scope.interrupted()) return _step(scope);
		return false;
	}

	/**
	 * Step.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	protected abstract boolean _step(IScope scope);

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

}
