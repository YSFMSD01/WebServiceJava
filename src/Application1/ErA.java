package Application1;

import linda.*;
import linda.shm.CentralizedLinda;
import java.util.Collection;
import java.util.Arrays;
public class ErA {
	
	protected Linda linda;
	protected int size;
	
	public ErA(int size) {
		this.size = size;
		this.linda = new CentralizedLinda();
		for (int i = 2; i<= size; i++) {
			linda.write(new Tuple(i));
		}
	}
	
	
	public void removeMultiples(int n) {
		for(int i = 2*n; i <= size; i += n) {
			linda.tryTake(new Tuple(i));
		}
	}
	
	public void removeNonPrimes() {
		for(int curInt = 2; curInt * curInt <= size; curInt++) {
			Tuple primeTuple = linda.tryRead(new Tuple(curInt));
			
			if(primeTuple != null) {
				int prime = (Integer) primeTuple.element();
				removeMultiples(prime);
			}
		}
	}
	public int[] getcrible() {
		
		Collection<Tuple> tuples = linda.readAll(Tuple.valueOf("[ ?Integer ]"));
		
		int[] crible = new int[tuples.size()];
	    int i = 0;
	    for(Tuple tuple : tuples) {
	    	crible[i] = (Integer) tuple.element();
	    	i++;
	    }
	    return crible;
	}
	public static void main(String[] args) {
		
		int k;
		
		if (args.length == 1) { 
            try {
                k = Integer.parseInt (args[0]);
 
            }
            catch (NumberFormatException nfx) {
                throw new IllegalArgumentException("\nUsage : veuiller entrer un entier k"
                                          	+ " <invalid version : Sequential or Parallel >\n");
                                           
                                    					}
            		
         ErA era = new ErA(k);

		era.removeNonPrimes();
		System.out.print(" resultat pour k = "+k + " "+ Arrays.toString(era.getcrible()));
	
	}

	}
}

