Projet CardMatcher permettant la reconnaissance et la comparaison entre cartes dans différentes situations.
Il fût réalisé par :

Jossua MIGNON
Gaëlle QUILLAUD
Enzo LIEVOUX
Lilou TISSERAND
El Mehdi BAKKADI
Kelly ALTAR DIT ALTER


Il contient :
- un dossier sources contenant les classes utilisées
- un dossier images contenant la base d'apprentissage
- un dossier temoin contenant la base test

Ce projet fonctionne avec les bibliothèques JavaFX v19 et OpenCV 4.5.5. Pour plus de détails voir le pom.xml.

Liste des principaux bugs :
- Si une carte est supprimée, la reconnaissance live ne fonctionne plus (problème dû au fait que la base d'image prise par la fonction n'est pas la courante mais celle du fichier sauvegarde).
- La suppression des lignes des CSV associées à une cartes ont été désactivé car problème d'indice dans certains cas.
- Plus la base d'apprentissage est grande, plus la reconnaissance live est lente (faire attention, peut être dangereux pour l'ordinateur de l'utilisateur).

Axes d'amélioration immédiats :
- Fin de l'intégration de la classe Descriptors au StaticController.
- Correction des problèmes liés à la suppression de ligne dans les CSV.
- Mise en place des CSV dans la méthode Descriptors.liveMatch() pour une lecture/un calcul des descripteurs et des points d'intérêts en amonts. Cela apportera plus de fluidité et cela permettra d'utiliser les listes courantes d'images.
- Réarrangement en packages propres et intelligents.
- Refonte de la nomenclature pour plus de clarté pour l’utilisateur.
- Calcul et affichage de statistiques.
