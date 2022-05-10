package linda.search.basic;

import linda.*;
import linda.shm.CentralizedLinda;

public class Main {

    public static void main(String args[]) {
        int nbArgs = args.length;
    	if (nbArgs < 2) {
            System.err.println("linda.search.basic.Main search search2 ... file.");
            return;
    	}
        Linda linda = new linda.shm.CentralizedLinda();
        Searcher searcher = new Searcher(linda);
        Manager managers[] = new Manager[nbArgs-1];

        for (int i=0; i<nbArgs-1; i++){
            managers[i] = new Manager(linda, args[nbArgs-1], args[i]);
            
            (new Thread(managers[i])).start();
        }

        (new Thread(searcher)).start();
        
    }
}
