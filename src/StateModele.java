

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
//import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;



public class StateModele {
	
	public static void main(String[] args) {
	
				
			
			//Instruction récupérant le modèle sous forme d'arbre à partir de la classe racine "Model"
			Model umlP = chargerModele("model/state.uml");
			
			
			String nomModele=  umlP.getName();
			
			System.out.println("Modèle \""+nomModele+"\" chargé.");
			
			
			Class context = null;
			for(PackageableElement packE : umlP.getModel().getPackagedElements()) {
				//System.out.println(packE.getName());
				if(packE instanceof Class && packE.getName().equals("Context")) {
					context = (Class)packE;
				}
			}
			
			
			System.out.println("Les machine à etats decrivant le comportement de la classe : "+context.getName());
			StateModele sm = new StateModele();
			for(Behavior b : sm.getEtats(context)) {
				System.out.println(b.getName());
			}
			
			sm.verifMachine((StateMachine)sm.getEtats(context).get(0));
			System.out.println("\n La liste des etats de la machine a etats : ");
			for(Vertex v : sm.listEtatsMachine((StateMachine)sm.getEtats(context).get(0))) {
				System.out.println(v.getName());
			}
			
			System.out.println("\n La liste des opérations de la machine a etats : ");
			for(Operation op : sm.getOperations((StateMachine)sm.getEtats(context).get(0))) {
			System.out.println(op.getName());	
			}
			
			
		}
	
	
	//Retourne les machine à été d'une classe
	public List<Behavior> getEtats(Class cls) {
		List<Behavior> listClassRetour = new ArrayList<Behavior>();
		
		
		for(Behavior behaviour : cls.getOwnedBehaviors()) {
			if(behaviour instanceof StateMachine) {
				listClassRetour.add(behaviour);
			}
			
		}
		//System.out.println(cls.getOwnedBehaviors());
		
		return listClassRetour;
	}
		
	
	//Vérifie si une machine à état est bien formée
	public Boolean verifMachine(StateMachine machineState) {
		
		Boolean retour = false;
		if(machineState.getRegions().size() == 1) {
			retour = true;
			System.out.println(machineState.getName()+" est bien formée");
		}
		
		return retour;
	}
	
	//Liste des etats
	
	public List<Vertex> listEtatsMachine(StateMachine machState) {
		List<Vertex> listVertexRetour = new ArrayList<Vertex>();
		if(!verifMachine(machState)) {
			System.out.println("Machine mal formée");
		}else {
			Region region = machState.getRegions().get(0);
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof Pseudostate || vertex instanceof State) {
					listVertexRetour.add(vertex);
				}
			}
		}
		return listVertexRetour;
	}
	
	
	//Liste des opération 
	public List<Operation> getOperations(StateMachine machineState) {
		return machineState.getAllOperations();
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
