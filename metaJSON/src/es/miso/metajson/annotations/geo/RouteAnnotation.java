package es.miso.metajson.annotations.geo;

import java.io.PrintWriter;

import org.eclipse.emf.ecore.*;

import es.miso.metajson.annotations.IAnnotationProcessor;

public class RouteAnnotation implements IAnnotationProcessor{

	@Override
	public boolean write(EObject context, EAnnotation a, PrintWriter file) {
		if (context instanceof EReference) {
			file.write("{ \"route\": \"true\"");
			for (String key : a.getDetails().keySet()) {
				file.write(", ");
				file.write("\""+key+"\": \""+ a.getDetails().get(key)+"\"");
			}
			file.write("}");
		}
		return false;
	}

	@Override
	public String getName() {
		return "route";
	}

	@Override
	public Class<?> getContext() {
		return EReference.class;
	}
}
