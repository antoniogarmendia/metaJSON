package es.miso.metajson.annotations;

import java.io.PrintWriter;

import org.eclipse.emf.ecore.*;

public interface IAnnotationProcessor {
	boolean write(EObject context, EAnnotation a, PrintWriter file);	
	String  getName();
	Class<?> getContext();
	default boolean isSpecialSection() { return false; }
}
