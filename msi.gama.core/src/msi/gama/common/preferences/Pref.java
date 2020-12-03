/*******************************************************************************************************
 *
 * msi.gama.common.preferences.Pref.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.preferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class Pref<T> implements IParameter {

	@FunctionalInterface
	public interface ValueProvider<T> {

		T get();
	}

	static int ORDER = 0;

	private final int order = ORDER++;
	private final boolean inGaml;

	String key, title, tab, group, comment;
	boolean disabled = false; // by default
	boolean hidden = false; // by default
	boolean restartRequired = false; // by default
	ValueProvider<T> initialProvider;
	T value, initial;
	final int type;
	List<T> values;
	Number min, max;
	boolean slider = true; // by default
	String[] enables, disables, refreshes;
	Set<IPreferenceChangeListener<T>> listeners = new HashSet<>();
	// private T[] v;

	Pref(final String key, final int type, final boolean inGaml) {
		this.type = type;
		this.key = key;
		this.inGaml = inGaml;
	}

	public Pref<T> disabled() {
		disabled = true;
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public Pref<T> onChange(final IPreferenceAfterChangeListener<T> consumer) {
		addChangeListener(consumer);
		return this;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public Pref<T> among(@SuppressWarnings ("unchecked") final T... v) {
		return among(Arrays.asList(v));
	}

	public Pref<T> among(final List<T> v) {
		this.values = v;
		return this;
	}

	public Pref<T> between(final Number mini, final Number maxi) {
		this.min = mini;
		this.max = maxi;
		return this;
	}

	public Pref<T> in(final String category, final String aGroup) {
		this.tab = category;
		this.group = aGroup;
		return this;
	}

	public Pref<T> withComment(final String aComment) {
		setUnitLabel(aComment);
		return this;
	}

	public Pref<T> named(final String t) {
		this.title = t;
		// this.title = t + " [" + key + "]";
		return this;
	}

	public Pref<T> init(final T v) {
		this.initial = v;
		this.value = v;
		return this;
	}

	public Pref<T> init(final ValueProvider<T> p) {
		initialProvider = p;
		return this;
	}

	public Pref<T> set(final T value) {
		if (isValueChanged(value) && acceptChange(value)) {
			this.value = value;
			afterChange(value);
		}
		return this;
	}

	private boolean isValueChanged(final T newValue) {
		return value == null ? newValue != null : !value.equals(newValue);
	}

	public Pref<T> activates(final String... link) {
		enables = link;
		return this;
	}

	public Pref<T> deactivates(final String... link) {
		disables = link;
		return this;
	}

	public Pref<T> refreshes(final String... link) {
		refreshes = link;
		return this;
	}

	public T getValue() {
		if (initialProvider != null) {
			init(initialProvider.get());
			initialProvider = null;
		}
		return value;
	}

	@Override
	public IType<?> getType() {
		return Types.get(type);
	}

	@Override
	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	public List<T> getValues() {
		return values;
	}

	@Override
	public String getName() {
		return key;
	}

	@Override
	public String getCategory() {
		return group;
	}

	@Override
	public String getUnitLabel(final IScope scope) {
		return comment;
	}

	@Override
	public void setUnitLabel(final String label) {
		comment = label;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void setValue(final IScope scope, final Object value) {
		set((T) value);
	}

	public Pref<T> addChangeListener(final IPreferenceChangeListener<T> r) {
		listeners.add(r);
		return this;
	}

	public void removeChangeListener(final IPreferenceChangeListener<T> r) {
		listeners.remove(r);
	}

	public void removeChangeListeners() {
		listeners.clear();
	}

	@Override
	public T value(final IScope scope) throws GamaRuntimeException {
		return getValue();
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(value, includingBuiltIn);
	}

	@Override
	public Object getInitialValue(final IScope scope) {
		if (initialProvider != null) {
			init(initialProvider.get());
			initialProvider = null;
		}
		return initial;
	}

	@Override
	public Number getMinValue(final IScope scope) {
		return min;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		return max;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		return values;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setDefined(final boolean b) {}

	@Override
	public Number getStepValue(final IScope scope) {
		return null;
	}

	/**
	 * If the value is modified, this method is called. Should return true to accept the change, false otherwise
	 */
	public boolean acceptChange(final T newValue) {
		for (final IPreferenceChangeListener<T> listener : listeners) {
			if (!listener.beforeValueChange(newValue)) { return false; }
		}
		return true;
	}

	protected void afterChange(final T newValue) {
		initialProvider = null;
		for (final IPreferenceChangeListener<T> listener : listeners) {
			listener.afterValueChange(newValue);
		}
	}

	@Override
	public String[] getEnablement() {
		return this.enables;
	}

	@Override
	public String[] getDisablement() {
		return this.disables;
	}

	public String[] getRefreshment() {
		return this.refreshes;
	}

	public void save() {
		final Map<String, Object> map = GamaMapFactory.createUnordered();
		map.put(getName(), getValue());
		GamaPreferences.setNewPreferences(map);
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		return slider;
	}

	public Pref<T> hidden() {
		hidden = true;
		return this;
	}

	public boolean isHidden() {
		return hidden;
	}

	public Pref<T> restartRequired() {
		restartRequired = true;
		return this;
	}

	public boolean isRestartRequired() {
		return restartRequired;
	}

	public boolean inGaml() {
		return inGaml;
	}

	@Override
	public List<GamaColor> getColor(final IScope scope) {
		return null;
	}

}