package es.miso.metajson;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class Exporter {
	
	public Exporter() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"library", new EcoreResourceFactoryImpl());
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl()); 
          
        //this.resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
	}
		
	public void export(String inFile, String outFile) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource ecoreResource = resourceSet.getResource(URI.createFileURI(new File(inFile).getAbsolutePath()), true);
		EPackage ePackage = (EPackage)ecoreResource.getContents().get(0);
		
		System.out.println("Got : "+ePackage);
		
		if (ePackage.getName().equals("PrimitiveTypes")) {
			ePackage = (EPackage)ecoreResource.getContents().get(1);
		}
		
		new JSONSerializer(ePackage, outFile).serialize();
	}
	
	public static void main(String[] args) throws IOException {
		Exporter exp = new Exporter();
		if (args.length<2) {
			System.out.println("Params: <input ecore> <output json>");
			return;
		}
		else exp.export(args[0], args[1]);
		//exp.export("./metamodels/DFAAutomaton.ecore", "./out/DFAAutomaton.json");
	}

}
