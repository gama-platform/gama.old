/*********************************************************************************************
 *
 * 'BoxSettingsTab.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class BoxSettingsTab {

	protected IBoxProvider provider;
	protected IBoxSettingsStore store;
	protected IBoxSettings settings;
	protected IBoxDecorator decorator;

	Button enabled;
	Combo combo;
	Combo borderWidth;
	Button roundBox;
	Combo highlightWidth;
	Button highlightOne;
	ColorSelector fillSelectedColor;
	Button fillSelected;
	Combo builderCombo;
	ColorSelector fromColorLab;
	ColorSelector toColorLab;
	StyledText st;
	Button bordertDrawLine;
	Button highlightDrawLine;
	Button fillGradient;
	ColorSelector fillGradientColor;
	Button fillOnMove;
	Button circulateColors;
	Combo levels;
	Combo fillKey;
	protected boolean changed;
	Composite composite;
	ColorSelector borderColorSelector;
	Combo borderColorType;
	Combo highlightColorType;
	ColorSelector highlightColorSelector;
	Button genGradientBut;
	Combo borderLineStyle;
	Combo highlightLineStyle;
	Button noBackground;
	Button eolBox;
	Scale scale;
	Spinner spinner;

	public BoxSettingsTab() {}

	public Control createContro(final Composite parent, final IBoxProvider provider0) {
		provider = provider0;
		if (provider == null) {
			final Label l = new Label(parent, SWT.NONE);
			l.setText("Error - cannot make configuration");
			return l;
		}
		store = provider.getSettingsStore();
		settings = provider.createSettings();
		decorator = provider.createDecorator();
		decorator.setSettings(settings);
		final Control result = createContents0(parent);
		updateContents();
		decorator.setStyledText(st);
		decorator.decorate(true);
		decorator.enableUpdates(true);
		settings.addPropertyChangeListener(event -> {
			changed = true;
			if (event.getProperty().equals(IBoxSettings.PropertiesKeys.Color.name())) {
				updateFromToColors();
			}

			provider.getEditorsBoxSettings().copyFrom(settings);
		});

		return result;
	}

	protected Control createContents0(final Composite parent) {
		final int N = 6;
		final Composite c = new Composite(parent, SWT.NONE);
		this.composite = c;
		final GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 1;
		layout.marginWidth = 1;
		layout.numColumns = N;
		c.setLayout(layout);
		c.setSize(200, 400);

		enabled = new Button(c, SWT.CHECK);
		GridData gd = new GridData();
		enabled.setLayoutData(gd);
		enabled.setText("Enabled");
		enabled.setAlignment(SWT.RIGHT);
		enabled.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setEnabled(enabled.getSelection());
			}
		});

		newButton(c, "Export", new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFileName(settings.getName());
				dialog.setFilterExtensions(new String[] { "*.eb" });
				dialog.setText("Editbox settings");
				final String file = dialog.open();
				if (file != null) {
					try {
						settings.export(new FileOutputStream(file));
					} catch (final Exception ex) {
						// EditBox.logError(this, "Failed to export EditBox
						// setttings", ex);
						final MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
						mb.setText("Failed to export configuration: " + ex.getMessage());
						mb.open();
					}

				}
				super.widgetSelected(e);
			}

		});

		final Button importConfig = newButton(c, "Import", new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFileName(settings.getName());
				dialog.setFilterExtensions(new String[] { "*.eb" });
				dialog.setText("Editbox settings");
				final String file = dialog.open();
				if (file != null) {
					try {
						final IBoxSettings newSettings = provider.createSettings();
						newSettings.load(new FileInputStream(file));
						newSettings.setEnabled(settings.getEnabled());
						settings.copyFrom(newSettings);
						updateContents();
					} catch (final Exception ex) {
						// EditBox.logError(this, "Failed to import EditBox
						// setttings", ex);
						final MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
						mb.setText("Failed to load configuration: " + ex.getMessage());
						mb.open();
					}
				}
			}

		});

		gd = new GridData();
		gd.horizontalSpan = 4;
		importConfig.setLayoutData(gd);

		newLabel(c, "Enter/select theme");
		combo = new Combo(c, SWT.DROP_DOWN);
		gd = new GridData(GridData.BEGINNING);
		gd.widthHint = 150;
		gd.horizontalSpan = 4;
		gd.horizontalAlignment = SWT.FILL;
		combo.setLayoutData(gd);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String s = combo.getText();
				if (s != null && s.length() > 0) {
					decorator.enableUpdates(false);
					store.load(s, settings);
					updateContents();
					decorator.enableUpdates(true);
				}
			}
		});

		final Button removeConfig = new Button(c, SWT.NONE);
		removeConfig.setText("Remove");
		removeConfig.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String t = combo.getText();
				if (t != null && t.length() > 0) {
					final int si = combo.indexOf(t);
					if (si > -1) {
						combo.remove(si);
						store.remove(t);
					} else {
						combo.setText("");
					}
				}
			}
		});

		final Label bl = newLabel(c, "Box border:");
		gd = new GridData();
		gd.horizontalSpan = N;
		gd.horizontalAlignment = SWT.FILL;
		bl.setLayoutData(gd);

		final Composite c1 = new Composite(c, SWT.NONE);
		final GridLayout ly = new GridLayout();
		ly.horizontalSpacing = 0;
		ly.marginWidth = 0;
		ly.numColumns = 2;
		c1.setLayout(layout);
		c1.setLayoutData(new GridData());

		newLabel(c1, " style");
		borderLineStyle =
				newCombo(c1, new String[] { "Solid", "Dot", "Dash", "DashDot", "DashDotDot" }, new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						settings.setBorderLineStyle(borderLineStyle.getSelectionIndex());
					}
				});
		borderLineStyle.select(0);

		final Composite c2 = new Composite(c, SWT.NONE);
		final GridLayout ly2 = new GridLayout();
		ly2.horizontalSpacing = 0;
		ly2.numColumns = 2;
		c2.setLayout(layout);
		c2.setLayoutData(new GridData());

		newLabel(c2, "color");
		borderColorType = newCombo(c2, new String[] { "Custom", "Dark", "Darker", "Darkest" }, new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int idx = borderColorType.getSelectionIndex();
				settings.setBorderColorType(idx);
				borderColorSelector.getButton().setEnabled(idx == 0);
			}
		});
		borderColorType.select(0);

		borderColorSelector = new ColorSelector(c);
		borderColorSelector.addListener(event -> settings.setBorderRGB(borderColorSelector.getColorValue()));

		final Label l0 = newLabel(c, "width");
		l0.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		borderWidth = newCombo(c, new String[] { "0", "1", "2", "3", "4", "5" }, new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setBorderWidth(borderWidth.getSelectionIndex());
			}
		});

		bordertDrawLine = new Button(c, SWT.CHECK);
		bordertDrawLine.setText("Line");
		bordertDrawLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setBorderDrawLine(bordertDrawLine.getSelection());
			}
		});

		final Label hl = newLabel(c, "Highlight selected box:");
		gd = new GridData();
		gd.horizontalSpan = N;
		gd.horizontalAlignment = SWT.FILL;
		hl.setLayoutData(gd);

		final Composite c3 = new Composite(c, SWT.NONE);
		final GridLayout ly3 = new GridLayout();
		ly3.horizontalSpacing = 0;
		ly3.marginWidth = 0;
		ly3.numColumns = 2;
		c3.setLayout(layout);
		c3.setLayoutData(new GridData());

		newLabel(c3, " style");
		highlightLineStyle =
				newCombo(c3, new String[] { "Solid", "Dot", "Dash", "DashDot", "DashDotDot" }, new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						settings.setHighlightLineStyle(highlightLineStyle.getSelectionIndex());
					}
				});
		highlightLineStyle.select(0);

		final Composite c4 = new Composite(c, SWT.NONE);
		final GridLayout ly5 = new GridLayout();
		ly5.horizontalSpacing = 0;
		ly5.numColumns = 2;
		c4.setLayout(layout);
		c4.setLayoutData(new GridData());

		newLabel(c4, "color");
		highlightColorType =
				newCombo(c4, new String[] { "Custom", "Dark", "Darker", "Darkest" }, new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						final int idx = highlightColorType.getSelectionIndex();
						settings.setHighlightColorType(idx);
						highlightColorSelector.getButton().setEnabled(idx == 0);
					}
				});
		highlightColorType.select(0);

		highlightColorSelector = new ColorSelector(c);
		highlightColorSelector.addListener(event -> settings.setHighlightRGB(highlightColorSelector.getColorValue()));

		final Label l = newLabel(c, "width");
		l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		highlightWidth = newCombo(c, new String[] { "0", "1", "2", "3", "4", "5" }, new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setHighlightWidth(highlightWidth.getSelectionIndex());
			}
		});

		highlightDrawLine = new Button(c, SWT.CHECK);
		highlightDrawLine.setText("Line");
		highlightDrawLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setHighlightDrawLine(highlightDrawLine.getSelection());
			}
		});

		roundBox = new Button(c, SWT.CHECK);
		roundBox.setText("Round box");
		roundBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setRoundBox(roundBox.getSelection());
				if (roundBox.getSelection()) {
					fillGradient.setSelection(false);
					settings.setFillGradient(false);
				}
			}
		});

		highlightOne = new Button(c, SWT.CHECK);
		highlightOne.setText("Highlight one");
		highlightOne.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setHighlightOne(highlightOne.getSelection());
			}
		});

		eolBox = new Button(c, SWT.CHECK);
		eolBox.setText("Expand box");
		eolBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setExpandBox(eolBox.getSelection());
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 2;
		eolBox.setLayoutData(gd);

		noBackground = new Button(c, SWT.CHECK);
		noBackground.setText("No background");
		noBackground.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setNoBackground(noBackground.getSelection());
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 2;
		noBackground.setLayoutData(gd);

		fillSelected = new Button(c, SWT.CHECK);
		fillSelected.setText("Fill selected box");
		fillSelected.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setFillSelected(fillSelected.getSelection());
			}
		});

		fillSelectedColor = new ColorSelector(c);
		fillSelectedColor.addListener(e -> settings.setFillSelectedRGB(fillSelectedColor.getColorValue()));
		fillSelectedColor.getButton().setLayoutData(new GridData(GridData.BEGINNING));

		fillOnMove = new Button(c, SWT.CHECK);
		fillOnMove.setText("On move");
		fillOnMove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setFillOnMove(fillOnMove.getSelection());
			}
		});

		newLabel(c, "with key");
		gd = new GridData();
		gd.horizontalSpan = 2;
		fillOnMove.setLayoutData(gd);

		fillKey = newCombo(c, new String[] { "", "Alt", "Ctrl", "Shift" }, new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setFillKeyModifier(fillKey.getText());
			}
		});

		fillGradient = new Button(c, SWT.CHECK);
		fillGradient.setText("Make gradient");
		fillGradient.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setFillGradient(fillGradient.getSelection());
				if (fillGradient.getSelection()) {
					roundBox.setSelection(false);
					settings.setRoundBox(false);
				}
			}
		});

		fillGradientColor = new ColorSelector(c);
		fillGradientColor.addListener(event -> settings.setFillGradientColorRGB(fillGradientColor.getColorValue()));

		final Label la = newLabel(c, "Alpha blending");
		la.setToolTipText("Can slow down box drawing");
		gd = new GridData();
		gd.horizontalSpan = 2;
		la.setLayoutData(gd);

		scale = new Scale(c, SWT.HORIZONTAL);
		scale.setToolTipText("Can slow down box drawing");
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 80;
		scale.setLayoutData(gd);
		scale.setMinimum(0);
		scale.setMinimum(255);
		scale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int alpha = scale.getSelection() * 255 / 100;
				spinner.setSelection(alpha);
				settings.setAlpha(alpha);
			}
		});

		spinner = new Spinner(c, SWT.NONE);
		spinner.setToolTipText("Can slow down box drawing");
		spinner.setMinimum(0);
		spinner.setMaximum(255);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				scale.setSelection(spinner.getSelection() * 100 / 255);
				settings.setAlpha(spinner.getSelection());
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 1;
		spinner.setLayoutData(gd);

		newLabel(c, "Color levels");
		levels = newCombo(c,
				new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" },
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						settings.setColorsSize(levels.getSelectionIndex());
						st.setText(generateIndentText(settings.getColorsSize() + 1));
					}
				});

		circulateColors = new Button(c, SWT.CHECK);
		circulateColors.setText("Circulate colors");
		circulateColors.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setCirculateLevelColors(circulateColors.getSelection());
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 4;
		circulateColors.setLayoutData(gd);

		newLabel(c, "Syntax");
		builderCombo = new Combo(c, SWT.READ_ONLY);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 5;
		builderCombo.setLayoutData(gd);
		builderCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				settings.setBuilder(builderCombo.getText());
			}
		});

		newLabel(c, "Gradient tool");

		final Composite c6 = new Composite(c, SWT.NONE);
		final GridLayout ly6 = new GridLayout();
		ly6.horizontalSpacing = 0;
		ly6.marginWidth = 0;
		ly6.numColumns = 2;
		c6.setLayout(layout);
		c6.setLayoutData(new GridData());

		newLabel(c6, "from color");

		fromColorLab = new ColorSelector(c6);
		fromColorLab.getButton().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		newLabel(c, "to");
		toColorLab = new ColorSelector(c);

		final IPropertyChangeListener listener = event -> genGradientBut
				.setEnabled(toColorLab.getColorValue() != null && fromColorLab.getColorValue() != null);
		fromColorLab.addListener(listener);
		toColorLab.addListener(listener);

		genGradientBut = newButton(c, "Generate", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (fromColorLab.getColorValue() == null || toColorLab.getColorValue() == null) { return; }
				final Color[] colors = settings.getColors();
				if (colors == null || colors.length < 2) { return; }
				settings.setColorsRGB(rgbGradient(colors));
			}
		});

		final Label l1 = newLabel(c, "Preview - double click on any box to change color:");
		gd = new GridData();
		gd.horizontalSpan = N;
		gd.horizontalAlignment = SWT.FILL;
		l1.setLayoutData(gd);

		st = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.FULL_SELECTION);
		gd = new GridData();
		gd.horizontalSpan = N;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.heightHint = 50;
		st.setLayoutData(gd);
		st.setEditable(false);
		st.setToolTipText("Double click to change color");
		return c;
	}

	protected Shell getShell() {
		if (composite != null) { return composite.getShell(); }
		return null;
	}

	protected Combo newCombo(final Composite c, final String[] items, final SelectionListener listener) {
		final Combo combo1 = new Combo(c, SWT.READ_ONLY);
		combo1.setItems(items);
		combo1.addSelectionListener(listener);
		return combo1;
	}

	protected Button newButton(final Composite c, final String name, final SelectionAdapter selectionAdapter) {
		final Button b = new Button(c, SWT.NONE);
		b.setText(name);
		b.addSelectionListener(selectionAdapter);
		return b;
	}

	protected Label newLabel(final Composite c, final String msg) {
		final Label l = new Label(c, SWT.NONE);
		l.setText(msg);
		final Color bc = c.getBackground();
		if (bc != null) {
			l.setBackground(new Color(null, bc.getRGB()));
		}
		return l;
	}

	protected void updateContents() {
		enabled.setSelection(settings.getEnabled());
		combo.setItems(store.getCatalog().toArray(new String[0]));
		if (settings.getName() != null) {
			final int idx = combo.indexOf(settings.getName());
			if (idx > -1) {
				combo.select(idx);
			} else {
				combo.setText(settings.getName());
			}
		}
		if (settings.getBorderColor() != null) {
			borderColorSelector.setColorValue(settings.getBorderColor().getRGB());
		}
		borderWidth.select(settings.getBorderWidth());
		roundBox.setSelection(settings.getRoundBox());
		if (settings.getHighlightColor() != null) {
			highlightColorSelector.setColorValue(settings.getHighlightColor().getRGB());
		}
		highlightWidth.select(settings.getHighlightWidth());
		highlightOne.setSelection(settings.getHighlightOne());
		fillSelected.setSelection(settings.getFillSelected());
		if (settings.getFillSelectedColor() != null) {
			fillSelectedColor.setColorValue(settings.getFillSelectedColor().getRGB());
		}
		builderCombo.setItems(provider.getBuilders().toArray(new String[0]));
		int i = -1;
		if (settings.getBuilder() != null) {
			i = builderCombo.indexOf(settings.getBuilder());
		}
		builderCombo.select(i == -1 ? 0 : i);
		st.setText(generateIndentText(settings.getColorsSize() + 1));
		updateFromToColors();
		bordertDrawLine.setSelection(settings.getBorderDrawLine());
		highlightDrawLine.setSelection(settings.getHighlightDrawLine());
		fillGradient.setSelection(settings.getFillGradient());
		if (settings.getFillGradientColor() != null) {
			fillGradientColor.setColorValue(settings.getFillGradientColor().getRGB());
		}
		fillOnMove.setSelection(settings.getFillOnMove());
		circulateColors.setSelection(settings.getCirculateLevelColors());
		levels.select(settings.getColorsSize());
		fillKey.setText(settings.getFillKeyModifier() == null ? "" : settings.getFillKeyModifier());
		borderColorType.select(settings.getBorderColorType());
		highlightColorType.select(settings.getHighlightColorType());
		highlightColorSelector.getButton().setEnabled(settings.getHighlightColorType() == 0);
		borderColorSelector.getButton().setEnabled(settings.getBorderColorType() == 0);
		borderLineStyle.select(settings.getBorderLineStyle());
		highlightLineStyle.select(settings.getHighlightLineStyle());
		noBackground.setSelection(settings.getNoBackground());
		eolBox.setSelection(settings.getExpandBox());
		spinner.setSelection(settings.getAlpha());
		scale.setSelection(settings.getAlpha() * 100 / 255);
	}

	private void updateFromToColors() {
		final Color[] c = settings.getColors();
		if (c != null && c.length > 1) {
			updateBackground(fromColorLab, c[0]);
			updateBackground(toColorLab, c[c.length - 1]);
		} else {
			genGradientBut.setEnabled(false);
		}
	}

	protected void updateBackground(final ColorSelector ctrl, final Color c) {
		if (c == null) {
			genGradientBut.setEnabled(false);
		} else {
			ctrl.setColorValue(c.getRGB());
		}
	}

	public void dispose() {
		if (settings != null) {
			settings.dispose();
		}
	}

	RGB[] rgbGradient(final Color[] c) {
		final int n = c.length - 1;
		final RGB c1 = fromColorLab.getColorValue();
		final RGB c2 = toColorLab.getColorValue();
		final int VR = (c2.red - c1.red) / n;
		final int VG = (c2.green - c1.green) / n;
		final int VB = (c2.blue - c1.blue) / n;

		final RGB[] gradient = new RGB[n + 1];
		gradient[0] = c1;
		for (int i = 1; i <= n; i++) {
			final RGB prev = gradient[i - 1];
			gradient[i] = new RGB(prev.red + VR, prev.green + VG, prev.blue + VB);
		}
		return gradient;
	}

	String generateIndentText(final int n) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				sb.append("  ");
			}
			sb.append("level " + (i + 1 == n ? "n" : i + 1));
			sb.append("\n");
		}
		return sb.toString();
	}

	public IBoxProvider getProvider() {
		return provider;
	}

	public IBoxSettings getSettings() {
		return settings;
	}

	public String validate() {
		settings.setName(combo.getText());
		if (settings.getName() == null || settings.getName().length() == 0) { return "Enter configuration name"; }
		return null;
	}

	public void save() {
		if (changed) {
			store.saveDefaults(settings);
			provider.getEditorsBoxSettings().copyFrom(settings);
		}
	}

	public void cancel() {
		if (changed) {
			store.loadDefaults(provider.getEditorsBoxSettings());
		}
	}
}
