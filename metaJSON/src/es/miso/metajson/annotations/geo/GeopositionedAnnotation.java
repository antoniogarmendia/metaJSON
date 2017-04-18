package es.miso.metajson.annotations.geo;

import java.io.PrintWriter;

import org.eclipse.emf.ecore.*;

import es.miso.metajson.annotations.IAnnotationProcessor;

public class GeopositionedAnnotation implements IAnnotationProcessor{

	@Override
	public boolean write(EObject context, EAnnotation a, PrintWriter file) {
		if (context instanceof EPackage) {
			file.write("  {\"isgeo\" : \"true\"}");
		}
		return false;
	}

	@Override
	public String getName() {
		return "geopositioned";
	}

	@Override
	public Class<?> getContext() {
		return EPackage.class;
	}
}
