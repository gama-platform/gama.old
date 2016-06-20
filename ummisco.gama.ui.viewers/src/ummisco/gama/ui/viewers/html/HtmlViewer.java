/**
 * Created by drogoul, 28 avr. 2014
 *
 */
package ummisco.gama.ui.viewers.html;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.utils.WebHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * Class BrowserEditor.
 *
 * @author drogoul
 * @since 28 avr. 2014
 *
 */
public class HtmlViewer extends EditorPart implements IToolbarDecoratedView {

	Browser browser;
	private GamaToolbar2 toolbar;
	ToolItem back, forward, home;

	public HtmlViewer() {
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput in) throws PartInitException {
		setSite(site);
		setInput(in);
		openInput();
	}

	private void openInput() {
		if (browser == null)
			return;
		if (getEditorInput() instanceof FileEditorInput) {
			final FileEditorInput input = (FileEditorInput) getEditorInput();
			try {
				this.setUrl(input.getURI().toURL().toString());
			} catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		} else if (getEditorInput() instanceof FileStoreEditorInput) {
			final FileStoreEditorInput input = (FileStoreEditorInput) getEditorInput();
			try {
				this.setUrl(input.getURI().toURL().toString());
			} catch (final MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		final Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		browser = new Browser(compo, SWT.NONE);
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void changed(final ProgressEvent arg0) {
			}

			@Override
			public void completed(final ProgressEvent event) {
				// toolbar.status((Image) null, browser.getUrl(),
				// IGamaColors.NEUTRAL, SWT.LEFT);
				checkButtons();
			}
		});
		parent.layout();
		openInput();
	}

	public void setUrl(final String url) {
		browser.setUrl(url);
		this.setPartName(url.substring(url.lastIndexOf('/') + 1));
		checkButtons();
	}

	/**
	 *
	 */
	private void checkButtons() {
		back.setEnabled(browser.isBackEnabled());
		forward.setEnabled(browser.isForwardEnabled());
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public Control getSizableFontControl() {
		return browser;
	}

	/**
	 * Method createToolItem()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;

		back = tb.button("browser/back", "Back", "Go to previous page in history", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.back();
				checkButtons();
			}

		}, SWT.RIGHT);
		home = tb.button("browser/home", "Home", "Go back to the welcome page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.setUrl(WebHelper.getWelcomePageURL().toString());
				checkButtons();
			}

		}, SWT.RIGHT);
		forward = tb.button("browser/forward", "Forward", "Go to next page in history", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.forward();
				checkButtons();
			}

		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("browser/refresh", "Refresh", "Refresh current page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.refresh();
			}

		}, SWT.RIGHT);
		tb.button("browser/stop", "Stop", "Stop loading page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.stop();
			}

		}, SWT.RIGHT);

	}

}
