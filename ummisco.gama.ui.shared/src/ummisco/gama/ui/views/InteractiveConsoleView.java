/*******************************************************************************************************
 *
 * InteractiveConsoleView.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.internal.console.IOConsoleViewer;

import msi.gama.application.workbench.ThemeHelper;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlIdiomsProvider;
import msi.gaml.descriptions.IVarDescriptionProvider;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * The Class InteractiveConsoleView.
 */
public class InteractiveConsoleView extends GamaViewPart implements IToolbarDecoratedView.Sizable,
		IToolbarDecoratedView.LogExportable, IGamaView.Console, IExecutionContext, IVarDescriptionProvider {

	/** The msg console. */
	private IOConsole msgConsole;

	/** The viewer. */
	IOConsoleViewer viewer;

	/** The error writer. */
	private OutputStreamWriter resultWriter, errorWriter;

	/** The reader. */
	BufferedReader reader;

	/** The scope. */
	private IScope scope;

	/** The temps. */
	private final Map<String, Object> temps = new LinkedHashMap<>();

	/** The history. */
	private final List<String> history = new ArrayList<>();

	/** The index in history. */
	private int indexInHistory = 0;

	/** The control to display in full screen. */
	private Composite controlToDisplayInFullScreen;

	/** The parent of control to display full screen. */
	private Composite parentOfControlToDisplayFullScreen;

	@Override
	public void createPartControl(final Composite composite) {
		setParentOfControlToDisplayFullScreen(composite);
		final var layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		getParentOfControlToDisplayFullScreen().setLayout(layout);
		controlToDisplayInFullScreen = new Composite(composite, SWT.BORDER);
		controlToDisplayInFullScreen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		super.createPartControl(controlToDisplayInFullScreen);

	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		// Issue #2816
		this.setTitleImage(GamaIcon.named(IGamaIcons.VIEW_INTERACTIVE).image());
	}

	@Override
	public IScope getScope() { return scope; }

	@Override
	public void ownCreatePartControl(final Composite p) {
		msgConsole = new IOConsole("GAMA Console", null);
		reader = new BufferedReader(new InputStreamReader(msgConsole.getInputStream()));
		var stream = msgConsole.newOutputStream();
		stream.setColor(ThemeHelper.isDark() ? IGamaColors.NEUTRAL.lighter() : IGamaColors.NEUTRAL.color());
		// stream.setFontStyle(SWT.ITALIC);
		resultWriter = new OutputStreamWriter(stream);
		stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.ERROR.color());
		errorWriter = new OutputStreamWriter(stream);
		viewer = new IOConsoleViewer(p, msgConsole);

		viewer.setWordWrap(true);

		msgConsole.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(final DocumentEvent event) {
				if (Strings.LN.equals(event.getText())) {
					var textEntered = "";
					try {
						textEntered = reader.readLine();
					} catch (final IOException e) {
						append("Error in reading command", true, true);
						return;
					}
					processInput(textEntered);
				}
			}

			@Override
			public void documentAboutToBeChanged(final DocumentEvent event) {

			}
		});

		viewer.getTextWidget().addVerifyKeyListener(e -> {
			if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
				final var text = (StyledText) e.widget;
				final var selection = text.getSelection();
				final var line = text.getLineAtOffset(selection.y);
				if (line == text.getLineCount() - 1) {
					e.doit = false;
					insertHistory(e.keyCode == SWT.ARROW_UP);

				}
			}
		});
		p.layout(true, true);
		showPrompt();

	}

	/**
	 * Gets the control to display in full screen.
	 *
	 * @return the control to display in full screen
	 */
	public Composite getControlToDisplayInFullScreen() { return controlToDisplayInFullScreen; }

	/** The Constant PROMPT. */
	public static final String PROMPT = "gaml> ";

	/**
	 * Show prompt.
	 */
	private void showPrompt() {

		new Thread(() -> {
			append(Strings.LN + PROMPT, false, false);
			try {
				// Wait for the output stream to finish
				Thread.sleep(200);
			} catch (final InterruptedException e) {}
			WorkbenchHelper.run(() -> {
				if (viewer != null && viewer.getTextWidget() != null && !viewer.getTextWidget().isDisposed()) {
					viewer.getTextWidget().setCaretOffset(viewer.getTextWidget().getCharCount());
				}

			});

		}).start();

	}

	/**
	 * Insert history.
	 *
	 * @param back
	 *            the back
	 */
	private void insertHistory(final boolean back) {

		if (history.size() == 0) {
			ViewsHelper.requestUserAttention(this, "No history");
			return;
		}
		if (indexInHistory <= 0) {
			if (back) { ViewsHelper.requestUserAttention(this, "No more history"); }
			indexInHistory = 0;
		} else if (indexInHistory >= history.size() - 1) {
			if (!back) { ViewsHelper.requestUserAttention(this, "No more history"); }
			indexInHistory = history.size() - 1;
		}
		try {
			final var text = viewer.getTextWidget();
			final var lineIndex = text.getLineCount() - 1;
			final var nbChars = text.getCharCount();
			final var firstOffset = text.getOffsetAtLine(lineIndex) + PROMPT.length();
			viewer.getDocument().replace(firstOffset, nbChars - firstOffset, history.get(indexInHistory));
			text.setCaretOffset(text.getCharCount());
			if (back) {
				indexInHistory--;
			} else {
				indexInHistory++;
			}
		} catch (final org.eclipse.jface.text.BadLocationException e1) {}

	}

	/**
	 * Append the text to the console.
	 *
	 * @param text
	 *            to display in the console
	 */
	public void append(final String text, final boolean error, final boolean showPrompt) {

		WorkbenchHelper.asyncRun(() -> {
			@SuppressWarnings ("resource") final var writer = error ? errorWriter : resultWriter;
			try {
				writer.append(text);
				writer.flush();
				if (showPrompt) { showPrompt(); }
			} catch (final IOException e) {}

		});

	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		reset();
		super.widgetDisposed(e);
	}

	@Override
	public void close(final IScope scope) {
		reset();
		super.close(scope);
	}

	@Override
	public void reset() {
		if (msgConsole != null) { msgConsole.clearConsole(); }
		setExecutorAgent(null);
		showPrompt();
	}

	@Override
	public Control getSizableFontControl() {
		if (viewer == null) return null;
		return viewer.getTextWidget();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.ACTION_CLEAR, "Clear", "Clear the console", e -> {
			msgConsole.clearConsole();
			showPrompt();
		}, SWT.RIGHT);

	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return null;
	}

	/**
	 * As ConsoleView is automatically opened by moving to the simulation perspective, the automatic closing can cause
	 * problems. So the view is stated as accepting an "experiment-less" mode. See Issue #1361 Method
	 * shouldBeClosedWhenNoExperiments()
	 *
	 * @see ummisco.gama.ui.views.GamaViewPart#shouldBeClosedWhenNoExperiments()
	 */
	@Override
	protected boolean shouldBeClosedWhenNoExperiments() {
		return false;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void append(final String text, final ITopLevelAgent agent, final GamaColor color) {
		setExecutorAgent(agent);
		if (text != null) { append(text, false, true); }
	}

	/**
	 * Sets the executor agent.
	 *
	 * @param agent
	 *            the new executor agent
	 */
	private void setExecutorAgent(final ITopLevelAgent agent) {
		if (scope != null) {
			scope.clear();
			scope = null;
		}
		if (agent == null) {

			WorkbenchHelper.asyncRun(
					() -> { if (toolbar != null && !toolbar.isDisposed()) { toolbar.wipe(SWT.LEFT, true); } });
		} else {
			scope = new ExecutionScope(agent, " in console", this);
			agent.getSpecies().getDescription().attachAlternateVarDescriptionProvider(this);
			WorkbenchHelper.asyncRun(() -> toolbar.status(GamaIcon.named(IGamaIcons.MENU_AGENT).image(),
					"Listening agent: " + Cast.toGaml(agent), IGamaColors.NEUTRAL, SWT.LEFT));
		}

	}

	/**
	 * Process input.
	 *
	 * @param s
	 *            the s
	 */
	protected void processInput(final String s) {
		final var agent = getListeningAgent();
		if (agent == null || agent.dead()) {
			setExecutorAgent(null);
		} else {
			final var entered = s.trim();
			history.add(entered);
			indexInHistory = history.size() - 1;
			String result = null;
			var error = false;
			if (entered.startsWith("?")) {
				result = GamlIdiomsProvider.getDocumentationOn(entered.substring(1));
			} else {
				try {
					final var expr = GAML.compileExpression(s, agent, this, false);
					if (expr != null) { result = StringUtils.toGaml(scope.evaluate(expr, agent).getValue(), true); }
				} catch (final Exception e) {
					error = true;
					result = "> Error: " + e.getMessage();
				} finally {
					agent.getSpecies().removeTemporaryAction();
				}
			}
			if (result == null) { result = "nil"; }
			append(result, error, true);
			if (!error && GAMA.getExperiment() != null) { GAMA.getExperiment().refreshAllOutputs(); }
		}

	}

	/**
	 * Gets the parent of control to display full screen.
	 *
	 * @return the parent of control to display full screen
	 */
	public Composite getParentOfControlToDisplayFullScreen() { return parentOfControlToDisplayFullScreen; }

	/**
	 * Sets the parent of control to display full screen.
	 *
	 * @param parentOfControlToDisplayFullScreen
	 *            the new parent of control to display full screen
	 */
	public void setParentOfControlToDisplayFullScreen(final Composite parentOfControlToDisplayFullScreen) {
		this.parentOfControlToDisplayFullScreen = parentOfControlToDisplayFullScreen;
	}

	/**
	 * Gets the listening agent.
	 *
	 * @return the listening agent
	 */
	private IAgent getListeningAgent() {
		if (scope == null) {
			setExecutorAgent(GAMA.getPlatformAgent());
			return GAMA.getPlatformAgent();
		}
		ITopLevelAgent agent = scope.getRoot();
		if (agent instanceof PlatformAgent) return agent;
		return agent.getSimulation();
	}

	@Override
	public void setTempVar(final String name, final Object value) {
		temps.put(name, value);

	}

	@Override
	public Object getTempVar(final String name) {
		return temps.get(name);
	}

	@Override
	public Map<? extends String, ? extends Object> getLocalVars() { return temps; }

	@Override
	public void clearLocalVars() {
		temps.clear();

	}

	@Override
	public void putLocalVar(final String name, final Object val) {
		temps.put(name, val);

	}

	@Override
	public Object getLocalVar(final String name) {
		return temps.get(name);
	}

	@Override
	public boolean hasLocalVar(final String name) {
		return temps.containsKey(name);
	}

	@Override
	public void removeLocalVar(final String name) {
		temps.remove(name);
	}

	@Override
	public IExecutionContext getOuterContext() { return this; }

	@Override
	public IExecutionContext createCopy() {
		return this;
	}

	@Override
	public IExecutionContext createChildContext() {
		return this;
	}

	@Override
	public IExpression getVarExpr(final String name, final boolean asField) {
		if (temps.containsKey(name)) {
			final var value = temps.get(name);
			final IType<?> t = GamaType.of(value);
			return GAML.getExpressionFactory().createVar(name, t, false, IVarExpression.TEMP, null);
		}
		return null;
	}

	@Override
	public boolean hasAttribute(final String name) {
		return temps.containsKey(name);
	}

	@Override
	public String getContents() { return viewer.getDocument().get(); }

}
