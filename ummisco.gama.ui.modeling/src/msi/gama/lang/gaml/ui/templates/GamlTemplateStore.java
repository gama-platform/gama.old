/*******************************************************************************************************
 *
 * GamlTemplateStore.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.templates;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateStore;

import com.google.inject.Inject;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import msi.gama.lang.gaml.ui.templates.GamlTemplateStore.GamlTemplateStoreProvider;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;
import msi.gaml.types.Signature;

/**
 * The class GamlTemplateStore. Loads template and gives them a unique index.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
@SuppressWarnings ("deprecation")
@ProvidedBy (GamlTemplateStoreProvider.class)
public class GamlTemplateStore extends XtextTemplateStore {

	/**
	 * The Class GamlTemplateStoreProvider.
	 */
	public static class GamlTemplateStoreProvider implements Provider<GamlTemplateStore> {

		/** The instance. */
		static GamlTemplateStore instance;

		/** The context type registry. */
		@Inject private ContextTypeRegistry contextTypeRegistry;

		/** The language name. */
		@Inject @Named (Constants.LANGUAGE_NAME) private String languageName;

		/** The store. */
		@Inject private IPreferenceStore store;

		/** The plugin. */
		@Inject private AbstractUIPlugin plugin;

		/**
		 * @see com.google.inject.Provider#get()
		 */
		@Override
		public GamlTemplateStore get() {
			if (instance == null) {
				instance = new GamlTemplateStore(contextTypeRegistry, store, languageName, plugin);
			}
			return getInstance();
		}

		/**
		 * Gets the single instance of GamlTemplateStoreProvider.
		 *
		 * @return single instance of GamlTemplateStoreProvider
		 */
		public static GamlTemplateStore getInstance() { return instance; }

	}

	/** The indexes. */
	static Map<String, Integer> indexes = new HashMap<>();

	/**
	 * @param registry
	 * @param store
	 * @param key
	 * @param plugin
	 */
	/* @Inject */
	public GamlTemplateStore(final ContextTypeRegistry registry, final IPreferenceStore store,
			/* @Named(Constants.LANGUAGE_NAME) */final String key, final AbstractUIPlugin plugin) {
		super(registry, store, key, plugin);
	}

	/**
	 * Gets the new id from id.
	 *
	 * @param id the id
	 * @return the new id from id
	 */
	public String getNewIdFromId(final String id) {
		String newId = "";
		String[] strings = id.split("\\.");
		final String last = strings[strings.length - 1];
		Integer index;
		if (Strings.isGamaNumber(last)) {
			index = Integer.decode(last);
			strings = Arrays.copyOf(strings, strings.length - 1);
			for (final String s : strings) { newId += s + "."; }
			newId = newId.substring(0, newId.length() - 1);
			if (indexes.containsKey(newId)) { index = indexes.get(newId); }
			indexes.put(newId, index + 1);
		} else {
			newId = id;
			index = indexes.get(id);
			if (index == null) {
				index = 1;
			} else {
				index++;
			}
			indexes.put(id, index);
		}
		return newId + "." + index;

	}

	/**
	 * Adds a template to the internal store. The added templates must have a unique id.
	 *
	 * @param data
	 *            the template data to add
	 */
	@Override
	protected void internalAdd(final TemplatePersistenceData data) {
		if (!data.isCustom()) {
			// give the data a new unique id
			final String id = getNewIdFromId(data.getId());
			final TemplatePersistenceData d2 = new TemplatePersistenceData(data.getTemplate(), true, id);
			super.internalAdd(d2);
		}
	}

	/**
	 * Direct add.
	 *
	 * @param data the data
	 * @param isEdited the is edited
	 */
	public void directAdd(final TemplatePersistenceData data, final boolean isEdited) {
		if (isEdited) {
			add(data);
		} else {
			// Trick the template store so that (1) the template is considered
			// as "user added" while (2) maintaining a proper id
			final String id = data.getId();
			final TemplatePersistenceData d2 = new TemplatePersistenceData(data.getTemplate(), true) {

				@Override
				public String getId() { return id; }

			};
			add(d2);
		}
	}

	@Override
	public org.eclipse.jface.text.templates.persistence.TemplatePersistenceData getTemplateData(final String id) {
		if (id == null) return null;
		for (final org.eclipse.text.templates.TemplatePersistenceData data : internalGetTemplates()) {
			if (id.equals(data.getId())) {
				return new org.eclipse.jface.text.templates.persistence.TemplatePersistenceData(data);
			}

		}
		return null;
	}

	@Override
	protected void loadContributedTemplates() throws IOException {
		super.loadContributedTemplates();
		Iterable<String> protos = DescriptionFactory.getProtoNames();
		for (final String keyword : protos) {
			final SymbolProto sp = DescriptionFactory.getProto(keyword, null);
			// List<template> templates = sp.getTemplates();
			for (final usage u : sp.getUsages()) {
				final TemplatePersistenceData data = GamlTemplateFactory.from(u, sp);
				if (data != null) { internalAdd(data); }
			}
		}
		protos = GAML.OPERATORS.keySet();
		for (final String keyword : protos) {
			final Map<Signature, OperatorProto> map = GAML.OPERATORS.get(keyword);
			for (final OperatorProto p : map.values()) {
				for (final usage u : p.getUsages()) {
					final TemplatePersistenceData data = GamlTemplateFactory.from(u, p);
					if (data != null) { internalAdd(data); }
				}
			}
		}
	}

}
