**Question 1 : Représentation binaire**

Pourquoi est-il nécessaire de définir explicitement une convention telle que big-endian lorsqu’une structure est stockée 
dans un tableau de byte ? 
Que se passerait-il si writeInt utilisait big-endian mais readInt supposait little-endian sur une autre machine ?

Dans la mémoire, on n'a que des octets bruts les uns après les autres. Pour un entier sur 4 octets, 
il faut choisir si on met l'octet le plus lourd au début (big-endian) ou à la fin (little-endian). 
Sans règle claire, l'ordinateur ne sait pas dans quel sens lire.

Si on mélange les deux : Les octets sont lus à l'envers. 
Les tailles de fichiers et les numéros de blocs deviennent complètement faux.

**Question 2 : Signes et octets**

Pourquoi une opération telle que memory[offset] & 0xFF est-elle importante lors de la reconstruction d’une valeur entière ? 
Expliquez le rôle de l’opérateur binaire en Java.

En Java, le type byte est toujours signé (de -128 à +127).
Si un octet commence par un bit 1, Java croit que c'est un nombre négatif et rajoute plein de 1 devant quand il le transforme en entier pour faire des calculs.

Le masque & 0xFF sert à effacer ces 1 parasites en ne gardant que les 8 bits de l'octet d'origine (valeur de 0 à 255).

**Question 3 : Sérialisation**

Pourquoi tester uniquement readInt(writeInt(x)) == x peut-il être insuffisant ? 
Et si oui comment sampler correctement les valeurs de x ? 
Expliquez en quoi l’inspection directe des octets permet de détecter davantage de classes d’erreurs.

Si il y a une erreur et que les deux fonctions écrivent et lisent toutes les deux à l'envers, le test va marcher. 
Pourtant, les données dans le tableau ne respectent pas le format du big-endian celui qui est demandé ici.

Pour sampler correctement les valeurs de x on peut faire :
	Des valeurs simples : 0 et -1.
	Les limites : le plus grand entier (Integer.MAX_VALUE) et le plus petit (Integer.MIN_VALUE).
	Des nombres avec des octets faciles à repérer (comme 0x12345678) pour voir s'ils sont dans le bon ordre.
	
Aussi, regarder case par case dans memory[] permet d'être sûr que chaque octet est posé au bon endroit, 
sans dépendre du code de lecture que l'on a écrit lors des différentes étapes.

**Question 4 : Bitmap**

Pourquoi un bitmap est-il plus compact qu’une représentation utilisant un entier par bloc ? 
Exprimez la taille du bitmap en fonction du nombre de blocs.

Un entier int prend 32 bits en mémoire dans la JVM. Le bitmap n'utilise qu'un seul bit par bloc (0 pour libre, 1 pour occupé). 
C'est 32 fois plus compact donc intéressant d'un point de vue mémoire.

Par exemple pour N blocs, la taille est de : 
N / 8  octets. Pour 2048 blocs on a : 2048 / 8 = 256 octets.

**Question 5 : Layout**

Pourquoi les structures du système de fichiers doivent-elles occuper des zones mémoire déterministes ? 
Que se passerait-il si la position du bitmap changeait sans que les méthodes qui l’utilisent soient modifiées ?

Au démarrage, le programme doit savoir exactement où chercher chaque structure.
L'adresse 0 correspond au superbloc
l'adresse 512 correspond au bitmap.

Si le bitmap bougeait, les fonctions iraient lire ou modifier de la mémoire au mauvais endroit. 
Elles risqueraient d'écrire des bits d'allocation par-dessus le superbloc ou les inodes, ce qui peut casser le système de fichier.

**Question 6 : Inode**

Pourquoi séparer les métadonnées du fichier de son contenu ? 
Expliquez pourquoi un inode peut être considéré comme une structure permettant de retrouver le contenu d’un fichier sans contenir directement ce contenu.

Dans un système de fichiers, les métadonnées ont une structure rigide et connue à l'avance, alors que le contenu peut-être modifié utltérieurement
Séparer les deux permet alors de garder d'un côté les données importantes d'un fichier avec une taille fixe et de l'autre avoir les données brutes 
qui peuvent être modifié par l'utilisateur.

A partir des métadonnées on peut retrouver l'emplacement mémoire du contenu du fichier grâce aux pointeurs directs et indirects.
Ainsi on peut retouver le contenu du fichier.

**Question 7 : Allocation**

Pourquoi allocateBlock() doit-il commencer à rechercher à partir du bloc 129 ? 
Que se passerait-il si la recherche commençait au bloc 0 ?

Les blocs 0 à 128 sont réservés pour le fonctionnement interne du système superbloc, bitmap et table des inodes. 
La zone pour les fichiers commence au bloc 129. Donc on commence notre recherche ici.

Si on commençait à 0, la fonction donnerait des blooc internes du systeme pour enregistrer un fichier. En écrivant dedans, on écraserait le superbloc ou le bitmap, détruisant le système de fichiers.

**Question 8 : Fragmentation**

Deux systèmes peuvent-ils posséder exactement le même nombre de blocs libres mais présenter des niveaux de fragmentation différents ? 
Justifiez votre réponse avec une représentation sous forme de séquence de blocs libres et occupés.

Oui, deux systèmes peuvent avoir le même nombre de blocs libres mais être rangés différemment.
Exemple avec 4 blocs libres sur 8 blocs au total :

Système 1 non fragmenté :
[Occupé] [Occupé] [Occupé] [Occupé] [Libre] [Libre] [Libre] [Libre]
Les 4 blocs libres se suivent. Un fichier de 4 blocs peut être écrit d'un seul coup.

Système 2 très fragmenté :
[Libre] [Occupé] [Libre] [Occupé] [Libre] [Occupé] [Libre] [Occupé]
Les 4 blocs libres sont éparpillés. Pour enregistrer un fichier de 4 blocs, il faudra obligatoirement le couper en 4 morceaux séparés.