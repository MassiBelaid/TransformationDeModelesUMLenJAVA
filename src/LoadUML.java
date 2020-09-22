import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate.Factory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.CallConcurrencyKind;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.DirectedRelationship;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.ElementImport;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterSet;
import org.eclipse.uml2.uml.ParameterableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.RedefinableElement;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.StringExpression;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
//import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;

public class LoadUML {

	public static void main(String[] args) {

		//Instruction récupérant le modèle sous forme d'arbre à partir de la classe racine "Model"
		Model umlP = chargerModele("model/model.uml");
		
		
		String nomModele=  umlP.getName();
		
		
		System.out.println("Ton modèle se nomme: \""+nomModele+"\"");
		
		
		Package monPackage = null;
		Class classTest = null;
		Class Etudiant = null;
		Class Personne = null;
		Property prop1 = null;
		Operation opMarcher = null;
		for(PackageableElement packE : umlP.getModel().getPackagedElements()) {
			//System.out.println(packE.getClass());
			if(packE instanceof Class) {
				//System.out.println("La classe : "+packE.getName());
				if(packE.getName().equals("Teste")) {
					classTest = (Class)packE;
					for(Property  attr :  classTest.getAttributes()) {
						System.out.println(attr.getName());
						prop1 = attr;
					}
				}else if(packE.getName().equals("Personne")) {
					Personne = (Class)packE;
				}else if (packE.getName().equals("Etudiant")) {
					Etudiant = (Class)packE;
					
					for(Operation  op :  Etudiant.getOperations()) {
						opMarcher = op;
					}
				}
			}else if(packE instanceof Package) {
				//System.out.println("Le Package : "+packE.getName());
				monPackage = (Package) packE;
			}
			
			
		}
		
		
		
		
		moveClass(classTest, monPackage);
		encapsulation(classTest, prop1);
		
		remonter(Personne, Etudiant, opMarcher);
		//remonter(Personne, Etudiant, UMLFactory.eINSTANCE.createOperation());
		
		
		
		//Modifier le nom du modèle UML
		/*umlP.setName("NewModelName");
		System.out.println("\""+nomModele+"\" Changé ! Le modèle se nomme: \""+umlP.getName()+"\"");*/
		
		
		//Sauvegarder le modèle avec son nouveau nom
		sauverModele("model/nouveauModel.uml", umlP);

	}
	
	public static void moveClass(Class c, Package p) {
		c.setPackage(p);
		System.out.println("Class moved");
	}
	
	public static void encapsulation(Class cls, Property prop) {
		if(prop.getVisibility() == VisibilityKind.PUBLIC_LITERAL) {
			System.out.println(prop.getVisibility());
			prop.setVisibility(VisibilityKind.PRIVATE_LITERAL);
			System.out.println(prop.getVisibility());
			Operation getter = cls.createOwnedOperation("get"+prop.getName(), null, null, prop.getType());
			
			List<String> passer = new ArrayList<>();
			
			Operation setter= UMLFactory.eINSTANCE.createOperation();
			setter.setName("set"+prop.getName());
			setter.createOwnedParameter(prop.getName(), prop.getType());
			cls.getOwnedOperations().add(setter);
			//cls.createOwnedOperation("set"+prop.getName(),(EList<String>) prop.getName(), (EList<Type>) prop.getType(), null);
			System.out.println("finiii");
			
		}
		
	}
	
	public static void remonter(Class clMere, Class clOrigin, Operation oper) {
		if(clOrigin.parents().contains(clMere)) {
				if(clOrigin.getOwnedOperations().contains(oper)) {
					Boolean existeDeja = true;
					for(Operation opr : clMere.getOperations()) {
						if((opr.getName().equals(oper.getName())) && (!opr.isAbstract())) {
							existeDeja = false;
						}
					}
					if(existeDeja) {
						clMere.getOwnedOperations().add(oper);
					}else {
						System.out.println("La méthode existe deja dans la classe mère");
					}
				}else {
					System.out.println("La méthode n'apartient pas à la classe d'origine");
				}

		}else {
			System.out.println("Ce n'est pas sa classe mère...");
		}
	}
	
	
	
	public static void sauverModele(String uri, EObject root) {
		   Resource resource = null;
		   try {
		      URI uriUri = URI.createURI(uri);
		      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		      resource = (new ResourceSetImpl()).createResource(uriUri);
		      resource.getContents().add(root);
		      resource.save(null);
		   } catch (Exception e) {
		      System.err.println("ERREUR sauvegarde du modèle : "+e);
		      e.printStackTrace();
		   }
		}

		public static Model chargerModele(String uri) {
		   Resource resource = null;
		   EPackage pack=UMLPackage.eINSTANCE;
		   try {
		      URI uriUri = URI.createURI(uri);
		      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
		      resource = (new ResourceSetImpl()).createResource(uriUri);
		      XMLResource.XMLMap xmlMap = new XMLMapImpl();
		      xmlMap.setNoNamespacePackage(pack);
		      java.util.Map options = new java.util.HashMap();
		      options.put(XMLResource.OPTION_XML_MAP, xmlMap);
		      resource.load(options);
		   }
		   catch(Exception e) {
		      System.err.println("ERREUR chargement du modèle : "+e);
		      e.printStackTrace();
		   }
		   return (Model) resource.getContents().get(0);
		}

}
