package linda.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	
	 public static void main (String[] args) throws Exception {
	 
	        try {
	        	Registry registry = LocateRegistry.createRegistry(1888);

	        	
	            LindaServer LindaImpl = new LindaServer();
	            registry.bind("Linda", LindaImpl);

	            // service ready : awaiting for calls
	            System.out.println ("Serveur pret");
	        } catch (java.rmi.server.ExportException e) {
	            System.out.println("Deja en marche...");
	        }
	    }
}
