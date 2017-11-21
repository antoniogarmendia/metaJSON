package es.miso.metajson.annotations;

import java.util.*;

import es.miso.metajson.annotations.apis.ReadExternalAnnotation;
import es.miso.metajson.annotations.device.ReadDeviceAnnotation;
import es.miso.metajson.annotations.geo.GeopositionedAnnotation;
import es.miso.metajson.annotations.geo.RouteAnnotation;
import es.miso.metajson.annotations.user.UserAnnotation;

public final class AnnotationRegistry {
	public static final AnnotationRegistry registry = new AnnotationRegistry();
	public HashMap<String, List<IAnnotationProcessor>> reg = new HashMap<>();
	
	private AnnotationRegistry(){
		this.register(new GeopositionedAnnotation());
		this.register(new ReadExternalAnnotation());
		this.register(new ReadDeviceAnnotation());
		this.register(new UserAnnotation());
		this.register(new RouteAnnotation());
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
