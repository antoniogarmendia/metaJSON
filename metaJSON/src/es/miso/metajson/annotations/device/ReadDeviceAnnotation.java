package es.miso.metajson.annotations.device;

import java.io.PrintWriter;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import es.miso.metajson.annotations.IAnnotationProcessor;

public class ReadDeviceAnnotation implements IAnnotationProcessor{

	@Override
	public boolean write(EObject context, EAnnotation a, PrintWriter file) {
		if (context instanceof EAttribute) {
			file.write("{");
			boolean first = true;
			for (String key : a.getDetails().keySet()) {
				if (!first) file.write(", ");
				file.write("\""+key+"\": \""+ a.getDetails().get(key)+"\"");
				first = false;
			}
			file.write("}");
		}
		return false;
	}

	@Override
	public String getName() {
		return "readDevice";
	}

	@Override
	public Class<?> getContext() {
		return EAttribute.class;
	}

}
