package es.miso.metajson.annotations;

import java.util.*;

import es.miso.metajson.annotations.apis.ReadExternalAnnotation;
import es.miso.metajson.annotations.geo.GeopositionedAnnotation;

public final class AnnotationRegistry {
	public static final AnnotationRegistry registry = new AnnotationRegistry();
	public HashMap<String, List<IAnnotationProcessor>> reg = new HashMap<>();
	
	private AnnotationRegistry(){
		this.register(new GeopositionedAnnotation());
		this.register(new ReadExternalAnnotation());
	}
	
	public void register(IAnnotationProcessor p) {
		String name = p.getName();
		if (this.reg.get(name)==null) this.reg.put(name, new ArrayList<IAnnotationProcessor>());
		this.reg.get(name).add(p);
	}
	
	public List<IAnnotationProcessor> get(String name, Class<?> ctx) {
		return (this.reg.get(name)==null) ? Collections.emptyList() : this.reg.get(name); 		
	}
}
