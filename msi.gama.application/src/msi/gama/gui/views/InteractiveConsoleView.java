package msi.gama.gui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.internal.console.IOConsoleViewer;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public class InteractiveConsoleView extends GamaViewPart implements IToolbarDecoratedView.Sizable {

	private IOConsole msgConsole;
	IOConsoleViewer viewer;
	private OutputStreamWriter resultWriter, errorWriter;
	private BufferedReader reader;
	private IAgent listeningAgent;
	private final List<String> history = new ArrayList();
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
	public void ownCreatePartControl(final Composite p) {

		msgConsole = new IOConsole("GAMA Console", null);
		reader = new BufferedReader(new InputStreamReader(msgConsole.getInputStream()));
		IOConsoleOutputStream stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.NEUTRAL.color());
		resultWriter = new OutputStreamWriter(stream);
		stream = msgConsole.newOutputStream();
		stream.setColor(IGamaColors.ERROR.color());
		errorWriter = new OutputStreamWriter(stream);
		viewer = new IOConsoleViewer(p, msgConsole);

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

		viewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(final VerifyEvent e) {
				if ( e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN ) {
					final StyledText text = (StyledText) e.widget;
					final Point selection = text.getSelection();
					final int line = text.getLineAtOffset(selection.y);
					if ( line == text.getLineCount() - 1 ) {
						e.doit = false;
						insertHistory(e.keyCode == SWT.ARROW_UP);

					}
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

		new Thread(new Runnable() {

			@Override
			public void run() {
				append(Strings.LN + PROMPT, false, false);
				try {
					// Wait for the output stream to finish
					Thread.sleep(200);
				} catch (final InterruptedException e) {}
				GAMA.getGui().run(new Runnable() {

					@Override
					public void run() {
						if ( viewer != null && viewer.getTextWidget() != null && !viewer.getTextWidget().isDisposed() )
							viewer.getTextWidget().setCaretOffset(viewer.getTextWidget().getCharCount());

					}
				});

			}
		}).start();;

	}

	private void insertHistory(final boolean back) {

		if ( history.size() == 0 ) {
			SwtGui.requestUserAttention(this, "No history");
			return;
		}
		if ( indexInHistory <= 0 ) {
			if ( back )
				SwtGui.requestUserAttention(this, "No more history");
			indexInHistory = 0;
		} else if ( indexInHistory >= history.size() - 1 ) {
			if ( !back )
				SwtGui.requestUserAttention(this, "No more history");
			indexInHistory = history.size() - 1;
		}
		try {
			final StyledText text = viewer.getTextWidget();
			final int lineIndex = text.getLineCount() - 1;
			final int nbChars = text.getCharCount();
			final int firstOffset = text.getOffsetAtLine(lineIndex) + PROMPT.length();
			viewer.getDocument().replace(firstOffset, nbChars - firstOffset, history.get(indexInHistory));
			text.setCaretOffset(text.getCharCount());
			if ( back ) {
				indexInHistory--;
			} else indexInHistory++;
		} catch (final BadLocationException e1) {}

	}

	/**
	 * Append the text to the console.
	 * @param text to display in the console
	 */
	public void append(final String text, final boolean error, final boolean showPrompt) {

		final OutputStreamWriter writer = error ? errorWriter : resultWriter;
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					writer.append(text);
					writer.flush();
					if ( showPrompt )
						showPrompt();
				} catch (final IOException e) {}

			}
		});

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
			final String entered = s.trim();
			history.add(entered);
			indexInHistory = history.size() - 1;
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

	public Composite getParentOfControlToDisplayFullScreen() {
		return parentOfControlToDisplayFullScreen;
	}

	public void setParentOfControlToDisplayFullScreen(Composite parentOfControlToDisplayFullScreen) {
		this.parentOfControlToDisplayFullScreen = parentOfControlToDisplayFullScreen;
	}

}
