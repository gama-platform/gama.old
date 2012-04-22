/**
 * Created by drogoul, 24 janv. 2012
 * 
 */
package msi.gaml.compilation;

import static msi.gaml.compilation.GamaCompiler.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import msi.gama.common.util.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaStartupException;
import msi.gaml.factories.*;
import msi.gaml.skills.*;
import msi.gaml.types.*;
import org.eclipse.core.runtime.*;
import org.osgi.framework.*;

/**
 * The class GamaBundleLoader.
 * 
 * @author drogoul
 * @since 24 janv. 2012
 * 
 */
public class GamaBundleLoader {

	static Map<String, Class> skillClasses = new HashMap();
	static Map<String, Class> builtInSpeciesClasses = new HashMap();
	static Map<Integer, List<Class>> CLASSES_BY_KIND = new HashMap();
	static Map<Integer, Class> FACTORIES_BY_KIND = new HashMap();
	public static volatile boolean contributionsLoaded = false;
	public final static Map<Bundle, String> gamlAdditionsBundleAndFiles = new HashMap();

	/**
	 * 
	 * FIX ME
	 * start
	 */
	public static void preBuildContributions() throws GamaStartupException {
		Map<String, String> additions = new LinkedHashMap();
		IConfigurationElement[] config =
			Platform.getExtensionRegistry().getConfigurationElementsFor("gaml.grammar.addition");
		for ( IConfigurationElement e : config ) {
			final String pathName = e.getAttribute("name");
			final String pluginName = e.getContributor().getName();
			System.out.println("Compiling additions to GAML from " + pluginName);
			additions.put(pluginName, pathName);
		}
		String core = "msi.gama.core";
		String corePath = additions.get(core);
		if ( corePath == null ) { throw new GamaStartupException(
			"Core implementation of GAML not found. Please check that msi.gama.core is in the application bundles",
			(Throwable) null); }

		// Pre-loading (before any other things) the correspondance between the types and the
		// variable classes, so that further contributions can have working factories
		preBuildTypeVariables(additions);
		// Building the core additions first (others will then be able to overload).
		preBuild(core, corePath);
		additions.remove(core);
		// Building the other contributions
		for ( String pluginName : additions.keySet() ) {
			String pathName = additions.get(pluginName);
			preBuild(pluginName, pathName);

		}
		// postBuildContributions();
		contributionsLoaded = true;
	}

	/**
	 * @throws GamaStartupException
	 * @param additions
	 */
	private static void preBuildTypeVariables(final Map<String, String> additions)
		throws GamaStartupException {
		for ( String pluginName : additions.keySet() ) {
			Bundle bundle = Platform.getBundle(pluginName);
			try {
				bundle.start();
				String pathName = additions.get(pluginName);
				GamaBundleLoader.addGamlExtension(bundle, pathName);
				FileUtils.getGamaProperties(bundle, pathName, GamlProperties.TYPES_NAMES);
			} catch (BundleException e1) {
				throw new GamaStartupException("GAML additions in " + pluginName +
					" cannot be installed due to an error in loading the plug-in.", e1);
			}
		}
	}

	/**
	 * 
	 * FIX ME
	 * start
	 */
	public static void preBuild(final String pluginName, final String pathToAdditions)
		throws GamaStartupException {
		Bundle plugin = Platform.getBundle(pluginName);
		// Generating built-in species

		try {
			GamlProperties mp =
				FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.SPECIES);
			for ( String className : mp.keySet() ) {
				builtInSpeciesClasses.put(mp.getFirst(className), plugin.loadClass(className));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Generating built-in skills

		try {
			GamlProperties mp =
				FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.SKILLS);

			for ( String className : mp.keySet() ) {
				for ( String keyword : mp.get(className) ) {
					try {
						// GuiUtils.debug("Registering skill " + keyword + " implemented by " +
						// className);
						skillClasses.put(keyword, plugin.loadClass(className));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GamaStartupException e1) {
			e1.printStackTrace();
		}

		// Generating kinds & factories

		try {
			GamlProperties mp =
				FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.KINDS);
			for ( String ks : mp.keySet() ) {
				Integer i = Integer.decode(ks);

				if ( !CLASSES_BY_KIND.containsKey(i) ) {
					CLASSES_BY_KIND.put(i, new ArrayList());
				}

				for ( String className : mp.get(ks) ) {
					try {
						CLASSES_BY_KIND.get(i).add(plugin.loadClass(className));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			mp = FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.FACTORIES);

			for ( String ks : mp.keySet() ) {
				Integer i = Integer.decode(ks);
				String factory_name = new ArrayList<String>(mp.get(ks)).get(0);
				try {
					FACTORIES_BY_KIND.put(i, plugin.loadClass(factory_name));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (GamaStartupException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

		GuiUtils.debug("===> Generating support structures for GAML.");
		final long startTime = System.nanoTime();
		GamlProperties mp =
			FileUtils.getGamaProperties(Platform.getBundle("msi.gama.core"), pathToAdditions,
				GamlProperties.FACTORIES);
		try {
			String className = mp.getFirst(String.valueOf(ISymbolKind.MODEL));
			if ( className != null ) {
				DescriptionFactory.disposeModelFactory();
				DescriptionFactory.setFactoryClass((Class<ISymbolFactory>) Platform.getBundle(
					"msi.gama.core").loadClass(className));
			}
		} catch (ClassNotFoundException e1) {

		}
		// Necessary to load this in advanvce for it to be accessible by the parser later
		// (which does not have a clue about available plugins)
		FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.CHILDREN);
		FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.SPECIES_SKILLS);
		// FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.FACETS);
		//
		GamlProperties types =
			FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.TYPES);
		final Set<String> classNames =
			new HashSet(FileUtils.getGamaProperties(plugin, pathToAdditions, GamlProperties.SKILLS)
				.keySet());
		classNames.addAll(types.keySet());
		classNames.addAll(FileUtils.getGamaProperties(plugin, pathToAdditions,
			GamlProperties.UNARIES).keySet());
		classNames.addAll(FileUtils.getGamaProperties(plugin, pathToAdditions,
			GamlProperties.BINARIES).keySet());
		classNames.addAll(FileUtils.getGamaProperties(plugin, pathToAdditions,
			GamlProperties.SYMBOLS).keySet());
		classNames.addAll(FileUtils.getGamaProperties(plugin, pathToAdditions,
			GamlProperties.DEFINITIONS).keySet());
		ClassLoader cl = GamaClassLoader.getInstance().addBundle(plugin);
		// GamlCompiler.class.getClassLoader();

		Types.initWith(types, cl);
		Set<Class> classes = new HashSet();

		for ( String className : classNames ) {
			try {
				Class c = cl.loadClass(className);
				classes.add(c);
				type s = (type) c.getAnnotation(type.class);
				if ( s != null ) {
					classes.addAll(Arrays.asList(s.wraps()));
				}
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
			}
		}
		int numberOfClasses = classes.size();
		scanBuiltIn(classes);
		long endTime = System.nanoTime();
		GuiUtils.debug("===> Scanning of " + numberOfClasses +
			" support classes, found in plugin " + plugin + ", in " + (endTime - startTime) /
			1000000000d + " seconds.");

	}

	public static void scanBuiltIn(final Set<Class> classes) {
		for ( Class c : classes ) {
			// if ( !c.getCanonicalName().startsWith("msi") ) {
			// continue;
			// }
			boolean isISkill = ISkill.class.isAssignableFrom(c);
			boolean isSkill = Skill.class.isAssignableFrom(c);
			// GUI.debug("Processing " + c.getSimpleName());
			skill skillAnnotation = (skill) c.getAnnotation(skill.class);
			if ( skillAnnotation != null ) {
				GamlCompiler.getSkillConstructor(c);
			}
			species speciesAnnotation = (species) c.getAnnotation(species.class);
			if ( speciesAnnotation != null && !isSkill ) {
				GamlCompiler.getAgentConstructor(c);
			}
			for ( final Method m : c.getMethods() ) {
				Annotation[] annotations = m.getAnnotations();
				for ( Annotation vp : annotations ) {
					// GUI.debug(">> Scanning annotation " + vp.toString());
					if ( vp instanceof action ) {
						action prim = (action) vp;
						GamlCompiler.getPrimitive(c, m.getName());
						registerNewFunction(prim.value());
					} else if ( vp instanceof getter ) {
						if ( isISkill ) {
							GamlCompiler.getGetter(c, m.getName(), m.getReturnType());
						} else {
							GamlCompiler.getFieldGetter(c, m.getName(), m.getReturnType());
						}
					} else if ( vp instanceof setter ) {
						Class[] paramClasses = m.getParameterTypes();
						Class paramClass =
							paramClasses.length == 1 ? paramClasses[0] : paramClasses[1];
						GamlCompiler.getSetter(c, m.getName(), paramClass);
					} else if ( vp instanceof operator ) {
						operator op = (operator) vp;
						boolean isStatic = Modifier.isStatic(m.getModifiers());
						Class[] args = m.getParameterTypes();
						for ( String keyword : op.value() ) {
							registerNewOperator(keyword.intern(), m.getName(),
								m.getDeclaringClass(), m.getReturnType(), args,
								GamlCompiler.isUnary(args, isStatic),
								GamlCompiler.isContextual(args, isStatic),
								GamlCompiler.isLazy(args, isStatic), op.iterator(), isStatic,
								op.priority(), op.can_be_const(), op.type(), op.content_type());
						}
					}
				}
			}
		}
		for ( IType type : Types.getSortedTypes() ) {
			if ( type != null ) {
				GamlCompiler.initFieldGetters(type);
			}
		}
		// ModelFactory.computeBuiltInSpecies(new ModelDescription());
	}

	public static void addGamlExtension(final Bundle bundle, final String pathName) {
		gamlAdditionsBundleAndFiles.put(bundle, pathName + "/std.gaml");
	}

}
