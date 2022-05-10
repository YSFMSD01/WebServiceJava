package linda.shm;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	LinkedHashMap<Tuple,ArrayList<Callback>> eventsRead = new LinkedHashMap<Tuple,ArrayList<Callback>>();
	LinkedHashMap<Tuple,ArrayList<Callback>> eventsTake = new LinkedHashMap<Tuple,ArrayList<Callback>>();
	
    public CentralizedLinda() {
    }
    /*création d'un éspace de Tuple sous forme d'une liste */
    ArrayList<Tuple> espace_Tuple = new ArrayList<Tuple>();

    /*pour la concurrence*/
    final Lock moniteur = new  ReentrantLock();
	final Condition condition = moniteur.newCondition();

    

    


  //  @override
    public void write(Tuple t) {
        /*quand on utilise un tuple on le bloque(question de concurrence)*/
        moniteur.lock();
		espace_Tuple.add(t);
		if (eventsRead.containsKey(t)) {
			if (eventsRead.get(t).isEmpty()) {
				eventsRead.remove(t);
			}
				//on fera une boucle pour traiter tous les callback dans l'arraylist<callback>
			//le "if" ca sert à rien de voir le callback s'il n'existe pas;
			if(eventsRead.get(t)!=null) {
				for(int i=0;i<=eventsRead.get(t).size();i++) {
					eventsRead.get(t).get(i).call(t);
					eventsRead.get(t).remove(i);
				}
			eventsRead.remove(t);
			}	
		}else{
			if(eventsRead.get(t)!=null) {
			for(int i=0;i<=eventsTake.get(t).size();i++) {
				eventsTake.get(t).get(i).call(t);
				eventsTake.get(t).remove(i);
			}
			espace_Tuple.remove(t);
				if (eventsTake.get(t).isEmpty()) {
					eventsRead.remove(t);
				}	
		}
		}
		condition.signalAll();
		moniteur.unlock();
    }

  //  @override
    /** Version non bloquante de Read*/
    public Tuple tryRead(Tuple model) {
        moniteur.lock();
        for(Tuple t : espace_Tuple){
            if(t.matches(model)){
                /*on enlÃ¨ve le moniteur pour qu'on puisse retourner t*/
                moniteur.unlock();
                return t;
            }
        }  
        moniteur.unlock();
        return null;
}


   // @override
    /**Version non bloquante de Take */
    public Tuple tryTake(Tuple model){
        moniteur.lock();
        for (Tuple t: espace_Tuple){
            if (t.matches(model)){
            	espace_Tuple.remove(t);
                /*on enlÃ¨ve le moniteur pour qu'on puisse retourner t*/
                moniteur.unlock();
                return t;
            }
        }
        moniteur.unlock();
        return null;
    }
    // @override
    // dans cette méthode on va utiliser la méthode tryTake dans le cas ou
    // le tuple qu'on veut est déjà là
    public Tuple take(Tuple model){
    
    	do {
    		Tuple t= this.tryTake(model);
    		if(t!=null) {
    			return t;
    		}
    		else {
    			try {
    				moniteur.lock();
    				condition.await();
    			}catch(InterruptedException e) {
    				e.printStackTrace();
    				}
        		}
    	}while(true);
    }
  
    
    public Tuple read(Tuple model) {
    	moniteur.lock();
    	while(true) {
    		Tuple t= this.tryRead(model);
    		if(t!=null) {
    			moniteur.unlock();
    			return t;
    		}
    		else {
    			try {
    				condition.await();
    			}catch(InterruptedException e) {
    				e.printStackTrace();
    			}
        	}
    	}
    }

   public ArrayList<Tuple> takeAll(Tuple model){
        ArrayList<Tuple> list = new ArrayList<Tuple>() ;
        moniteur.lock();
        for (Tuple t: espace_Tuple){
            if (t.matches(model)){
                t.add(t);
            }
        }
        moniteur.unlock();
        espace_Tuple.removeAll(list);
        return list;
   }

  // @override
   
   public ArrayList<Tuple> readAll(Tuple model){
    ArrayList<Tuple> list = new ArrayList<Tuple>() ;
    moniteur.lock();
    for (Tuple t: espace_Tuple){
        if (t.matches(model)){
            list.add(t);
        }
    }
    moniteur.unlock();
    return list;
   }
 
   public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
	   if (timing == eventTiming.IMMEDIATE) {
			switch(mode)
			{

			case TAKE :	
				Tuple t_Take = new Tuple();
				t_Take = this.tryTake(template);
				if (t_Take == null) {
					if (eventsTake.containsKey(template)) {
						eventsTake.get(template).add(callback);
					} else {
						eventsTake.put(template, new ArrayList<Callback>());
						eventsTake.get(template).add(callback);
					}
				} else {
					callback.call(template);
				}
				break;
			case READ : 
				Tuple t_Read = new Tuple();
				t_Read = this.tryRead(template);
				if (t_Read == null) {
					if (eventsRead.containsKey(template)) {
						eventsRead.get(template).add(callback);
					} else {
						eventsRead.put(template, new ArrayList<Callback>());
						eventsRead.get(template).add(callback);
					}
				} else {
					callback.call(template);
				}
				break;	
			}
		} else {
			switch(mode)
			{
			
			case TAKE :
				if (eventsTake.containsKey(template)) {
					eventsTake.get(template).add(callback);
				} else {
					eventsTake.put(template, new ArrayList<Callback>());
					eventsTake.get(template).add(callback);
				}
			case READ :
				if (eventsRead.containsKey(template)) {
					eventsRead.get(template).add(callback);
				} else {
					eventsRead.put(template, new ArrayList<Callback>());
					eventsRead.get(template).add(callback);
				}				
			}
		}
	
	   
   }

   
   public void debug(String prefix) {
		moniteur.lock();
		System.out.println("DEBUG " + "id : " + prefix.toString());
		Iterator<Tuple> iter = espace_Tuple.iterator();
		while (iter.hasNext()) {
			Tuple t = iter.next();
			System.out.println(t);
		}
		moniteur.unlock();
   }
 
}
