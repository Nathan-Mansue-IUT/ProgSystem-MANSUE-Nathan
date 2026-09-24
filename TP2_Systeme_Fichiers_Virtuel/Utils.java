/*
 * Utils.java                                          2026/09/24
 * IUT de Rodez, pas de copyright ni de copyleft
 */

/**
 * Classe permettant de manipulé les données stockée en mémoire
 * @author Nathan Mansue
 */
public class Utils {

    /**
	 * Ecrit a partir de l'offset en little-endian
	 */
    public static int writeInt(byte[] memory, int offset, int value) {
		memory[offset] = (byte) (value & 0xFF);
		memory[offset + 1] = (byte) (value >> 8 & 0xFF); // décalage de 8 bits
		memory[offset + 2] = (byte) (value >> 16 & 0xFF);
		memory[offset + 3] = (byte) (value >> 24 & 0xFF);
        return 4;
    }

    public static int readInt(byte[] memory, int offset) {
		int resultat;
		resultat = (int) (memory[offset] & 0xFF);  
		resultat = (int) (memory[offset + 1] & 0xFF); 
		resultat = (int) (memory[offset + 2] & 0xFF); 
		resultat = (int) (memory[offset + 3] & 0xFF); 
        return 0;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
		memory[offset] = (byte) (value & 0xFF);
		memory[offset + 1] = (byte) (value >> 8 & 0xFF);
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
		short resultat;
		resultat = (short) (memory[offset] & 0xFF);
		resultat = (short) (memory[offset + 1] & 0xFF);
        return 0;
    }
	
	public static int writeLong(byte[] memory, int offset, long value) {
		memory[offset] = (byte) (value & 0xFF);
		memory[offset + 1] = (byte) (value >> 8 & 0xFF); // décalage de 8 bits
		memory[offset + 2] = (byte) (value >> 16 & 0xFF);
		memory[offset + 3] = (byte) (value >> 24 & 0xFF);
		memory[offset + 4] = (byte) (value >> 32 & 0xFF);
		memory[offset + 5] = (byte) (value >> 40 & 0xFF);
		memory[offset + 6] = (byte) (value >> 48 & 0xFF);
		memory[offset + 7] = (byte) (value >> 56 & 0xFF);
        return 8;
    }

    public static long readLong(byte[] memory, int offset) {
		long resultat;
		resultat = (short) (memory[offset] & 0xFF);
		resultat = (short) (memory[offset + 1] & 0xFF);
		resultat = (short) (memory[offset + 2] & 0xFF);
		resultat = (short) (memory[offset + 3] & 0xFF);
		resultat = (short) (memory[offset + 4] & 0xFF);
		resultat = (short) (memory[offset + 5] & 0xFF);
		resultat = (short) (memory[offset + 6] & 0xFF);
		resultat = (short) (memory[offset + 7] & 0xFF);
        return 0L;
    }

    public static int writeString(byte[] memory, int offset, String str, int maxLength) {

        // 1. Convertir la chaîne en octets.
		byte[] tableau = str.getBytes();
		
        // 2. Copier les octets sans dépasser maxLength.
		for(int i = 0 ; i < tableau.length ; i++) {
		    memory[offset + i] = tableau[i];	
		}
		
        // 3. Nettoyer le reste de la zone avec des zéros.
        if(tableau.length < maxLength) {
		    for(int j = tableau.length ; j < maxLength ; j++) {
                memory[offset + j] = 0;
            }				
		}

        return maxLength;
    }

    public static String readString(byte[] memory, int offset, int maxLength) {
        byte[] resultat = new byte[maxLength];
        // Lire jusqu'au premier octet nul
        // ou jusqu'à maxLength.
		for(int i = 0 ; i < maxLength ; i++) {
			resultat[i] = memory[offset + i];
        }

        String resultatString = new String(resultat);
        return "";
    }
}