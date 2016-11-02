/*********************************************************************************************
 *
 * 'CRSChooser.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Creates a Control for choosing a Coordinate Reference System.
 * 
 * @author jeichar
 * @since 0.6.0
 *
 *
 *
 * @source $URL$
 */
public class CRSChooser {

	private static final String WKT_ID = "WKT"; //$NON-NLS-1$
	private static final String ALIASES_ID = "ALIASES"; //$NON-NLS-1$
	private static final String LAST_ID = "LAST_ID"; //$NON-NLS-1$
	private static final String NAME_ID = "NAME_ID"; //$NON-NLS-1$
	private static final String CUSTOM_ID = "CRS.Custom.Services"; //$NON-NLS-1$
	private static final Controller DEFAULT = new Controller() {

		@Override
		public void handleClose() {
		}

		@Override
		public void handleOk() {
		}

	};

	ListViewer codesList;
	Text searchText;
	Text wktText;
	Text keywordsText;
	CoordinateReferenceSystem selectedCRS;
	Matcher matcher;
	private TabFolder folder;
	private Controller parentPage;
	private HashMap<String, String> crsCodeMap;
	private CoordinateReferenceSystem sourceCRS;

	public CRSChooser(final Controller parentPage) {
		matcher = Pattern.compile(".*?\\(([^(]*)\\)$").matcher(""); //$NON-NLS-1$ //$NON-NLS-2$
		this.parentPage = parentPage;
	}

	public CRSChooser() {
		this(DEFAULT);
	}

	private Control createCustomCRSControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);

		final GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		GridData gridData = new GridData();
		final Label keywordsLabel = new Label(composite, SWT.NONE);
		keywordsLabel.setText("Keywords:");
		keywordsLabel.setLayoutData(gridData);
		keywordsLabel.setToolTipText("Comma separated keywords for searching");

		gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		keywordsText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		keywordsText.setLayoutData(gridData);
		keywordsText.setToolTipText("Comma separated keywords for searching");

		gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.horizontalSpan = 2;
		final Label editorLabel = new Label(composite, SWT.NONE);
		editorLabel.setText("Coordinate Reference System WKT:");
		editorLabel.setLayoutData(gridData);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		wktText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		if (selectedCRS != null)
			wktText.setText(selectedCRS.toWKT());
		wktText.setLayoutData(gridData);
		wktText.addModifyListener(e -> {
			if (!keywordsText.isEnabled())
				keywordsText.setEnabled(true);
		});

		searchText.setFocus();
		return composite;
	}

	private Control createStandardCRSControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		GridData gridData = new GridData();
		final Label codesLabel = new Label(composite, SWT.NONE);
		codesLabel.setText("Coordinate Reference Systems:");
		codesLabel.setLayoutData(gridData);

		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		searchText = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.CANCEL);
		searchText.setLayoutData(gridData);
		searchText.addModifyListener(e -> fillCodesList());
		searchText.addListener(SWT.KeyUp, event -> {
			if (event.keyCode == SWT.ARROW_DOWN) {
				codesList.getControl().setFocus();
			}
		});
		gridData = new GridData(400, 300);
		codesList = new ListViewer(composite);
		codesList.setContentProvider(new ArrayContentProvider());
		codesList.setLabelProvider(new LabelProvider());
		codesList.addSelectionChangedListener(event -> {
			selectedCRS = null;
			final String crsCode = (String) ((IStructuredSelection) codesList.getSelection()).getFirstElement();
			if (crsCode == null)
				return;
			matcher.reset(crsCode);
			if (matcher.matches()) {
				selectedCRS = createCRS(matcher.group(1));
				if (selectedCRS != null && wktText != null) {
					wktText.setEditable(true);
					String wkt = null;
					try {
						wkt = selectedCRS.toWKT();
					} catch (final Exception e1) {
						/*
						 * if unable to generate WKT, just return the string and
						 * make the text area non editable.
						 */
						wkt = selectedCRS.toString();
						wktText.setEditable(false);
					}
					wktText.setText(wkt);
					final Preferences node = findNode(matcher.group(1));
					if (node != null) {
						final Preferences kn = node.node(ALIASES_ID);
						try {
							final String[] keywords = kn.keys();
							if (keywords.length > 0) {
								final StringBuffer buffer = new StringBuffer();
								for (final String string : keywords) {
									buffer.append(", "); //$NON-NLS-1$
									buffer.append(string);
								}
								buffer.delete(0, 2);
								keywordsText.setText(buffer.toString());
							}
						} catch (final BackingStoreException e2) {
							ExceptionMonitor.show(wktText.getShell(), e2);
						}
					} else {
						keywordsText.setText(""); //$NON-NLS-1$
					}
				}
			}

		});

		codesList.addDoubleClickListener(event -> {
			parentPage.handleOk();
			parentPage.handleClose();

		});

		codesList.getControl().setLayoutData(gridData);
		/*
		 * fillCodesList() by itself resizes the Preferences Page but in the
		 * paintlistener it flickers the window
		 */
		fillCodesList();

		searchText.setFocus();

		return composite;
	}

	public void setFocus() {
		searchText.setFocus();
	}

	/**
	 * Creates the CRS PreferencePage root control with a CRS already selected
	 * 
	 * @param parent
	 *            PreferencePage for this chooser
	 * @param crs
	 *            current CRS for the associated map
	 * @return control for the PreferencePage
	 */
	public Control createControl(final Composite parent, final CoordinateReferenceSystem crs) {
		final Control control = createControl(parent);
		selectedCRS = crs;
		gotoCRS(selectedCRS);
		return control;
	}

	public void clearSearch() {
		searchText.setText(""); //$NON-NLS-1$
	}

	/**
	 * Takes in a CRS, finds it in the list and highlights it
	 * 
	 * @param crs
	 */
	@SuppressWarnings("unchecked")
	public void gotoCRS(final CoordinateReferenceSystem crs) {
		if (crs != null) {
			final List list = codesList.getList();
			final Set<Identifier> identifiers = new HashSet<Identifier>(crs.getIdentifiers());

			final Set<Integer> candidates = new HashSet<Integer>();

			for (int i = 0; i < list.getItemCount(); i++) {
				for (final Identifier identifier : identifiers) {
					final String item = list.getItem(i);
					if (sameEPSG(crs, identifier, item) || exactMatch(crs, identifier, item)) {
						codesList.setSelection(new StructuredSelection(item), false);
						list.setTopIndex(i);
						return;
					}
					if (isMatch(crs, identifier, item)) {
						candidates.add(i);
					}
				}
			}
			if (candidates.isEmpty()) {
				final java.util.List<String> input = (java.util.List<String>) codesList.getInput();
				final String sourceCRSName = crs.getName().toString();
				sourceCRS = crs;
				input.add(0, sourceCRSName);
				codesList.setInput(input);
				codesList.setSelection(new StructuredSelection(sourceCRSName), false);
				list.setTopIndex(0);
				try {
					final String toWKT = crs.toWKT();
					wktText.setText(toWKT);
				} catch (final RuntimeException e) {
					ExceptionMonitor.show(wktText.getShell(), e, crs.toString() + " cannot be formatted as WKT"); //$NON-NLS-1$
					wktText.setText("Unknown/Illegal WKT");
				}
			} else {
				final Integer next = candidates.iterator().next();
				codesList.setSelection(new StructuredSelection(list.getItem(next)), false);
				list.setTopIndex(next);

			}
		}
	}

	private boolean exactMatch(final CoordinateReferenceSystem crs, final Identifier identifier, final String item) {
		return crs == DefaultGeographicCRS.WGS84 && item.equals("WGS 84 (4326)") || //$NON-NLS-1$
				item.equalsIgnoreCase(identifier.toString()) || isInCodeMap(identifier, item);
	}

	private boolean isInCodeMap(final Identifier identifier, final String item) {

		final String name = crsCodeMap.get(identifier.getCode());
		if (name == null)
			return false;
		return name.equals(item);
	}

	private boolean sameEPSG(final CoordinateReferenceSystem crs, final Identifier identifier, final String item) {
		final String toString = identifier.toString();
		return toString.contains("EPSG:") && item.contains(toString); //$NON-NLS-1$
	}

	private boolean isMatch(final CoordinateReferenceSystem crs, final Identifier identifier, final String item) {
		return crs == DefaultGeographicCRS.WGS84 && item.contains("4326") || item.contains(identifier.toString()); //$NON-NLS-1$
	}

	/**
	 * Creates the CRS PreferencePage root control with no CRS selected
	 * 
	 * @param parent
	 *            PreferencePage for this chooser
	 * @return control for the PreferencePage
	 */
	public Control createControl(final Composite parent) {
		GridData gridData = null;

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(gridData);

		final TabItem standard = new TabItem(folder, SWT.NONE);
		standard.setText("Standard CRS");
		final Control stdCRS = createStandardCRSControl(folder);
		standard.setControl(stdCRS);

		final TabItem custom = new TabItem(folder, SWT.NONE);
		custom.setText("Custom CRS");
		final Control cstCRS = createCustomCRSControl(folder);
		custom.setControl(cstCRS);

		return folder;
	}

	/**
	 * checks if all keywords in filter array are in input
	 * 
	 * @param input
	 *            test string
	 * @param filter
	 *            array of keywords
	 * @return true, if all keywords in filter are in the input, false otherwise
	 */
	protected boolean matchesFilter(final String input, final String[] filter) {
		for (final String match : filter) {
			if (!input.contains(match))
				return false;
		}
		return true;
	}

	/**
	 * filters all CRS Names from all available CRS authorities
	 * 
	 * @param filter
	 *            array of keywords
	 * @return Set of CRS Names which contain all the filter keywords
	 */
	protected Set<String> filterCRSNames(final String[] filter) {
		crsCodeMap = new HashMap<String, String>();
		final Set<String> descriptions = new TreeSet<String>();

		for (final Object object : ReferencingFactoryFinder.getCRSAuthorityFactories(null)) {
			final CRSAuthorityFactory factory = (CRSAuthorityFactory) object;
			try {
				final Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
				for (final Object codeObj : codes) {
					final String code = (String) codeObj;
					String description;
					try {
						description = factory.getDescriptionText(code).toString();
					} catch (final Exception e1) {
						description = "UNNAMED";
					}
					description += " (" + code + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					crsCodeMap.put(code, description);
					if (matchesFilter(description.toUpperCase(), filter)) {
						descriptions.add(description);
					}
				}
			} catch (final FactoryException e) {
				ExceptionMonitor.show(wktText.getShell(), e, "CRS Authority:" + e.getMessage());
			}
		}
		return descriptions;
	}

	/**
	 * populates the codes list with a filtered list of CRS names
	 */
	protected void fillCodesList() {
		final String[] searchParms = searchText.getText().toUpperCase().split(" "); //$NON-NLS-1$
		Set<String> descriptions = filterCRSNames(searchParms);
		descriptions = filterCustomCRSs(descriptions, searchParms);
		final java.util.List<String> list = new ArrayList<String>(descriptions);
		codesList.setInput(list);
		if (!list.isEmpty()) {
			codesList.setSelection(new StructuredSelection(list.get(0)));
		} else {
			codesList.setSelection(new StructuredSelection());
			// System.out.println( "skipped");
		}
	}

	private Set<String> filterCustomCRSs(final Set<String> descriptions, final String[] searchParms) {
		try {
			final Preferences root = Preferences.userRoot();
			final Preferences node = root.node(CUSTOM_ID);

			for (final String id : node.childrenNames()) {
				final Preferences child = node.node(id);
				final String string = child.get(NAME_ID, null);
				if (string != null && matchesFilter(string.toUpperCase(), searchParms)) {
					descriptions.add(string);
					continue;
				}

				final Preferences aliases = child.node(ALIASES_ID);
				for (final String alias : aliases.keys()) {
					if (matchesFilter(alias.toUpperCase(), searchParms)) {
						descriptions.add(string);
						continue;
					}
				}
			}
		} catch (final Exception e) {
			ExceptionMonitor.show(wktText.getShell(), e);
		}
		return descriptions;
	}

	/**
	 * creates a CRS from a code when the appropriate CRSAuthorityFactory is
	 * unknown
	 * 
	 * @param code
	 *            CRS code
	 * @return CRS object from appropriate authority, or null if the appropriate
	 *         factory cannot be determined
	 */
	protected CoordinateReferenceSystem createCRS(final String code) {
		if (code == null)
			return null;
		for (final Object object : ReferencingFactoryFinder.getCRSAuthorityFactories(null)) {
			final CRSAuthorityFactory factory = (CRSAuthorityFactory) object;
			try {
				return (CoordinateReferenceSystem) factory.createObject(code);
			} catch (final FactoryException e2) {
				// then we have the wrong factory
				// is there a better way to do this?
			} catch (final Exception e) {
				ExceptionMonitor.show(wktText.getShell(), e, "Error creating CRS object, trying more...");
			}
		}
		try {
			final Preferences child = findNode(code);
			if (child != null) {
				final String wkt = child.get(WKT_ID, null);
				if (wkt != null) {
					try {
						return ReferencingFactoryFinder.getCRSFactory(null).createFromWKT(wkt);
					} catch (final Exception e) {
						ExceptionMonitor.show(wktText.getShell(), e);
						child.removeNode();
					}
				}
			}

		} catch (final Exception e) {
			ExceptionMonitor.show(wktText.getShell(), e);
		}
		return null; // should throw an exception?
	}

	private Preferences findNode(final String code) {
		try {
			final Preferences root = Preferences.userRoot();
			final Preferences node = root.node(CUSTOM_ID);

			if (node.nodeExists(code)) {
				return node.node(code);
			}

			for (final String id : node.childrenNames()) {
				final Preferences child = node.node(id);
				final String name = child.get(NAME_ID, null);
				if (name != null && matchesFilter(name, new String[] { code })) {
					return child;
				}
			}
			return null;
		} catch (final BackingStoreException e) {
			ExceptionMonitor.show(wktText.getShell(), e);
			return null;
		}
	}

	/**
	 * returns the selected CRS
	 * 
	 * @return selected CRS
	 */
	public CoordinateReferenceSystem getCRS() {
		if (folder == null)
			return selectedCRS;
		if (folder.getSelectionIndex() == 1) {
			try {
				final String text = wktText.getText();
				final CoordinateReferenceSystem createdCRS = ReferencingFactoryFinder.getCRSFactory(null)
						.createFromWKT(text);

				if (keywordsText.getText().trim().length() > 0) {
					final Preferences node = findNode(createdCRS.getName().getCode());
					if (node != null) {
						final Preferences kn = node.node(ALIASES_ID);
						final String[] keywords = keywordsText.getText().split(","); //$NON-NLS-1$
						kn.clear();
						for (String string : keywords) {
							string = string.trim().toUpperCase();
							if (string.length() > 0)
								kn.put(string, string);
						}
						kn.flush();
					} else {
						CoordinateReferenceSystem found = createCRS(createdCRS.getName().getCode());
						if (found != null && CRS.findMathTransform(found, createdCRS, true).isIdentity()) {
							saveKeywords(found);
							return found;
						}

						final Set<Identifier> identifiers = new HashSet<Identifier>(createdCRS.getIdentifiers());
						for (final Identifier identifier : identifiers) {
							found = createCRS(identifier.toString());
							if (found != null && CRS.findMathTransform(found, createdCRS, true).isIdentity()) {
								saveKeywords(found);
								return found;
							}
						}
						return saveCustomizedCRS(text, true, createdCRS);
					}
				}

				return createdCRS;
			} catch (final Exception e) {
				ExceptionMonitor.show(wktText.getShell(), e);
			}
		}
		if (selectedCRS == null) {
			final String crsCode = (String) ((IStructuredSelection) codesList.getSelection()).getFirstElement();
			if (sourceCRS != null && crsCode.equals(sourceCRS.getName().toString())) {
				// System.out.println("source crs: " +
				// sourceCRS.getName().toString());
				return sourceCRS;
			}
			return createCRS(searchText.getText());
		}
		return selectedCRS;
	}

	/**
	 *
	 * @param found
	 * @throws CoreException
	 * @throws IOException
	 * @throws BackingStoreException
	 */
	private void saveKeywords(final CoordinateReferenceSystem found)
			throws CoreException, IOException, BackingStoreException {
		final String[] keywords = keywordsText.getText().split(","); //$NON-NLS-1$
		if (keywords.length > 0) {
			boolean legalKeyword = false;
			// determine whether there are any keywords that are not blank.
			for (int i = 0; i < keywords.length; i++) {
				String string = keywords[i];
				string = string.trim().toUpperCase();
				if (string.length() > 0) {
					legalKeyword = true;
					break;
				}
			}
			if (legalKeyword) {
				saveCustomizedCRS(found.toWKT(), false, found);
			}
		}
		keywordsText.setText(""); //$NON-NLS-1$
		wktText.setText(found.toWKT());
	}

	/**
	 * @param text
	 * @param createdCRS
	 * @throws CoreException
	 * @throws IOException
	 * @throws BackingStoreException
	 */
	private CoordinateReferenceSystem saveCustomizedCRS(final String text, final boolean processWKT,
			final CoordinateReferenceSystem createdCRS) throws CoreException, IOException, BackingStoreException {
		final Preferences root = Preferences.userRoot();
		final Preferences node = root.node(CUSTOM_ID);
		int lastID;
		String code;
		String name;
		String newWKT;
		if (processWKT) {
			lastID = Integer.parseInt(node.get(LAST_ID, "0")); //$NON-NLS-1$
			code = "UDIG:" + lastID; //$NON-NLS-1$
			name = createdCRS.getName().toString() + "(" + code + ")";//$NON-NLS-1$ //$NON-NLS-2$
			lastID++;
			node.putInt(LAST_ID, lastID);
			newWKT = processingWKT(text, lastID);
		} else {
			final Set<ReferenceIdentifier> ids = createdCRS.getIdentifiers();
			if (!ids.isEmpty()) {
				final Identifier id = ids.iterator().next();
				code = id.toString();
				name = createdCRS.getName().getCode() + " (" + code + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				name = code = createdCRS.getName().getCode();
			}

			newWKT = text;
		}

		final Preferences child = node.node(code);
		child.put(NAME_ID, name);
		child.put(WKT_ID, newWKT);
		final String[] keywords = keywordsText.getText().split(","); //$NON-NLS-1$
		if (keywords.length > 0) {
			final Preferences keyworkNode = child.node(ALIASES_ID);
			for (String string : keywords) {
				string = string.trim().toUpperCase();
				keyworkNode.put(string, string);
			}
		}
		node.flush();

		return createdCRS;
	}

	/**
	 * Remove the last AUTHORITY if it exists and add a UDIG Authority
	 */
	private String processingWKT(final String text, final int lastID) {
		String newWKT;
		final String[] prep = text.split(","); //$NON-NLS-1$
		if (prep[prep.length - 2].toUpperCase().contains("AUTHORITY")) { //$NON-NLS-1$
			final String substring = text.substring(0, text.lastIndexOf(','));
			newWKT = substring.substring(0, substring.lastIndexOf(',')) + ", AUTHORITY[\"UDIG\",\"" + (lastID - 1) //$NON-NLS-1$
					+ "\"]]"; //$NON-NLS-1$
		} else {
			newWKT = text.substring(0, text.lastIndexOf(']')) + ", AUTHORITY[\"UDIG\",\"" + (lastID - 1) + "\"]]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		wktText.setText(newWKT);
		return newWKT;
	}

	public void setController(final Controller controller) {
		parentPage = controller;
	}

}
