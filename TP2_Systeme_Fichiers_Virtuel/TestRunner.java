import java.io.FileReader;
import java.io.IOException;

public class TestRunner {
	
	public static void main(String[] args) {
		TestRunner.testStep2();
		TestRunner.testStep3();
		TestRunner.testStep4();
		TestRunner.testStep5();
		TestRunner.testStep6();
		TestRunner.testStep7();
		TestRunner.testStep8();
		TestRunner.testStep9();
		TestRunner.testStep9Supplementaire();
		
		if (args.length > 0) {
            testExternalFile(args[0]);
		} else {
			System.out.println(
					"[INFO] Aucun fichier externe fourni.");
		}

		System.out.println(
				"=== TOUS LES TESTS SONT TERMINÉS ===");
	}
	
	public static void testStep2() {
		System.out.println("=== TEST ÉTAPE 2 : Utils Entiers ===");

		byte[] buffer = new byte[32];

		int value = 0xF0A1B2E3;
		int written = Utils.writeInt(buffer, 3, value);

		assert written == 4 : "writeInt doit retourner 4";

		
		assert (buffer[3]  & 0xFF) == 0xF0 : "Octet 0 incorrect";
		assert (buffer[4]  & 0xFF) == 0xA1 : "Octet 1 incorrect";
		assert (buffer[5]  & 0xFF) == 0xB2 : "Octet 2 incorrect";
		assert (buffer[6]  & 0xFF) == 0xE3 : "Octet 3 incorrect";

		assert Utils.readInt(buffer, 3) == value :
				"Erreur writeInt / readInt";

		short shortValue = (short) 0xF0A1;
		int shortWritten = Utils.writeShort(buffer, 20, shortValue);

		assert shortWritten == 2 : "writeShort doit retourner 2";

		assert (buffer[20] & 0xFF) == 0xF0 :
				"Premier octet du short incorrect";

		assert (buffer[21] & 0xFF) == 0xA1 :
				"Deuxième octet du short incorrect";

		assert Utils.readShort(buffer, 20) == shortValue :
				"Erreur writeShort / readShort";

		System.out.println("[OK] Étape 2 validée !");
	}
	
	public static void testStep3() {
        System.out.println("=== TEST ÉTAPE 3 : Utils Long & String ===");

        byte[] buffer = new byte[64];

        long value = 0x1122334455667788L;

        int written = Utils.writeLong(buffer, 0, value);

        assert written == 8 : "writeLong doit retourner 8";

        assert (buffer[0] & 0xFF) == 0x11;
        assert (buffer[1] & 0xFF) == 0x22;
        assert (buffer[2] & 0xFF) == 0x33;
        assert (buffer[3] & 0xFF) == 0x44;
        assert (buffer[4] & 0xFF) == 0x55;
        assert (buffer[5] & 0xFF) == 0x66;
        assert (buffer[6] & 0xFF) == 0x77;
        assert (buffer[7] & 0xFF) == 0x88;

        assert Utils.readLong(buffer, 0) == value :
            "Erreur writeLong / readLong";

        for (int i = 16; i < 32; i++) {
            buffer[i] = (byte) 0x7F;
        }
        
        int stringWritten =
            Utils.writeString(buffer, 16, "MYFS", 16);

        assert stringWritten == 16 :
            "writeString doit retourner maxLength";

        assert (buffer[16] & 0xFF) == 'M';
        assert (buffer[17] & 0xFF) == 'Y';
        assert (buffer[18] & 0xFF) == 'F';
        assert (buffer[19] & 0xFF) == 'S';

        for (int i = 20; i < 32; i++) {
            assert buffer[i] == 0 :
                "La zone inutilisée doit être nettoyée";
        }

        assert Utils.readString(buffer, 16, 16).equals("MYFS") :
            "Erreur writeString / readString"; 

        System.out.println("[OK] Étape 3 validée !");
    }

    public static void testStep4() {
        System.out.println("=== TEST ÉTAPE 4 : Initialisation Mémoire ===");

        MemoryManager mm = new MemoryManager();

        byte[] mem = mm.getFilesystemMemory();

        assert mem != null :
            "La mémoire ne doit pas être nulle";

        assert mem.length == MemoryManager.TOTAL_MEMORY :
            "Taille mémoire incorrecte";

        assert Utils.readString(
            mem,
            MemoryManager.SUPERBLOCK_OFFSET,
            16).equals("MYFS1.0") :
            "Signature du superbloc incorrecte";

        assert Utils.readInt(
            mem,
            MemoryManager.SUPERBLOCK_OFFSET + 16)
            == MemoryManager.BLOCK_SIZE :
            "Taille de bloc incorrecte";

        assert Utils.readInt(
            mem,
            MemoryManager.SUPERBLOCK_OFFSET + 20)
            == MemoryManager.TOTAL_MEMORY :
            "Taille mémoire incorrecte";

        assert Utils.readInt(
            mem,
            MemoryManager.SUPERBLOCK_OFFSET + 24)
            == MemoryManager.NUM_BLOCKS :
            "Nombre de blocs incorrect";

        assert Utils.readInt(
            mem,
            MemoryManager.SUPERBLOCK_OFFSET + 28)
            == MemoryManager.MAX_INODES :
            "Nombre maximal d'inodes incorrect";

        System.out.println("[OK] Étape 4 validée !");
    }
	
	public static void testStep5() {
		System.out.println("=== TEST ÉTAPE 5 : Bitmap et Allocation ===");

		MemoryManager mm = new MemoryManager();

		assert mm.setBlockUsed(130, true) :
				"setBlockUsed doit réussir";

		assert mm.isBlockUsed(130) == 1 :
				"Le bloc 130 doit être occupé";

		assert mm.setBlockUsed(130, false) :
				"La libération doit réussir";

		assert mm.isBlockUsed(130) == 0 :
				"Le bloc 130 doit être libre";

		mm.setBlockUsed(129, true);

		int bitmapOffset =
				MemoryManager.BITMAP_OFFSET + (129 / 8);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x02 :
				"Le bit du bloc 129 est incorrect";

		mm.setBlockUsed(130, true);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x06 :
				"Les bits 129 et 130 sont incorrects";

		mm.setBlockUsed(130, false);

		assert (mm.getFilesystemMemory()[bitmapOffset]
				& 0xFF) == 0x02 :
				"La libération du bloc 130 est incorrecte";

		MemoryManager mm2 = new MemoryManager();

		int first = mm2.allocateBlock();
		int second = mm2.allocateBlock();

		assert first == 129 :
				"Le premier bloc de données doit être 129";

		assert second == 130 :
				"Le second bloc de données doit être 130";

		assert mm2.isBlockUsed(129) == 1;
		assert mm2.isBlockUsed(130) == 1;

		assert mm2.isBlockUsed(-1) == -1 :
				"Un bloc négatif doit être refusé";

		assert mm2.isBlockUsed(
				MemoryManager.NUM_BLOCKS) == -1 :
				"Un bloc hors limites doit être refusé";

		System.out.println("[OK] Étape 5 validée !");
    }
	
	public static void testStep6() {
		System.out.println("=== TEST ÉTAPE 6 : Adressage Inode ===");

		MemoryManager mm = new MemoryManager();

		Inode inode = new Inode(mm, 4);

		int expectedOffset =
				MemoryManager.INODE_TABLE_OFFSET
				+ (4 * Inode.INODE_SIZE);

		assert inode.getInodeOffset() == expectedOffset :
				"Offset d'inode incorrect";

		System.out.println("[OK] Étape 6 validée !");
	}
	
	public static void testStep7() {
		System.out.println("=== TEST ÉTAPE 7 : Sérialisation Inode ===");

		MemoryManager mm = new MemoryManager();

		Inode inode = new Inode(mm, 2);

		int[] ptrs = new int[] {
			150, 151, 0, 0, 0,
			0, 0, 0, 0, 0
		};

		long creation = 0x0102030405060708L;
		long modification = 0x1112131415161718L;

		inode.writeToMemory(
				1,
				1024,
				creation,
				modification,
				ptrs,
				777,
				(short) 0644,
				3);

		byte[] memory =
				mm.getFilesystemMemory();

		int offset = inode.getInodeOffset();

		assert (memory[offset] & 0xFF) == 0x00;
		assert (memory[offset + 3] & 0xFF) == 0x02;

		assert Utils.readInt(memory, offset + 4) == 1;
		assert Utils.readInt(memory, offset + 8) == 1024;

		assert (memory[offset + 12] & 0xFF) == 0x01;
		assert (memory[offset + 13] & 0xFF) == 0x02;
		assert (memory[offset + 14] & 0xFF) == 0x03;
		assert (memory[offset + 15] & 0xFF) == 0x04;
		assert (memory[offset + 16] & 0xFF) == 0x05;
		assert (memory[offset + 17] & 0xFF) == 0x06;
		assert (memory[offset + 18] & 0xFF) == 0x07;
		assert (memory[offset + 19] & 0xFF) == 0x08;

		assert Utils.readLong(
				memory,
				offset + 12) == creation;

		assert Utils.readLong(
				memory,
				offset + 20) == modification;

		assert Utils.readInt(
				memory,
				offset + 28) == 150;

		assert Utils.readInt(
				memory,
				offset + 32) == 151;

		assert Utils.readInt(
				memory,
				offset + 68) == 777;

		assert Utils.readShort(
				memory,
				offset + 72) == (short) 0644;

		assert Utils.readInt(
				memory,
				offset + 74) == 3;

		assert inode.getFileType() == 1;
		assert inode.getFileSize() == 1024;

		int[] result =
				inode.getDirectPointers();

		assert result[0] == 150;
		assert result[1] == 151;

		System.out.println("[OK] Étape 7 validée !");
	}

	public static void testStep8() {
		System.out.println("=== TEST ÉTAPE 8 : Création Fichier ===");

		VirtualFileSystem vfs =
				new VirtualFileSystem();

		boolean ok1 =
				vfs.createFile("/", "fichier1.txt");

		boolean ok2 =
				vfs.createFile("/", "fichier2.txt");

		assert ok1 :
				"La création du premier fichier a échoué";

		assert ok2 :
				"La création du second fichier a échoué";

		MemoryManager mm =
				vfs.getMemoryManager();

		Inode inode0 =
				new Inode(mm, 0);

		Inode inode1 =
				new Inode(mm, 1);

		assert inode0.getFileType() == 1 :
				"L'inode 0 doit représenter un fichier";

		assert inode1.getFileType() == 1 :
				"L'inode 1 doit représenter un fichier";

		assert inode0.getFileSize() == 0 :
				"Le premier fichier doit être vide";

		assert inode1.getFileSize() == 0 :
				"Le second fichier doit être vide";

		System.out.println("[OK] Étape 8 validée !");
	}
	
	public static void testStep9() {
		System.out.println("=== TEST ÉTAPE 9 : Entrées/Sorties Fichier ===");

		VirtualFileSystem vfs =
				new VirtualFileSystem();

		assert vfs.createFile(
				"/",
				"test.txt");

		String text =
				"Contenu de test du système de fichiers";

		byte[] original =
				text.getBytes();

		boolean writeOk =
				vfs.writeFile(0, original);

		assert writeOk :
				"Erreur d'écriture";

		Inode inode =
				new Inode(
						vfs.getMemoryManager(),
						0);

		assert inode.getFileSize()
				== original.length :
				"Taille d'inode incorrecte";

		byte[] readBytes =
				vfs.readFile(0);

		assert readBytes != null :
				"Buffer lu nul";

		assert readBytes.length
				== original.length :
				"Longueur lue incorrecte";

		for (int i = 0; i < original.length; i++) {
			assert readBytes[i] == original[i] :
					"Octet incorrect à l'indice " + i;
		}

		System.out.println("[OK] Étape 9 validée !");
	}

    public static void testStep9Supplementaire() {
		
		/* Fichier exactement égale a un bloc */
		
		System.out.println("=== TEST ÉTAPE 9 Supplémentaire: Tests avant étape 10 ===");

		VirtualFileSystem vfs1 = new VirtualFileSystem();
		
		byte[] data = new byte[MemoryManager.BLOCK_SIZE];
		
		for(int remplissage = 0 ; remplissage < data.length ; remplissage++){
		    data[remplissage] = (byte) remplissage;	
		}

        /* Test de création et d'écriture */
        assert vfs1.createFile("","Test1.txt") : "Création du fichier échoué";
		assert vfs1.writeFile(0, data) : "Ecriture du fichier échoué";
		
		Inode inodeT1 = new Inode(vfs1.getMemoryManager(), 0);
		/* Test de la taille du fichier */
		assert inodeT1.getFileSize() == 512 : "La taille du fichier doit être de 512";
		int[] tableauPointeurs1 = inodeT1.getDirectPointers();
		
		assert tableauPointeurs1[0] != 0 : "Le premier pointeur doit être utilisé";
		
		for(int pointeur = 1 ; pointeur < tableauPointeurs1.length ; pointeur++) {
		    assert tableauPointeurs1[pointeur] == 0 : "Le pointeur ne doit pas être utilisé";	
	    } 	
		
		byte[] memory = vfs1.getMemoryManager().getFilesystemMemory();
		int offsetPhysique = tableauPointeurs1[0] * MemoryManager.BLOCK_SIZE;
		
		for(int verification = 0 ; verification < 512 ; verification++) {
		    assert memory[offsetPhysique + verification] == data[verification]
                   : "Erreur octet " + verification + " incorrect";			
		}
		
		byte[] contenuLu = vfs1.readFile(0);
		
		for(int verification = 0 ; verification < 512 ; verification++) {
		    assert contenuLu[verification] == data[verification]
                   : "Erreur octet " + verification + " incorrect";			
		}
		
		/* Fichier de 513 octet */
		
		VirtualFileSystem vfs2 = new VirtualFileSystem();
		
		byte[] data2 = new byte[MemoryManager.BLOCK_SIZE + 1];
		
		for(int remplissage = 0 ; remplissage < data2.length ; remplissage++){
		    data2[remplissage] = (byte) remplissage;	
		}

        assert vfs2.createFile("","Test2.txt") : "Création du fichier échoué";
		assert vfs2.writeFile(0, data2) : "Ecriture du fichier échoué";
		
		Inode inodeT2 = new Inode(vfs2.getMemoryManager(), 0);
		assert inodeT2.getFileSize() == 513 : "La taille du fichier doit être de 513";
		int[] tableauPointeurs2 = inodeT2.getDirectPointers();
		
		assert tableauPointeurs2[0] != 0 : "Le premier pointeur doit être utilisé";
		assert tableauPointeurs2[1] != 0 : "Le deuxième pointeur doit être utilisé";
		
		for(int pointeur = 2 ; pointeur < tableauPointeurs1.length ; pointeur++) {
		    assert tableauPointeurs2[pointeur] == 0 : "Le pointeur ne doit pas être utilisé";	
	    } 	
		
		byte[] memory2 = vfs2.getMemoryManager().getFilesystemMemory();
		int offsetPhysique2 = tableauPointeurs2[0] * MemoryManager.BLOCK_SIZE;
		
		for(int verification = 0 ; verification < 512 ; verification++) {
		    assert memory2[offsetPhysique2 + verification] == data2[verification]
                   : "Erreur octet " + verification + " incorrect";			
		}
		
		int offsetPhysique2bis = tableauPointeurs2[1] * MemoryManager.BLOCK_SIZE;
		
		for(int verification = 0 ; verification < 1 ; verification++) {
		    assert memory2[offsetPhysique2bis + verification] == data2[512 + verification]
                   : "Erreur octet " + verification + " incorrect";			
		}
		
		byte[] contenuLu2 = vfs2.readFile(0);
		
		for(int verification = 0 ; verification < 513 ; verification++) {
		    assert contenuLu2[verification] == data2[verification]
                   : "Erreur octet " + verification + " incorrect";			
		}
		
		/* Test de dépassement */
		
		VirtualFileSystem vfs3 = new VirtualFileSystem();
		
		byte[] data3 = new byte[11 * MemoryManager.BLOCK_SIZE];
		
		assert !vfs3.writeFile(0,data3) : "L'écriture doit échoué";
		
		System.out.println("[OK] Étape 9 Supplémentaire validée !");
	}
	
	public static void testExternalFile(String filename) {

		System.out.println(
				"=== TEST FICHIER EXTERNE ===");

		StringBuilder builder =
				new StringBuilder();

		try (FileReader reader =
					 new FileReader(filename)) {

			char[] buffer =
					new char[1024];

			int count;

			while ((count =
					reader.read(buffer)) != -1) {

				builder.append(
						buffer,
						0,
						count);
			}

		} catch (IOException e) {
			throw new AssertionError(
					"Impossible de lire le fichier externe",
					e);
		}

		String content =
				builder.toString();

		byte[] original =
				content.getBytes();

		VirtualFileSystem vfs =
				new VirtualFileSystem();

		assert vfs.createFile(
				"/",
				"external.txt") :
				"Impossible de créer le fichier VFS";

		assert vfs.writeFile(
				0,
				original) :
				"Impossible d'écrire le fichier externe";

		byte[] recovered =
				vfs.readFile(0);

		assert recovered != null :
				"Les données récupérées sont nulles";

		assert recovered.length
				== original.length :
				"Taille du fichier différente";

		for (int i = 0;
			 i < original.length;
			 i++) {

			assert recovered[i] == original[i] :
					"Différence à l'octet " + i;
		}

		System.out.println(
				"[OK] Fichier externe correctement transféré !");
	}
}