package es.miso.metajson.annotations.user;

import java.io.PrintWriter;

import org.eclipse.emf.ecore.*;

import es.miso.metajson.annotations.*;

public class UserAnnotation implements IAnnotationProcessor{

	@Override
	public boolean write(EObject context, EAnnotation a, PrintWriter file) {
		if (context instanceof EPackage) {
			file.write("  \"user\" = {");
			file.write("\"attributes\" : [\n");
			boolean first = true;
			for (String key : a.getDetails().keySet()) {
				if (!first) file.write(",\n");
				file.write("   { \"name\": \""+key+"\", \n");
				file.write("     \"type\": \""+a.getDetails().get(key)+"\", \n");
				file.write("     \"min\": \"0\", \n");
				file.write("     \"max\": \"1\", \n");
				file.write("     \"owner\": \"user\", \n");
				file.write("     \"annotations\": [], \n");
				file.write("     \"default\": \"\"}");
				first = false;
			}
			file.write("  ] }");
		}
		return true;
	}

	@Override
	public boolean isSpecialSection() { 
		return true; 
	}
	
	@Override
	public String getName() {
		return "userGeo";
	}

	@Override
	public Class<?> getContext() {
		return EPackage.class;
	}
}
