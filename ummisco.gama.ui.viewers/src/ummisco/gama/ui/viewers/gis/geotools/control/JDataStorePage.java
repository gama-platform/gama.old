/*********************************************************************************************
 *
 * 'JDataStorePage.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.Parameter;

/**
 * Data store wizard page for the {@link JDataStoreWizard data store wizard}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class JDataStorePage extends WizardPage {
	public static final String ID = "ummisco.gama.ui.viewers.gis.geotools.data.DataStorePage";

	/**
	 * Factory for which we are collection connection parameters
	 */
	protected DataStoreFactorySpi format;

	/** Map of user interface ParamFields displayed to the user */
	private final Map<Param, ParamField> fields = new HashMap<Param, ParamField>();

	/** Connection params for datastore */
	protected Map<String, Object> connectionParameters;

	/** level of parameters to display */
	private String level = null;

	public JDataStorePage(final DataStoreFactorySpi format) {
		this(format, null);
	}

	public JDataStorePage(final DataStoreFactorySpi format, Map<String, Object> params) {
		super(ID);
		if (format != null) {
			setTitle(format.getDisplayName());
			setDescription(format.getDescription());
		}

		this.format = format;
		if (params == null) {
			params = new HashMap<String, Object>();
			if (format != null) {
				for (final Param param : format.getParametersInfo()) {
					params.put(param.key, param.sample);
				}
			}
		}
		this.connectionParameters = params;
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			preDisplayPanel();
		} else {
			preClosePanel();
		}

		super.setVisible(visible);
	}

	@Override
	public void createControl(final Composite parent) {

		final Composite mainComposite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(2, false);
		mainComposite.setLayout(gridLayout);

		for (final Param param : format.getParametersInfo()) {
			if (level != null) {
				String check = param.metadata == null ? "user" : (String) param.metadata.get(Parameter.LEVEL);

				if (check == null) {
					check = "user";
				}
				if (level.equals(check)) {
					// we are good this is the one we want
				} else {
					continue; // skip since it is not the one we want
				}
			}
			String txt = param.title.toString();
			if (param.required) {
				txt += "*";
			}

			final Label label = new Label(mainComposite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			label.setText(txt);

			final ParamField field = ParamField.create(mainComposite, param);
			field.doLayout();

			fields.put(param, field);

			// if (param.description != null) {
			// JLabel info = new
			// JLabel(formatDescription(param.description.toString()));
			// page.add(info, "skip, span, wrap");
			// }
		}

		setControl(mainComposite);

	}

	private void preDisplayPanel() {
		// populate panel from params map
		for (final Entry<Param, ParamField> entry : fields.entrySet()) {
			final Param param = entry.getKey();
			final ParamField field = entry.getValue();
			Object value = null;
			try {
				value = param.lookUp(connectionParameters);
			} catch (final IOException e) {
			}
			if (value == null && param.required) {
				value = param.sample;
			}
			field.setValue(value);
		}
		// for( Entry<Param, ParamField> entry : fields.entrySet() ) {
		// ParamField field = entry.getValue();
		// TODO field.addListener(getWizard().getController());
		// }
	}

	private void preClosePanel() {
		for (final Entry<Param, ParamField> entry : fields.entrySet()) {
			final Param param = entry.getKey();
			final ParamField field = entry.getValue();

			final Object value = field.getValue();
			connectionParameters.put(param.key, value);
			field.setValue(value);
		}
		// for( Entry<Param, ParamField> entry : fields.entrySet() ) {
		// ParamField field = entry.getValue();
		// TODO field.removeListener(getJWizard().getController());
		// }
	}

	public void setLevel(final String level) {
		this.level = level;
	}

	public void setFormat(final DataStoreFactorySpi format) {
		if (this.format != format) {
			this.format = format;
		}
	}

	public boolean isValid() {
		// populate panel
		for (final Entry<Param, ParamField> entry : fields.entrySet()) {
			if (!entry.getValue().validate()) {
				return false; // not validate
			}
			if (entry.getKey().required && entry.getValue().getValue() == null) {

			}
		}
		return true;
	}

	// private String formatDescription( String desc ) {
	// String prefix = "<html>";
	// final int LEN = desc.length();
	//
	// if (LEN < MAX_DESCRIPTION_WIDTH) {
	// return prefix + desc;
	// } else {
	// StringBuffer sb = new StringBuffer(prefix);
	// StringTokenizer tokenizer = new StringTokenizer(desc);
	//
	// int n = 0;
	// while( tokenizer.hasMoreTokens() ) {
	// String word = tokenizer.nextToken();
	// if (n + word.length() + 1 > MAX_DESCRIPTION_WIDTH) {
	// sb.append("<br>");
	// n = 0;
	// }
	// sb.append(word);
	// sb.append(' ');
	// n += word.length() + 1;
	// }
	//
	// return sb.toString();
	// }
	// }
}
