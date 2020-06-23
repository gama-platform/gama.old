/*********************************************************************************************
 *
 * 'ElementTypeUtils.java, in plugin msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

public class ElementTypeUtils {
	public static final String ARCHITECTURE = "IArchitecture";
	public static final String SKILL = "ISkill";

	public static boolean isArchitectureElement(TypeElement typeElt, Messager mes) {
		return isImplementingInterface(typeElt, ARCHITECTURE, mes);
	}

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
