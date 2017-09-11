/*********************************************************************************************
 *
 * 'InteractiveConsoleView.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.internal.console.IOConsoleViewer;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.IScope;
import msi.gama.util.GAML;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IVarDescriptionProvider;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

public class InteractiveConsoleView extends GamaViewPart
		implements IToolbarDecoratedView.Sizable, IGamaView.Console, IExecutionContext, IVarDescriptionProvider {

	private IOConsole msgConsole;
	IOConsoleViewer viewer;
	private OutputStreamWriter resultWriter, errorWriter;
	private BufferedReader reader;
	private IScope scope;
	private final Map<String, Object> temps = new LinkedHashMap<>();
	// private IAgent listeningAgent;
	private final List<String> history = new ArrayList<>();
	private int indexInHistory = 0;
	private Composite controlToDisplayInFullScreen;
	private Composite parentOfControlToDisplayFullScreen;

	@Override
	public void createPartControl(final Composite composite) {
		setParentOfControlToDisplayFullScreen(composite);
		final GridLayout layout = new GridLayout(1, true);
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
	public IScope getScope() {
		return scope;
	}

	@Override
	public void ownCreatePartControl(final Composite p) {
		msgConsole = new IOConsole("GAMA Console", null);
		reader = new BufferedReader(new InputStreamReader(msgConsole.getInputStream()));
		IOConsoleOutputStream stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.NEUTRAL.color());
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
					String textEntered = "";
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
				final StyledText text = (StyledText) e.widget;
				final Point selection = text.getSelection();
				final int line = text.getLineAtOffset(selection.y);
				if (line == text.getLineCount() - 1) {
					e.doit = false;
					insertHistory(e.keyCode == SWT.ARROW_UP);

				}
			}
		});
		p.layout(true, true);
		showPrompt();

	}

	public Composite getControlToDisplayInFullScreen() {
		return controlToDisplayInFullScreen;
	}

	public static final String PROMPT = "gaml> ";

	private void showPrompt() {

		new Thread(() -> {
			append(Strings.LN + PROMPT, false, false);
			try {
				// Wait for the output stream to finish
				Thread.sleep(200);
			} catch (final InterruptedException e) {}
			WorkbenchHelper.run(() -> {
				if (viewer != null && viewer.getTextWidget() != null && !viewer.getTextWidget().isDisposed())
					viewer.getTextWidget().setCaretOffset(viewer.getTextWidget().getCharCount());

			});

		}).start();
		;

	}

	private void insertHistory(final boolean back) {

		if (history.size() == 0) {
			ViewsHelper.requestUserAttention(this, "No history");
			return;
		}
		if (indexInHistory <= 0) {
			if (back)
				ViewsHelper.requestUserAttention(this, "No more history");
			indexInHistory = 0;
		} else if (indexInHistory >= history.size() - 1) {
			if (!back)
				ViewsHelper.requestUserAttention(this, "No more history");
			indexInHistory = history.size() - 1;
		}
		try {
			final StyledText text = viewer.getTextWidget();
			final int lineIndex = text.getLineCount() - 1;
			final int nbChars = text.getCharCount();
			final int firstOffset = text.getOffsetAtLine(lineIndex) + PROMPT.length();
			viewer.getDocument().replace(firstOffset, nbChars - firstOffset, history.get(indexInHistory));
			text.setCaretOffset(text.getCharCount());
			if (back) {
				indexInHistory--;
			} else
				indexInHistory++;
		} catch (final org.eclipse.jface.text.BadLocationException e1) {}

	}

	/**
	 * Append the text to the console.
	 * 
	 * @param text
	 *            to display in the console
	 */
	public void append(final String text, final boolean error, final boolean showPrompt) {

		final OutputStreamWriter writer = error ? errorWriter : resultWriter;
		WorkbenchHelper.asyncRun(() -> {
			try {
				writer.append(text);
				writer.flush();
				if (showPrompt)
					showPrompt();
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
		msgConsole.clearConsole();
		setExecutorAgent(null);
		showPrompt();
	}

	@Override
	public Control getSizableFontControl() {
		if (viewer == null) { return null; }
		return viewer.getTextWidget();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.ACTION_CLEAR, "Clear", "Clear the console", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				msgConsole.clearConsole();
				showPrompt();
			}
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

	volatile boolean isListening;

	@Override
	public void append(final String text, final ITopLevelAgent agent, final GamaColor color) {
		setExecutorAgent(agent);
		if (text != null) {
			append(text, false, true);
		}
	}

	private void setExecutorAgent(final ITopLevelAgent agent) {
		if (scope != null) {
			scope.clear();
			scope = null;
		}
		if (agent == null) {

			WorkbenchHelper.asyncRun(() -> {
				if (toolbar != null && !toolbar.isDisposed()) {
					toolbar.wipe(SWT.LEFT, true);
				}
			});
		} else {
			scope = new ExecutionScope(agent, " in console", this);
			agent.getSpecies().getDescription().attachAlternateVarDescriptionProvider(this);
			WorkbenchHelper.asyncRun(() -> toolbar.status(GamaIcons.create(IGamaIcons.MENU_AGENT).image(),
					"Listening agent: " + Cast.toGaml(agent), IGamaColors.NEUTRAL, SWT.LEFT));
		}

	}

	protected void processInput(final String s) {
		final IAgent agent = getListeningAgent();
		if (agent == null || agent.dead()) {
			setExecutorAgent(null);
		} else {
			final String entered = s.trim();
			history.add(entered);
			indexInHistory = history.size() - 1;
			String result = null;
			boolean error = false;
			if (entered.startsWith("?")) {
				result = GAML.getDocumentationOn(entered.substring(1));
			} else
				try {
					final IExpression expr = GAML.compileExpression(s, agent, this, false);
					if (expr != null) {
						result = StringUtils.toGaml(scope.evaluate(expr, agent).getValue(), true);
					}
				} catch (final Exception e) {
					error = true;
					result = "> Error: " + e.getMessage();
				} finally {
					agent.getSpecies().removeTemporaryAction();
				}
			if (result == null) {
				result = "nil";
			}
			append(result, error, true);
			if (!error && GAMA.getExperiment() != null)
				GAMA.getExperiment().refreshAllOutputs();
		}

	}

	public Composite getParentOfControlToDisplayFullScreen() {
		return parentOfControlToDisplayFullScreen;
	}

	public void setParentOfControlToDisplayFullScreen(final Composite parentOfControlToDisplayFullScreen) {
		this.parentOfControlToDisplayFullScreen = parentOfControlToDisplayFullScreen;
	}

	private IAgent getListeningAgent() {
		if (scope == null)
			setExecutorAgent(GAMA.getPlatformAgent());
		return scope.getRoot();
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
	public Map<? extends String, ? extends Object> getLocalVars() {
		return temps;
	}

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
	public IExecutionContext getOuterContext() {
		return this;
	}

	@Override
	public IExecutionContext createCopyContext() {
		return this;
	}

	@Override
	public IExecutionContext createChildContext() {
		return this;
	}

	@Override
	public IExpression getVarExpr(final String name, final boolean asField) {
		if (temps.containsKey(name)) {
			final Object value = temps.get(name);
			final IType<?> t = GamaType.of(value);
			return GAML.getExpressionFactory().createVar(name, t, false, IVarExpression.TEMP, null);
		}
		return null;
	}

	@Override
	public boolean hasAttribute(final String name) {
		return temps.containsKey(name);
	}

}
