package es.miso.metajson;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.*;

import es.miso.metajson.annotations.AnnotationRegistry;
import es.miso.metajson.annotations.IAnnotationProcessor;

public class JSONSerializer {
	private EPackage metamodel;
	private PrintWriter jsonFile;
	
	public JSONSerializer(EPackage mm, String fileName) {
		this.metamodel = mm;
		try {
			this.jsonFile = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void serialize() {
		this.visit(this.metamodel);
		this.jsonFile.flush();
		this.jsonFile.close();
	}
	
	private void serializeAnnotations(EModelElement p, String indent) {
		this.jsonFile.write(indent+"\"annotations\" : [");
		for (EAnnotation a: p.getEAnnotations()) {
			boolean written = false;
			for (IAnnotationProcessor proc : AnnotationRegistry.registry.get(a.getSource(), p.getClass())) {
				if (written) this.jsonFile.write(",\n");
				written = proc.write(p, a, this.jsonFile);
			}
		}
		this.jsonFile.write(indent+"], \n");
	}
	
	private void visit(EPackage p) {
		this.jsonFile.write("{\"name\" : \""+p.getName()+"\",\n");
		this.serializeAnnotations(p, "  ");
		this.jsonFile.write(" \"classes\" : [");
		boolean first = true;
		for (EClassifier c : p.getEClassifiers()) {
			if (c instanceof EClass) {
				if (!first ) this.jsonFile.write(",");
				this.visit((EClass)c);			
				first = false;
			} 
		}
		this.jsonFile.write("\n], \n");
		this.jsonFile.write(" \"enums\" : [");
		first = true;
		for (EClassifier c : p.getEClassifiers()) {	
			if (c instanceof EEnum) {
				if (!first ) this.jsonFile.write(",");
				this.visit((EEnum)c);			
				first = false;
			} 
		}
		this.jsonFile.write("\n]\n}\n");
	}
	
	private boolean isRoot (EClassifier c) {
		if (!(c instanceof EClass)) return false;
		if (((EClass)c).isAbstract()) return false;
		EPackage p = c.getEPackage();
		for (EClassifier cl : p.getEClassifiers()) {
			if (!(cl instanceof EClass)) continue;
			if (((EClass) cl).isAbstract()) continue;				
			if (!isContained((EClass)cl, (EClass)c, new ArrayList<EClass>())) return false;
		}
		return true;
	}
	
	private List<EClass> getAllSubTypes ( EClass c ) {
		List<EClass> subTypes = new ArrayList<EClass>();
		EPackage container = c.getEPackage();
		for (EClassifier cl : container.getEClassifiers()) {
			if ( (cl instanceof EClass) && !cl.equals(c)) {
				EClass clase = (EClass) cl;
				if (clase.getEAllSuperTypes().contains(c)) subTypes.add(clase);
			}
		}
		return subTypes;
	}
	
	private boolean isContained(EClass containee, EClass container, List<EClass> visited) {
		if (containee.equals(container)) return true;
		if (containee.getEAllSuperTypes().contains(container)) return true;
		visited.add(container);
		for (EReference er : container.getEAllReferences()) {
			EClass target = er.getEReferenceType();
			if (er.isContainment() && !visited.contains(target)) {
				if (this.isContained(containee, target, visited)) return true;
				// now check all children of target
				for (EClass sub : this.getAllSubTypes(target)) {
					if (this.isContained(containee, sub, visited)) return true;
				}
			}
		}
		visited.remove(container);
		return false;
	}
	
	private boolean visit(EEnum e) {
		this.jsonFile.write("\n  {\n");
		this.jsonFile.write("    \"name\" : \""+e.getName()+"\",\n");
		this.jsonFile.write("    \"values\" : [");
		boolean first = true;
		for (EEnumLiteral lit : e.getELiterals()) {
			if (!first) this.jsonFile.write(", ");
			this.jsonFile.write("\""+lit.getName()+"\"");
			first = false;
		}
		this.jsonFile.write("]\n  }");
		return true;
	}
	
	private boolean visit(EClass c) {
		EClass clase = (EClass)c;
		
		this.jsonFile.write("\n  {\n");
		this.jsonFile.write("    \"name\" : \""+clase.getName()+"\",\n");
		this.jsonFile.write("    \"abstract\" : \""+clase.isAbstract()+"\",\n");
		this.jsonFile.write("    \"root\" : \""+this.isRoot(c)+"\",\n");
		this.serializeAnnotations(c, "    ");
		this.jsonFile.write("    \"parents\" : [");
		boolean first = true;
		for (EClass cl : clase.getESuperTypes()) {
			if (!first) this.jsonFile.write(", ");
			this.jsonFile.write("\""+cl.getName()+"\"");
			first = false;
		}
		this.jsonFile.write("],\n");
		this.jsonFile.write("    \"attributes\" : [");
		first = true;
		for (EAttribute atr : clase.getEAllAttributes()) {
			if (!first) this.jsonFile.write(",");
			this.visit(atr);
			first = false;
		}
		this.jsonFile.write("\n    ],\n");
		this.jsonFile.write("    \"references\" : [");
		first = true;
		for (EReference ref : clase.getEAllReferences()) {
			if (!first) this.jsonFile.write(",");
			this.visit(ref);
			first = false;
		}
		this.jsonFile.write("\n    ]\n");
		this.jsonFile.write("  }");
		return true;
	}

	private boolean visit(EReference ref) {
		this.jsonFile.write("\n    {\n");
		this.jsonFile.write("      \"name\" : \""+ref.getName()+"\",\n");
		this.jsonFile.write("      \"containment\" : \""+ref.isContainment()+"\",\n");
		this.jsonFile.write("      \"target\" : \""+ref.getEReferenceType().getName()+"\",\n");
		if (ref.getEOpposite()!=null) 
			this.jsonFile.write("      \"opposite\" : \""+ref.getEOpposite().getName()+"\",\n");
		else 
			this.jsonFile.write("      \"opposite\" : \"null\",\n");
		
		this.jsonFile.write("      \"min\" : \""+ref.getLowerBound()+"\",\n");
		this.jsonFile.write("      \"max\" : \""+ref.getUpperBound()+"\", \n");
		this.jsonFile.write("      \"owner\" : \""+ref.getEContainingClass().getName()+"\"");
		this.jsonFile.write("    }");
		return true;
	}

	private boolean visit(EAttribute atr) {
		this.jsonFile.write("\n    {\n");
		this.jsonFile.write("      \"name\" : \""+atr.getName()+"\",\n");
		this.jsonFile.write("      \"type\" : \""+atr.getEAttributeType().getName()+"\",\n");
		this.jsonFile.write("      \"min\" : \""+atr.getLowerBound()+"\",\n");
		this.jsonFile.write("      \"max\" : \""+atr.getUpperBound()+"\",\n");
		this.jsonFile.write("      \"default\" : \""+atr.getDefaultValue()+"\",\n");
		this.serializeAnnotations(atr, "      ");
		this.jsonFile.write("      \"owner\" : \""+atr.getEContainingClass().getName()+"\"");
		this.jsonFile.write("    }");
		return true;
	}
	
}
