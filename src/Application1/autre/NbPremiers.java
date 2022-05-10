package linda.autre;


// Les résultats ne sont pas très probants, mon ordinateur utilise déjà tous ses coeurs pour
// la version séquentielle de base

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NbPremiers {

    public void sequentiel(int n) {
              
        System.out.println("1) Version séquentielle");
        System.out.println("Affichage nombres premiers jusqu'à (100 max): " + n);

        long startTime = System.currentTimeMillis();
        
        Boolean liste[] = new Boolean[n+1];
        Arrays.fill(liste, true);
  
        for(int i=2; i*i<=n; i++) {
            if (liste[i]) {
                for (int j = i*i; j<=n; j+=i) {
                    liste[j] = false;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        
        for (int i=2; i<=100; i++) {
            if (liste[i]) {
                System.out.print(i + " ");
            }
        }
        System.out.println("\nTemps d'éxécution " + (endTime - startTime) + "ms\n");
 
    }

    public static void main(String[] args) throws Exception {
        // Borne supérieure
        int n = 50000000;

        // Version séquentielle
        NbPremiers p = new NbPremiers();
        p.sequentiel(n);

        // Version concurrente avec Threads
        System.out.println("2) Version concurrente avec Threads");
        System.out.println("Affichage nombres premiers jusqu'à (100 max): " + n);
        ExecutorService poule = Executors.newFixedThreadPool(4);

        long startTime = System.currentTimeMillis();
        Future<Boolean[]> tache = poule.submit(new ParalleleThread(n));

        poule.shutdown();

        Boolean resultats[] = tache.get();
        long endTime = System.currentTimeMillis();

        for (int i=2; i<=100; i++) {
            if (resultats[i]) {
                System.out.print(i + " ");
            }
        }
        System.out.println("\nTemps d'éxécution " + (endTime - startTime) + "ms\n");

    }
    
}

class ParalleleThread implements Callable<Boolean[]> {
    private int n;

    ParalleleThread(int n) {
        this.n = n;
    }

    @Override
    public Boolean[] call() {
        Boolean liste[] = new Boolean[n+1];
        Arrays.fill(liste, true);

        for(int i=2; i*i<=n; i++) {
            if (liste[i]) {
                for (int j = i*i; j<=n; j+=i) {
                    liste[j] = false;
                }
            }
        }

        return liste;
    }

}