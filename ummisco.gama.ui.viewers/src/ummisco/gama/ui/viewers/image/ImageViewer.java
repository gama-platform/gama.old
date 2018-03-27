/*********************************************************************************************
 *
 * 'ImageViewer.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.metadata.ImageDataLoader;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.PreferencesHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * A simple image viewer editor.
 */
public class ImageViewer extends EditorPart
implements IReusableEditor, IToolbarDecoratedView.Zoomable, IToolbarDecoratedView.Colorizable {

	GamaToolbar2 toolbar;
	private Image image;
	private ImageData imageData;
	private ScrolledComposite scroll;
	private Composite intermediate;
	private Canvas imageCanvas;
	private double zoomFactor = 1.0d;
	private double maxZoomFactor = 1.0d;
	private ImageResourceChangeListener inputListener = null;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		// we need either an IStorage or an input that can return an ImageData
		if (!(input instanceof IStorageEditorInput) && input
				.getAdapter(ImageData.class) == null) { throw new PartInitException("Unable to read input: " + input); //$NON-NLS-1$
		}
		setSite(site);
		setInput(input, false);
	}

	@Override
	public void setInput(final IEditorInput input) {
		setInput(input, true);
	}

	private void setInput(final IEditorInput input, final boolean notify) {
		final IEditorInput old = getEditorInput();
		if (input != old) {
			unregisterResourceListener(old);
			setPartName(input);
			if (notify) {
				super.setInputWithNotify(input);
			} else {
				super.setInput(input);
			}

			// start the image load job, after we set the input
			startImageLoad();
			// start listing after we start the load
			registerResourceListener(input);
		}
	}

	/**
	 * Set the part name based on the editor input.
	 */
	private void setPartName(final IEditorInput input) {
		String imageName = null;
		if (input instanceof IStorageEditorInput) {
			try {
				imageName = ((IStorageEditorInput) input).getStorage().getName();
			} catch (final CoreException ex) {
				// intentionally blank
			}
		}
		// this will catch ImageDataEditorInput as well
		if (imageName == null) {
			imageName = input.getName();
		}
		if (imageName == null) {
			imageName = getSite().getRegisteredName();
		}
		setPartName(imageName);
	}

	/**
	 * Get the IFile corresponding to the specified editor input, or null for none.
	 */
	private IFile getFileFor(final IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		} else if (input instanceof IStorageEditorInput) {
			try {
				final IStorage storage = ((IStorageEditorInput) input).getStorage();
				if (storage instanceof IFile) { return (IFile) storage; }
			} catch (final CoreException ignore) {
				// intentionally blank
			}
		}
		return null;
	}

	private void displayInfoString() {
		final GamaUIColor color = IGamaColors.OK;
		final IGamaFileMetaData md =
				GAMA.getGui().getMetaDataProvider().getMetaData(getFileFor(getEditorInput()), false, true);
		final String result = md == null ? "" : md.getSuffix();
		toolbar.button(color, result,
				e -> getEditorSite().getActionBars().getGlobalActionHandler(ActionFactory.PROPERTIES.getId()).run(),
				SWT.LEFT);
		toolbar.refresh(true);
	}

	/**
	 * Unregister any change listeners for the specified input.
	 */
	protected void unregisterResourceListener(final IEditorInput input) {
		if (input != null && inputListener != null) {
			inputListener.stop();
			inputListener = null;
		}
	}

	/**
	 * Register any change listeners on the specified new input.
	 */
	protected void registerResourceListener(final IEditorInput input) {
		if (input != null) {
			if (inputListener != null) {
				inputListener.stop();
			}
			inputListener = null;
			final IFile file = getFileFor(input);
			if (file != null) {
				inputListener = new ImageResourceChangeListener(file);
				inputListener.start();
			}
		}
	}

	/**
	 * Initialize the UI.
	 */
	@Override
	public void createPartControl(final Composite composite) {
		final Composite parent = GamaToolbarFactory.createToolbars(this, composite);

		scroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		// TODO: fixup scrolling (page) increment as things resize
		scroll.getHorizontalBar().setIncrement(10);
		scroll.getHorizontalBar().setPageIncrement(100);
		scroll.getVerticalBar().setIncrement(10);
		scroll.getVerticalBar().setPageIncrement(100);
		scroll.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				resizeCanvas(imageCanvas.getSize());
			}

		});

		// Intermediate composite
		intermediate = new Composite(scroll, SWT.None);
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		intermediate.setLayout(layout);
		intermediate.setBackground(getColor(0).color());

		// Image canvas
		imageCanvas = new Canvas(intermediate, SWT.NONE);
		final GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		imageCanvas.setLayoutData(data);
		imageCanvas.setBackground(getColor(0).color());
		// Scroll composite
		scroll.setContent(intermediate);
		// imageCanvas.setSize(0, 0);
		// make the canvas paint the image, if we have one
		imageCanvas.addPaintListener(e -> {
			final Rectangle bounds = imageCanvas.getBounds();
			// showImage() should be setting the imageCanvas bounds to the
			// zoomed size
			e.gc.setBackground(getColor(0).color());
			e.gc.fillRectangle(bounds);
			// System.out.println("Painting image at size " + bounds.width +
			// "x" + bounds.height);
			if (image != null) {
				final Rectangle imBounds = image.getBounds();
				e.gc.drawImage(image, 0, 0, imBounds.width, imBounds.height, 0, 0, bounds.width, bounds.height);
			}
		});

		startImageLoad();
	}

	private void resizeCanvas(final Point p) {
		final Rectangle scrollSize = scroll.getClientArea();
		final int width = p.x > scrollSize.width ? p.x : scrollSize.width;
		final int height = p.y > scrollSize.height ? p.y : scrollSize.height;
		intermediate.setSize(width, height);
		imageCanvas.setSize(p);
		final GridData data = (GridData) imageCanvas.getLayoutData();
		data.widthHint = p.x;
		data.heightHint = p.y;

		// System.out.println("Resizing intermediate to " +
		// intermediate.getSize().x + "x" + intermediate.getSize().y);
		intermediate.layout();
		int x = 0, y = 0;
		if (width > scrollSize.width) {
			x = (width - scrollSize.width) / 2;
		}
		if (height > scrollSize.height) {
			y = (height - scrollSize.height) / 2;
		}
		scroll.setOrigin(x, y);
	}

	/**
	 * This will start a job to load the image for the current editor input. This can be started from any thread.
	 */
	private void startImageLoad() {
		// skip if the UI hasn't been initialized yet, because
		// createPartControl() will do this
		if (imageCanvas == null) { return; }
		// clear out the current image
		final Runnable r = () -> {
			if (image != null) {
				image.dispose();
				imageData = null;
				image = null;
				imageCanvas.setSize(0, 0);
				scroll.redraw();
			}
		};
		WorkbenchHelper.asyncRun(r);

		// load the image in the background to keep the ui fresh
		final Job job = new Job(MessageFormat.format("Load Image {0}", getPartName())) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
				try {
					loadImageData();

					// show the image on the next SWT exec
					final Runnable r = () -> {
						showImage(true);
						displayInfoString();
					};
					WorkbenchHelper.asyncRun(r);

					return Status.OK_STATUS;
				} catch (final CoreException ex) {
					return ex.getStatus();
				} catch (final SWTException ex) {
					return new Status(IStatus.ERROR, "msi.gama.application", ex.getMessage());
				} finally {
					monitor.done();
				}
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/**
	 * Load the image data from the current editor input. This operation can take time and should not be called on the
	 * ui thread.
	 */
	private void loadImageData() throws CoreException {
		final IEditorInput input = getEditorInput();
		final Object o = input.getAdapter(ImageData.class);
		if (o instanceof ImageData) {
			imageData = (ImageData) o;
		} else if (input instanceof IStorageEditorInput) {
			final IFile file = getFileFor(input);
			imageData = ImageDataLoader.getImageData(file);
		}
		// save this away so we don't compute it all the time
		this.maxZoomFactor = determineMaxZoomFactor();
	}

	/**
	 * Refresh the ui to display the current image. This needs to be run in the SWT thread.
	 *
	 * @param createImage
	 *            true to (re)create the image object from the imageData, false to reuse.
	 */
	private void showImage(final boolean createImage) {
		if (imageData != null) {
			imageCanvas.setCursor(imageCanvas.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
			try {
				if (createImage || image == null) {
					// dispose of the old image
					if (image != null && !image.isDisposed()) {
						image.dispose();
						image = null;
					}
					image = new Image(imageCanvas.getDisplay(), imageData);
				}
				final Rectangle imageSize = image.getBounds();
				final Point newSize =
						new Point((int) (imageSize.width * zoomFactor), (int) (imageSize.height * zoomFactor));
				resizeCanvas(newSize);
				scroll.redraw();
			} finally {
				imageCanvas.setCursor(null);
			}
		}
	}

	@Override
	public void setFocus() {
		if (scroll != null && !scroll.isDisposed()) {
			scroll.setFocus();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		// do this directly
		if (this.inputListener != null) {
			inputListener.stop();
			inputListener = null;
		}
		if (image != null && !image.isDisposed()) {
			image.dispose();
			image = null;
		}
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {
		// get a new path from the user
		final SaveImageAsDialog d = new SaveImageAsDialog(getSite().getShell());
		// initialize the dialog path and file, as best as possible, including
		// pre-selecting the image type.
		int origImageType = imageData.type;
		// default to PNG, if the imageData doesn't yet have a type (i.e. as
		// from screenshot)
		if (origImageType < 0) {
			origImageType = SWT.IMAGE_PNG;
		}
		final IFile origFile = getFileFor(getEditorInput());
		if (origFile != null) {
			d.setOriginalFile(origFile, origImageType);
		} else {
			final IPath initialFileName = Path.fromPortableString(getPartName()).removeFileExtension();
			d.setOriginalName(initialFileName.toPortableString(), origImageType);
		}
		d.create();
		if (d.open() != Window.OK) { return; }

		// get the selected file path
		IPath path = d.getResult();
		if (path == null) { return; }
		// add a file extension if there isn't one
		if (path.getFileExtension() == null) {
			path = path.addFileExtension(d.getSaveAsImageExt());
		}

		final IFile dest = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (dest == null || origFile != null && dest.equals(origFile)) { return; }
		final int imageType = d.getSaveAsImageType();

		// create a scheduling rule for the file edit/creation
		final IResourceRuleFactory ruleFactory = dest.getWorkspace().getRuleFactory();
		ISchedulingRule rule = null;
		if (dest.exists()) {
			rule = ruleFactory.modifyRule(dest);
			rule = MultiRule.combine(rule, ruleFactory.validateEditRule(new IResource[] { dest }));
		} else {
			rule = ruleFactory.createRule(dest);
			// this might end up creating some folders, so include those, too
			IContainer parent = dest.getParent();
			while (parent != null && !(parent instanceof IProject) && !parent.exists()) {
				rule = MultiRule.combine(rule, ruleFactory.createRule(parent));
				parent = parent.getParent();
			}
		}
		// create the file
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation(rule) {

			@Override
			protected void execute(final IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
				try {
					if (dest.exists()) {
						if (!dest.getWorkspace().validateEdit(new IFile[] { dest }, getSite().getShell())
								.isOK()) { return; }
					}
					saveTo(imageData, dest, imageType, monitor);
				} catch (final IOException ex) {
					throw new InvocationTargetException(ex);
				}
			}
		};
		final ProgressMonitorDialog pmd = new ProgressMonitorDialog(getSite().getShell());
		try {
			pmd.run(true, true, op);
			// reset our editor input to the file, if weren't not open on a
			// file.
			if (getFileFor(getEditorInput()) == null) {
				setInput(new FileEditorInput(dest));
			}
		} catch (final InvocationTargetException ex) {
			final Throwable t = ex.getCause();

			final String title = "Error Saving";
			final String mesg = MessageFormat.format("Failed to save {0}", path.toPortableString());
			// ImagesActivator.getDefault().log(IStatus.WARNING, mesg, t);
			IStatus st = null;
			if (t instanceof CoreException) {
				st = ((CoreException) t).getStatus();
			} else {
				st = new Status(IStatus.ERROR, "msi.gama.application", 0, t.toString(), t);
			}
			if (st.getSeverity() != IStatus.CANCEL) {
				ErrorDialog.openError(getSite().getShell(), title, mesg, st);
			}
		} catch (final InterruptedException ex) {
			// ignore
		}
	}

	private void saveTo(final ImageData imageData, final IFile dest, final int imageType,
			final IProgressMonitor monitor) throws CoreException, InterruptedException, IOException {
		// do an indeterminate progress monitor so that something shows, since
		// the generation of the image data doesn't report progress
		final SubMonitor m = SubMonitor.convert(monitor, dest.getFullPath().toPortableString(), IProgressMonitor.UNKNOWN/* taskSize */);
		try {
			if (!dest.getParent().exists()) {
				final ContainerGenerator gen = new ContainerGenerator(dest.getFullPath().removeLastSegments(1));
				gen.generateContainer(m.split(500));
				if (monitor.isCanceled()) { throw new InterruptedException(); }
			}
			final ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { imageData };

			// but, let's use pipes instead so we don't have to buffer the whole
			// thing unnecessarily in memory
			final PipedInputStream pin = new PipedInputStream();
			final PipedOutputStream pout = new PipedOutputStream(pin);
			// the write to the pipe has to happen in a different thread or
			// else we get deadlock
			final Job writeJob = new Job("Write image data to pipe") {

				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					IStatus status = Status.OK_STATUS;
					try {
						loader.save(pout, imageType);
						pout.flush();
					} catch (final Exception ex) {
						status = new Status(IStatus.ERROR, "msi.gama.application",
								MessageFormat.format("Error getting image data for {0}", dest.getFullPath()), ex);
					} finally {
						try {
							pout.close();
						} catch (final IOException e) {
							System.out.println("Exception ignored in ImageViewer saveTo: " + e.getMessage());
						}
					}
					// always do our own error dialog
					if (!status.isOK()) {
						final IStatus fstatus = status;
						getSite().getShell().getDisplay()
						.asyncExec(() -> ErrorDialog.openError(getSite().getShell(), "Error Saving",
								MessageFormat.format("Failed to save {0}", dest.getFullPath()), fstatus));
					}
					return Status.OK_STATUS;
				}
			};
			writeJob.setSystem(true);
			writeJob.setUser(false);
			writeJob.schedule();


			try (final BufferedInputStream in = new BufferedInputStream(pin)) {
				// try reading one byte to make sure that loader.save() actually
				// worked before we destroy or create a file.
				in.mark(1);
				final int first = in.read();
				// the Job should have shown the error dialog if we don't get a
				// first byte
				if (first != -1) {
					in.reset();
					if (dest.exists()) {
						dest.setContents(in, true, true, m.split(500));
					} else {
						dest.create(in, true, m.split(500));
					}
				}
			} 
		} finally {
			monitor.done();
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public ImageData getImageData() {
		return imageData;
	}

	/**
	 * Get the current image information.
	 *
	 * @return { SWT.IMAGE_* type, width, height } or null for no image
	 */
	public int[] getCurrentImageInformation() {
		if (imageData != null) { return new int[] { imageData.type, imageData.width, imageData.height }; }
		return null;
	}

	/**
	 * Determine the max zoom factor for the current image size.
	 */
	private double determineMaxZoomFactor() {
		if (imageData != null) {
			final double maxWidth = (double) Integer.MAX_VALUE / imageData.width;
			final double maxHeight = (double) Integer.MAX_VALUE / imageData.height;
			return Math.min(maxWidth, maxHeight);
		}
		return 1.0d;
	}

	/**
	 * Get the current zoom factor.
	 */
	public double getZoomFactor() {
		return this.zoomFactor;
	}

	/**
	 * Update the zoom factor. This can safely called from any thread. It will trigger an image redraw is needed. If the
	 * passed in value is larger than the {@link #getMaxZoomFactor() max zoom factor}, the max zoom factor will used
	 * instead.
	 */
	public void setZoomFactor(final double z) {
		double newZoom = z;
		// don't go bigger than the maz zoom
		newZoom = Math.min(newZoom, maxZoomFactor);
		if (zoomFactor != newZoom && newZoom > 0.0d) {
			// Double old = Double.valueOf(zoomFactor);
			this.zoomFactor = newZoom;
			// redraw the image
			if (imageCanvas != null) {
				final Runnable r = () -> showImage(false);
				WorkbenchHelper.run(r);
			}
		}
	}

	@Override
	public void zoomIn() {
		setZoomFactor(getZoomFactor() * 1.1);
	}

	@Override
	public void zoomOut() {
		setZoomFactor(getZoomFactor() * 0.9);
	}

	@Override
	public void zoomFit() {
		if (imageData.width > imageData.height) {
			setZoomFactor((double) scroll.getSize().x / (double) imageData.width);
		} else {
			setZoomFactor((double) scroll.getSize().y / (double) imageData.height);
		}
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { intermediate, imageCanvas };
	}

	/**
	 * This handles changes to a file-based editor input.
	 */
	private class ImageResourceChangeListener implements IResourceChangeListener {

		IResource imageFile;

		public ImageResourceChangeListener(final IResource imageFile) {
			this.imageFile = imageFile;
		}

		/**
		 * Start listening to file changes.
		 */
		void start() {
			imageFile.getWorkspace().addResourceChangeListener(this);
		}

		/**
		 * Stop listening to file changes.
		 */
		void stop() {
			imageFile.getWorkspace().removeResourceChangeListener(this);
		}

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			final IResourceDelta delta = event.getDelta().findMember(imageFile.getFullPath());
			if (delta != null) {
				// file deleted -- close the editor
				if (delta.getKind() == IResourceDelta.REMOVED) {
					final Runnable r = () -> getSite().getPage().closeEditor(ImageViewer.this, false);
					getSite().getShell().getDisplay().asyncExec(r);
				}
				// file changed -- reload image
				else if (delta.getKind() == IResourceDelta.CHANGED) {
					final int flags = delta.getFlags();
					if ((flags & IResourceDelta.CONTENT) != 0 || (flags & IResourceDelta.LOCAL_CHANGED) != 0) {
						startImageLoad();
					}
				}
			}
		}
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
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("menu.saveas2", "Save as...", "Save as...", e -> doSaveAs(), SWT.RIGHT);

	}

	/**
	 * Method getColorLabels()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Colorizable#getColorLabels()
	 */
	@Override
	public String[] getColorLabels() {
		return new String[] { "Set background color..." };
	}

	/**
	 * Method getColor()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Colorizable#getColor(int)
	 */
	@Override
	public GamaUIColor getColor(final int index) {
		return GamaColors.get(PreferencesHelper.IMAGE_VIEWER_BACKGROUND.getValue());
	}

	/**
	 * Method setColor()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Colorizable#setColor(int,
	 *      ummisco.gama.ui.resources.GamaColors.GamaUIColor)
	 */
	@Override
	public void setColor(final int index, final GamaUIColor c) {
		if (imageCanvas != null) {
			final Runnable rr = () -> {
				intermediate.setBackground(c.color());
				showImage(false);
			};
			WorkbenchHelper.run(rr);
		}

	}

	/**
	 * Method zoomWhenScrolling()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return false;
	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}
}