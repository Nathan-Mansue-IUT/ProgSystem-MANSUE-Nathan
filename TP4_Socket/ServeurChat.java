import java.net.*;
import java.io.*;
import java.util.*;

public class ServeurChat {
    // Liste thread-safe des flux de sortie
    private static final List<PrintWriter> clients = 
        Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Serveur Chat démarré");

        while (true) {
            Socket client = serverSocket.accept();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            // TODO
            System.out.println("Client connecté. Total : " + clients.size());
            new Thread(new ChatHandler(client, out)).start();
        }
    }

    static class ChatHandler implements Runnable {
        private Socket client;
        private PrintWriter out;

        public ChatHandler(Socket socket, PrintWriter out) {
            this.client = socket;
            this.out = out;
        }

        @Override
        public void run() {
        try {
              // 3. Création des canaux input/output
            BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );
            // 4. Boucle d'écho (lecture/écriture)
            String ligne;
            while ((ligne = in.readLine()) != null) {
                ligne = Thread.currentThread().getName() + " " + ligne;
                System.out.println("Message reçu : " + ligne);
                broadcastExcept(ligne);  // Diffuse à tous sauf l'émetteur
            }
            // 5. Fermeture de la connexion client
            System.out.println("Client déconnecté");
            in.close();
            out.close();
            client.close();
        } catch (Exception e) {}
            finally {
                synchronized (clients) {
                    clients.remove(out);
                }
                try { client.close(); } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients) {
                    writer.println(">>> " + message);
                }
            }
        }

        private void broadcastExcept(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients) {
                    if (writer != out) {  // Exclut l'émetteur
                        writer.println(">>> " + message);
                    }
                }
            }
        }

    }
}