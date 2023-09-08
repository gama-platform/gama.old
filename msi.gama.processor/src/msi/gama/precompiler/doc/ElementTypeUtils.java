/*******************************************************************************************************
 *
 * ElementTypeUtils.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.vars;

/**
 * The Class ElementTypeUtils.
 */
public class ElementTypeUtils {
	
	/** The Constant ARCHITECTURE. */
	public static final String ARCHITECTURE = "IArchitecture";
	
	/** The Constant SKILL. */
	public static final String SKILL = "ISkill";

	/**
	 * Checks if is architecture element.
	 *
	 * @param typeElt the type elt
	 * @param mes the mes
	 * @return true, if is architecture element
	 */
	public static boolean isArchitectureElement(TypeElement typeElt, Messager mes) {
		return isImplementingInterface(typeElt, ARCHITECTURE, mes);
	}

	/**
	 * Checks if is implementing interface.
	 *
	 * @param typeElt the type elt
	 * @param interfaceName the interface name
	 * @param mes the mes
	 * @return true, if is implementing interface
	 */
	// Inspired by http://types.cs.washington.edu/checker-framework/current/api/ElementUtils
	public static boolean isImplementingInterface(TypeElement typeElt, String interfaceName, Messager mes) {
        
		boolean isImplementing = false;
		
		List<TypeElement> superEltList = new ArrayList<TypeElement>();
		Stack<TypeElement> stack = new Stack<TypeElement>();
		stack.push(typeElt);
		
		while(!stack.empty() && !isImplementing) {
			TypeElement elt = stack.pop();
			
            TypeMirror parentElt = elt.getSuperclass();
            if (parentElt.getKind() != TypeKind.NONE) {
                TypeElement parentEltType = (TypeElement) ((DeclaredType)parentElt).asElement();
                if (!superEltList.contains(parentEltType)) {
                    stack.push(parentEltType);
                    superEltList.add(parentEltType);
                }
            }
            for (TypeMirror implementedInterface : elt.getInterfaces()) {
                TypeElement implementedInterfaceElt = (TypeElement) ((DeclaredType)implementedInterface).asElement();

                if(interfaceName.equals(implementedInterfaceElt.getSimpleName().toString())) {
                	isImplementing = true;
                } else if (!superEltList.contains(implementedInterfaceElt)) {
                    stack.push(implementedInterfaceElt);
                    superEltList.add(implementedInterfaceElt);
                }
            }
		}
		
		return isImplementing;
	}

	/**
	 * Gets the first implementing interfaces with vars.
	 *
	 * @param typeElt the type elt
	 * @param mes the mes
	 * @return the first implementing interfaces with vars
	 */
	// Inspired by the previous method and thus by http://types.cs.washington.edu/checker-framework/current/api/ElementUtils	
	public static TypeElement getFirstImplementingInterfacesWithVars(TypeElement typeElt, Messager mes) {
		
		List<TypeElement> superEltList = new ArrayList<TypeElement>();
		Stack<TypeElement> stack = new Stack<TypeElement>();
		stack.push(typeElt);
		TypeElement firstEltWithVars = null;
		
		while(!stack.empty() && firstEltWithVars == null) {
			TypeElement elt = stack.pop();
			
			vars eltVars = elt.getAnnotation(vars.class);
            if( (eltVars != null) && 
            		(eltVars.value() != null) && 
            		(eltVars.value().length > 0) ) {
            	firstEltWithVars = elt;
            }
			
            TypeMirror parentElt = elt.getSuperclass();
            if (parentElt.getKind() != TypeKind.NONE) {
                TypeElement parentEltType = (TypeElement) ((DeclaredType)parentElt).asElement();
                if (!superEltList.contains(parentEltType)) {
                    stack.push(parentEltType);
                    superEltList.add(parentEltType);
                }
            }
            for (TypeMirror implementedInterface : elt.getInterfaces()) {
                TypeElement implementedInterfaceElt = (TypeElement) ((DeclaredType)implementedInterface).asElement();
                if (!superEltList.contains(implementedInterfaceElt)) {
                    stack.push(implementedInterfaceElt);
                    superEltList.add(implementedInterfaceElt);
                }
            }
		}
		
		return firstEltWithVars;
	}	
}
