/**
 * Created by drogoul, 28 avr. 2014
 * 
 */
package msi.gama.gui.viewers.html;

import java.io.IOException;
import java.net.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.osgi.framework.Bundle;

/**
 * Class BrowserEditor.
 * 
 * @author drogoul
 * @since 28 avr. 2014
 * 
 */
public class HtmlViewer extends EditorPart implements IToolbarDecoratedView {

	private final static int BACK = -50;
	private final static int FORWARD = -51;
	private final static int HOME = -52;
	private final static int REFRESH = -53;
	private final static int STOP = -54;
	private static String HOME_URL = null;

	public static void openWelcomePage(final boolean ifEmpty) {
		if ( ifEmpty && SwtGui.getPage().getActiveEditor() != null ) { return; }
		if ( ifEmpty && !GamaPreferences.CORE_SHOW_PAGE.getValue() ) { return; }
		if ( HOME_URL == null ) {
			Bundle bundle = Platform.getBundle("msi.gama.ext");
			URL url = bundle.getEntry("/images/welcome.html");
			try {
				url = FileLocator.toFileURL(url);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			HOME_URL = url.toString();
		}
		if ( HOME_URL != null ) {
			GuiUtils.showWebEditor(HOME_URL, null);
		}
	}

	Browser browser;
	// private GamaToolbar leftToolbar;
	private GamaToolbar2 toolbar;
	ToolItem back, forward, home;

	public HtmlViewer() {}

	@Override
	public void doSave(final IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {
		Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		browser = new Browser(compo, SWT.NONE);
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void changed(final ProgressEvent arg0) {}

			@Override
			public void completed(final ProgressEvent event) {
				toolbar.status((Image) null, browser.getUrl(), IGamaColors.NEUTRAL, SWT.LEFT);
				checkButtons();
			}
		});
		parent.layout();
		if ( getEditorInput() instanceof FileEditorInput ) {
			FileEditorInput input = (FileEditorInput) getEditorInput();
			try {
				this.setUrl(input.getURI().toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setUrl(final String url) {
		browser.setUrl(url);
		checkButtons();
	}

	/**
	 * 
	 */
	private void checkButtons() {
		back.setEnabled(browser.isBackEnabled());
		forward.setEnabled(browser.isForwardEnabled());
	}

	public void setHtml(final String html) {
		browser.setText(html, true);
		toolbar.wipe(SWT.LEFT);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public Control getSizableFontControl() {
		return browser;
	}

	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { BACK, HOME, FORWARD, SEP, REFRESH, STOP };
	}

	/**
	 * Method setToolbar()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#setToolbar(msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void setToolbar(final GamaToolbar2 toolbar) {
		this.toolbar = toolbar;
	}

	/**
	 * Method createToolItem()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void createToolItem(final int code, final GamaToolbar2 tb) {

		switch (code) {
			case BACK:
				back = tb.button("browser/back", "Back", "Go to previous page in history", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						browser.back();
						checkButtons();
					}

				}, SWT.RIGHT);
				break;
			case HOME:
				home = tb.button("browser/home", "Home", "Go back to the welcome page", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						openWelcomePage(false);
						checkButtons();
					}

				}, SWT.RIGHT);
				break;
			case FORWARD:
				forward = tb.button("browser/forward", "Forward", "Go to next page in history", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						browser.forward();
						checkButtons();
					}

				}, SWT.RIGHT);
				break;
			case REFRESH:
				tb.button("browser/refresh", "Refresh", "Refresh current page", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						browser.refresh();
					}

				}, SWT.RIGHT);
				break;
			case STOP:
				tb.button("browser/stop", "Stop", "Stop loading page", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						browser.stop();
					}

				}, SWT.RIGHT);
		}

	}

}
