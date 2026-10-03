**Question 1 : Représentation binaire**

Pourquoi est-il nécessaire de définir explicitement une convention telle que big-endian lorsqu’une structure est stockée dans un tableau de byte ? Que se passerait-il si writeInt utilisait big-endian mais readInt supposait little-endian sur une autre machine ?



Il y auras une absurdité sur les données on arriveras a les lires mais elles seront illogiques si on les lit dans l'autre convention.



**Question 2 : Signes et octets**

Pourquoi une opération telle que memory\[offset] \& 0xFF est-elle importante lors de la reconstruction d’une valeur entière ? Expliquez le rôle de l’opérateur binaire en Java.



Il empêche java de lire le 8 bit comme le bit de signe ce qui pourrait rendre fausse la valeur entière que l'on veut reconstruire.



**Question 3 : Sérialisation**

Pourquoi tester uniquement readInt(writeInt(x)) == x peut-il être insuffisant ? Et si oui comment sampler correctement les valeurs de x ? Expliquez en quoi l’inspection directe des octets permet de détecter davantage de classes d’erreurs.



**Question 4 : Bitmap**

Pourquoi un bitmap est-il plus compact qu’une représentation utilisant un entier par bloc ? Exprimez la taille du bitmap en fonction du nombre de blocs.



Soit N le nombre de bloc on a : tailleBitMap = N / 8 octet



**Question 5 : Layout**

Pourquoi les structures du système de fichiers doivent-elles occuper des zones mémoire déterministes ? Que se passerait-il si la position du bitmap changeait sans que les méthodes qui l’utilisent soient modifiées ?



Il y aura des problème de donnée la méthode pensera accéder aux bonnes informations quelles a besoin alors que ces informations sont stocké autre part.



**Question 6 : Inode**

Pourquoi séparer les métadonnées du fichier de son contenu ? Expliquez pourquoi un inode peut être considéré comme une structure permettant de retrouver le contenu d’un fichier sans contenir directement ce contenu.



L'ino



**Question 7 : Allocation**

Pourquoi allocateBlock() doit-il commencer à rechercher à partir du bloc 129 ? Que se passerait-il si la recherche commençait au bloc 0 ?



**Question 8 : Fragmentation**

Deux systèmes peuvent-ils posséder exactement le même nombre de blocs libres mais présenter des niveaux de fragmentation différents ? Justifiez votre réponse avec une représentation sous forme de séquence de blocs libres et occupés.

