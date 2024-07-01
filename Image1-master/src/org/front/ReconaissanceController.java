package org.front;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.matching.Descriptors;
import org.opencv.core.Mat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Classe Controller lié à la page de Reconaissance Live
public class ReconaissanceController implements Initializable{
	@FXML
    public ImageView objFindReco;
	@FXML
    private Button apprentissageButton;
    @FXML
    private ImageView reconnaissanceScreen;
    @FXML
    private ListView<String> myListCardReco;
    @FXML
    private Label description;
    @FXML
    private Button staticModeButton;
    @FXML
    private Button deleteButtonReco;
    @FXML
    private Button launchButton;

    private static Stage stage = new Stage();
    
    private File pickUpFile;
    
    private InputStream stream;
    
    private static Mat match = new Mat();;
   	
    private Future<?> recoLive;
    
   	private Executor executor = Executors.newSingleThreadExecutor();
   	
   	//Contructeur
   	public ReconaissanceController() {
   		System.out.println("Reconnaissance Controller");
   		CameraFX.setRecoMod(true);
		CameraFX.setHasBeenUpdated(false);
		//System.runFinalization();
   	}
    
   	// Gestion du bouton pour passer au mode Appprentissage
	@FXML
	private void switchToApprentissage() throws IOException, InterruptedException {
		
		// Changement de la scène affichée
		Main.setRoot("apprentissage");
		
		// Fermeture du buffer de lecture de fichier
		if(stream != null) {
			stream.close();
		}
		
		// Fermeture du Thread de gestion de la reconnaissance live et update du flag associé
    	if (recoLive != null) {
    		CameraFX.setHasBeenUpdated(false);
            recoLive.cancel(true);
            recoLive = null;
    	}
	}

	// Gestion du bouton d'affichage du mode de reconaissance statique
    @FXML
    void switchToStaticMode() throws IOException, InterruptedException {
    	// Mise en pause de la caméra
    	CameraFX.setPause(true);
    	
    	// Affichage de la page de reconaissance statique
    	stage = new Stage();
    	Parent root;
    	root = FXMLLoader.load(getClass().getResource("popupStatique.fxml"));
    	stage.setScene(new Scene(root));
    	stage.initModality(Modality.APPLICATION_MODAL);
    	stage.initOwner(staticModeButton.getScene().getWindow());
    	
    	// Attente de la fermeture et reprise de la lecture de la caméra après
    	stage.showAndWait();
    	CameraFX.setPause(false);
    }
	
    // Gestion du bouton de suppression lié à la base d'apprentissage
    @FXML
    void deletePickUpFile() throws IOException {
    	if (pickUpFile.exists()) {
    		// Fermeture de l'InputStream au cas ou
    		stream.close();
    		
    		// Supression de l'élément selectionné dans les différentes liste, fichiers CSV et supression du fichier en tant que tel 
    		myListCardReco.getItems().remove(pickUpFile.getName().replaceAll(".jpg", ""));
    		//CsvFile.deleteCsvFile(pickUpFile.getName().replaceAll(".jpg", "")); //Supression des CSV non appliquée ici car tout les cas de figure ne sont pas pris en compte
    		Main.listeCartes.remove(pickUpFile);
    		
    		// Update du l'affichage de la carte sélectionné avec la dernière de la liste
    		stream = new FileInputStream(Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().size()-1));
			Image retourCapture = new Image(stream);
			Main.cam.updateImageView(objFindReco, retourCapture);
			stream.close();
    	} else {
    		System.out.println("Pas de fichier selectionné");
    	}
    }
    
    // Gestion du bouton de controle de la reconaissance live et du Thread associé
    @FXML
    void changeHasBeenUpdated() throws InterruptedException {
    	 if(launchButton.getText().equals("Start")) {
    		 
    		 // Update des flags associés
    		 CameraFX.setHasBeenUpdated(true);
    		 launchButton.setText("Stop");
    		 
    		 // Création et corps du thread associé à la reconaissance live
    		 recoLive = ((ExecutorService) executor).submit(() -> {
    		        while (!Thread.interrupted()) {
    		        	while (CameraFX.getHasBeenUpdated()==true) {
    		    			try {
    		    				// Création de l'image de comparaison
								match = Descriptors.liveMatch(CameraFX.getFrame());
							} catch (IOException e) {
								e.printStackTrace();
							}
    		    			// Mise à jour de l'imageView principale
    		                Image matchToShow = Utils.mat2Image(match);
    		                Main.cam.updateImageView(reconnaissanceScreen, matchToShow);
    		                
    		                //Réinitialisation de la matrice match pour des questions de mémoire
    		                match.release();
    		       		}
    		        }
    		    });
    	 } else {
    	    // Arret de la reconaissance live et mise à jour des variables associés
    	    launchButton.setText("Start");
    	    CameraFX.setHasBeenUpdated(false);
    	    // Arrêt du Thread de reconnaissance live
    	    recoLive.cancel(true);
    	    recoLive = null;
    	 }
    }
     
    
	
   	// Initialisation de la liste des différentes cartes déjà présentes dans le répertoire
   	@Override
   	public void initialize(URL arg0, ResourceBundle arg1) {
   		
   		// Changement de scene de la caméra pour pouvoir l'afficher
   		Main.cam.screen = reconnaissanceScreen;
   		
   		// Initialisation de la listeView liée au carte de la base d'apprentissage
		myListCardReco.getItems().addAll(Main.listeCartes.getListNomsCards());

	    // Affichage de la carte sélectionnée dans l'encadré en haut à droite
		myListCardReco.setOnMouseClicked(event -> {
			try {
				pickUpFile = Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().indexOf(myListCardReco.getSelectionModel().getSelectedItem()));
				stream = new FileInputStream(pickUpFile);
				Image retourCapture = new Image(stream);
				Main.cam.updateImageView(objFindReco, retourCapture);
				description.setText(pickUpFile.getName().replaceAll(".jpg", ""));
				stream.close();
				} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

		});
   	}
   	
   	

}
