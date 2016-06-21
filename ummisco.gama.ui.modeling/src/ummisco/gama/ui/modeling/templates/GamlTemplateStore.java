/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package ummisco.gama.ui.modeling.templates;

import java.io.IOException;
import java.util.*;

import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;
import msi.gaml.types.Signature;
import ummisco.gama.ui.modeling.templates.GamlTemplateStore.GamlTemplateStoreProvider;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateStore;
import com.google.inject.*;
import com.google.inject.name.Named;

/**
 * The class GamlTemplateStore. Loads template and gives them a unique index.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
@ProvidedBy(GamlTemplateStoreProvider.class)
public class GamlTemplateStore extends XtextTemplateStore {

	public static class GamlTemplateStoreProvider implements Provider<GamlTemplateStore> {

		static GamlTemplateStore instance;

		@Inject
		private ContextTypeRegistry contextTypeRegistry;

		@Inject
		@Named(Constants.LANGUAGE_NAME)
		private String languageName;

		@Inject
		private IPreferenceStore store;

		@Inject
		private AbstractUIPlugin plugin;

		/**
		 * @see com.google.inject.Provider#get()
		 */
		@Override
		public GamlTemplateStore get() {
			if ( instance == null ) {
				instance = new GamlTemplateStore(contextTypeRegistry, store, languageName, plugin);
			}
			return getInstance();
		}

		public static GamlTemplateStore getInstance() {
			return instance;
		}

	}

	static Map<String, Integer> indexes = new HashMap();

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

	public String getNewIdFromId(final String id) {
		String newId = "";
		String[] strings = id.split("\\.");
		String last = strings[strings.length - 1];
		Integer index;
		if ( Strings.isGamaNumber(last) ) {
			index = Integer.decode(last);
			strings = Arrays.copyOf(strings, strings.length - 1);
			for ( String s : strings ) {
				newId += s + ".";
			}
			newId = newId.substring(0, newId.length() - 1);
			if ( indexes.containsKey(newId) ) {
				index = indexes.get(newId);
			}
			indexes.put(newId, index + 1);
		} else {
			newId = id;
			index = indexes.get(id);
			if ( index == null ) {
				index = 1;
				indexes.put(id, index);
			} else {
				index++;
				indexes.put(id, index);
			}
		}
		return newId + "." + index;

	}

	/**
	 * Adds a template to the internal store. The added templates must have
	 * a unique id.
	 * 
	 * @param data the template data to add
	 */
	@Override
	protected void internalAdd(final TemplatePersistenceData data) {
		if ( !data.isCustom() ) {
			// give the data a new unique id
			String id = getNewIdFromId(data.getId());
			TemplatePersistenceData d2 = new TemplatePersistenceData(data.getTemplate(), true, id);
			super.internalAdd(d2);
		}
	}

	public void directAdd(final TemplatePersistenceData data, final boolean isEdited) {
		if ( isEdited ) {
			add(data);
		} else {
			// Trick the template store so that (1) the template is considered as "user added" while (2) maintaining a proper id
			final String id = data.getId();
			final TemplatePersistenceData d2 = new TemplatePersistenceData(data.getTemplate(), true) {

				@Override
				public String getId() {
					return id;
				}

			};
			add(d2);
		}
	}

	@Override
	protected void loadContributedTemplates() throws IOException {
		super.loadContributedTemplates();
		Iterable<String> protos = DescriptionFactory.getProtoNames();
		for ( String keyword : protos ) {
			SymbolProto sp = DescriptionFactory.getProto(keyword, null);
			// List<template> templates = sp.getTemplates();
			List<usage> usages = sp.getUsages();
			for ( usage u : usages ) {
				TemplatePersistenceData data = GamlTemplateFactory.from(u, sp);
				if ( data != null ) {
					internalAdd(data);
				}
			}
		}
		protos = IExpressionCompiler.OPERATORS.keySet();
		for ( String keyword : protos ) {
			Map<Signature, OperatorProto> map = IExpressionCompiler.OPERATORS.get(keyword);
			for ( OperatorProto p : map.values() ) {
				List<usage> usages = p.getUsages();
				for ( usage u : usages ) {
					TemplatePersistenceData data = GamlTemplateFactory.from(u, p);
					if ( data != null ) {
						internalAdd(data);
					}
				}
			}
		}
	}

}
