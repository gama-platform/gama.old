package msi.gama.gui.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.internal.console.IOConsoleViewer;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;

public class InteractiveConsoleView extends GamaViewPart implements IToolbarDecoratedView.Sizable {

	private IOConsole msgConsole;
	IOConsoleViewer viewer;
	private BufferedWriter resultWriter, errorWriter;
	private BufferedReader reader;
	private IAgent listeningAgent;

	@Override
	public void ownCreatePartControl(final Composite parent) {
		msgConsole = new IOConsole("GAMA Console", null);
		reader = new BufferedReader(new InputStreamReader(msgConsole.getInputStream()));
		IOConsoleOutputStream stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.NEUTRAL.color());
		resultWriter = new BufferedWriter(new OutputStreamWriter(stream));
		stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.ERROR.color());
		errorWriter = new BufferedWriter(new OutputStreamWriter(stream));
		viewer = new IOConsoleViewer(parent, msgConsole);
		viewer.setWordWrap(true);

		msgConsole.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(final DocumentEvent event) {
				if ( Strings.LN.equals(event.getText()) ) {
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
		showPrompt();

	}

	private void showPrompt() {
		append(Strings.LN + "gaml> ", false, false);
	}

	/**
	 * Append the text to the console.
	 * @param text to display in the console
	 */
	public void append(final String text, final boolean error, final boolean cr) {

		try {
			final BufferedWriter writer = error ? errorWriter : resultWriter;
			writer.append(text);
			writer.flush();
			if ( cr )
				showPrompt();
		} catch (final IOException e) {}

	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		clearText();
		super.widgetDisposed(e);
	}

	@Override
	public void close() {
		clearText();
		super.close();
	}

	public void clearText() {
		msgConsole.clearConsole();
		showPrompt();
	}

	@Override
	public Control getSizableFontControl() {
		if ( viewer == null ) { return null; }
		return viewer.getTextWidget();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.ACTION_CLEAR.getCode(), "Clear", "Clear the console", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				clearText();
			}
		}, SWT.RIGHT);

	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return null;
	}

	/**
	 * As ConsoleView is automatically opened by moving to the simulation perspective, the automatic closing can cause problems. So the view is stated as accepting an "experiment-less" mode. See Issue
	 * #1361
	 * Method shouldBeClosedWhenNoExperiments()
	 * @see msi.gama.gui.views.GamaViewPart#shouldBeClosedWhenNoExperiments()
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

	private IAgent getExecutorAgent() {
		if ( GAMA.getExperiment() == null )
			return null;
		return GAMA.getExperiment().getAgent();
	}

	public void setExecutorAgent(final IAgent agent) {
		listeningAgent = agent;
		if ( agent != null )
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					toolbar.status(IGamaIcons.MENU_AGENT.image(), "Listening agent: " + Cast.toGaml(agent),
						IGamaColors.NEUTRAL, SWT.LEFT);

				}
			});

	}

	protected void processInput(final String s) {
		if ( listeningAgent == null || listeningAgent.dead() ) {
			setExecutorAgent(null);
		} else {
			String result = null;
			boolean error = false;
			try {
				final IExpression expr = GAML.compileExpression(s, getExecutorAgent(), false);
				if ( expr != null ) {
					result = Cast.toGaml(getExecutorAgent().getScope().evaluate(expr, getExecutorAgent()));
				}
			} catch (final Exception e) {
				error = true;
				result = "> Error: " + e.getMessage();
			}
			if ( result == null ) {
				result = "nil";
			}
			append(result, error, true);
			if ( !error && GAMA.getExperiment() != null )
				GAMA.getExperiment().refreshAllOutputs();
		}

	}

}
