package org.front;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;


// Classe permetant la gestion des listes courantes liées au bases d'images
public class CardsList {
   	private ArrayList<File> listCompleteCards;
   	
   	private ArrayList<String> listNomsCards;
   		
   	// Constructeur
   	public CardsList() {
   		this.setListCompleteCards(new ArrayList<File>());
   		this.setListNomsCards(new ArrayList<String>());
   	}
   	
   	
   	// Getters et Setters liés aux variables de la classe
	public ArrayList<String> getListNomsCards() {
		return listNomsCards;
	}

	public void setListNomsCards(ArrayList<String> list) {
		this.listNomsCards = list;
	}

	public ArrayList<File> getListCompleteCards() {
		return listCompleteCards;
	}

	public void setListCompleteCards(ArrayList<File> list) {
		this.listCompleteCards = list;
	}
	
	// Recupération de la taille de la liste de cartes
	public int getSize() {
		return listCompleteCards.size();
	}
   	
	
	// Permet d'enregister la liste de la base d'image dans un .txt (permet de fluidifier l'initialisation)
   	public void saveFiles(String path) {
		try {
			// Clean en cas de réutilisation de la variable
			this.getListCompleteCards().clear();
			
			// Parcours du répertoir lié à la base d'image
			listf(path, this.getListCompleteCards());
			
			// Création du .txt si inexistant
			File sauvegarde = new File(path + "sauvegarde.txt");
		 	if (sauvegarde.exists()){
		 		System.out.println("Fichier existe déjà. Update en cours");
				sauvegarde.delete();
				sauvegarde.createNewFile();
			}
		 	
		 	// Ecriture dans le txt de la liste des images comprises dans le repertoire
		 	FileWriter fw = new FileWriter(sauvegarde);
		 	for(File file : this.getListCompleteCards()) {	
				fw.write(file.getPath() + System.getProperty("line.separator"));
			}
		 	// Fermeture du buffer
		 	fw.close();
			System.out.println("Update Done !");
			
		} catch(IOException e){
			System.out.println(e);
		}
	}
   	
   	// Lecture du txt lié à la base de donnée et update des listes courantes associées
	public void readSave(String directoryName) {
   	    try
   	    {
   	      // Le fichier d'entrée
   	      File sauvegarde = new File(directoryName);  
   	      
   	      // Créer l'objet File Reader
   	      FileReader fr = new FileReader(sauvegarde); 
   	      
   	      // Créer l'objet BufferedReader        
   	      BufferedReader br = new BufferedReader(fr);     
   	      String line;
   	      
   	      //Boucle de lecture du fichier
   	      while((line = br.readLine()) != null)
	      {
	    	  if(line != null) {
	    		// Ajoute la ligne au buffer
	   	    	File carte = new File(line);
	   	    	this.listCompleteCards.add(carte);
	    	  }   
	      }
   	      
   	      // Fermeture des buffers d'écriture et de lecture
	      fr.close(); 
	      br.close();
	      
	      // Update de la seconde liste courante
	      for(File file : this.listCompleteCards) {
				this.listNomsCards.add(file.getName().replaceAll(".jpg", ""));
	      };
	    }
   	    catch(IOException e)
   	    {
   	      e.printStackTrace();
   	    }
	}
	
	// Permet de parcourir un ensemble de dossier et sous-dossiers à partir d'un path pour récupérer 
	// l'ensemble des fichiers .jpg trouvés dans une liste courante de fichier
   	private void listf(String directoryName, ArrayList<File> listCompleteCards) {
   		
   	    File directory = new File(directoryName);
   	    
   	    // On récupère l'ensemble des fichiers du dossier
   	    File[] fList = directory.listFiles();
   	    
   	    if(fList != null)
   	        for (File file : fList) {      
   	            if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
   	            	this.getListCompleteCards().add(file);
   	            } else if (file.isDirectory()) {
   	                listf(file.getPath(), this.getListCompleteCards());
   	            }
   	        }
   	}
   	
   	
   	// Permet d'ajouter une image dans les listes courantes
   	public void updateList(String path) {
   		File FichierCapture = new File(path);	
   		this.getListCompleteCards().add(FichierCapture);
		this.getListNomsCards().add(FichierCapture.getName().replaceAll(".jpg", ""));
   	}

   	// Permet de supprimer un élément des liste courante mais aussi supprimé le fichier physique
   	public void remove(File removedFile) {
   		
   		// Suppression de l'élément dans les listes courantes
   		this.getListNomsCards().remove(removedFile.getName().replaceAll(".jpg", ""));
   		this.getListCompleteCards().remove(removedFile);
   		
   		// Suppression de l'élément physique de son répertoire
   		removedFile.delete();
   		
   		// Test pour s'assurer que le fichier est bien supprimé
   		if (removedFile.exists()) {
   			System.out.println(removedFile.getName() + " pas supprimé");
   			
   			// Deuxième méthode de suppression du fichier
   			removedFile.deleteOnExit();
   			if (removedFile.exists()) {
   	   			System.out.println(removedFile.getName() + " pas supprimé x2");
   			};
   		};
   	}
}
