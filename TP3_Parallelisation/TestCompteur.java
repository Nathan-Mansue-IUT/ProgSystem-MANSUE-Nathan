public class TestCompteur {
    
    public static void main(String[] args) {
        for(int i = 1 ; i <= 50 ; i++) {
            CompteurDangereux.resetCompteur();
            
            CompteurDangereux.Incrementeur[] threads = new CompteurDangereux.Incrementeur[9];
            for (int j = 0 ; j< threads.length ; j++) {
                threads[j] = new CompteurDangereux.Incrementeur("nom", 1111111);
                threads[j].run();
            }      
            
            for (int k = 0 ; k < threads.length ; k++) {
                try {
                    threads[k].join();
                } catch (InterruptedException e) {
                    System.err.println("Le thread a été interrompu : " + e.getMessage());
                }
            }  
            System.out.println(CompteurDangereux.getCompteur());
        } 
    }
}