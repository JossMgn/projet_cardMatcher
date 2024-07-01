package org.front;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import org.opencv.core.Core;

public class Main extends Application {
	private static Scene scene;
	public static CameraFX cam = null;
	public static CardsList listeCartes = new CardsList();
	public static String pathImages = "images/";
	public static CardsList listeCartesTemoin = new CardsList();
	public static String pathImagesTemoin = "temoin/";

	
	// Initialisation de OpenCV, et des variables globales
	@Override
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Initialisation de la caméra
		Main.cam = new CameraFX();
		Main.cam.startCamera();
		
		// Initialisation des listes utilisées
		listeCartes.readSave(pathImages + "sauvegarde.txt");
		listeCartesTemoin.readSave(pathImagesTemoin + "sauvegardeT.txt");
		
		System.out.println("Load success 1");
	}
	
	// Lancement de la partie JavaFX
	@Override
	public void start(Stage RecoStage) throws IOException {
		
		scene = new Scene(loadFXML("reconaissance"));
		RecoStage.setTitle("Card Matcher");
		RecoStage.setScene(scene);
		RecoStage.show();
		System.out.println("Load success 2");
	}
	
	//Permet de lié un fichier fxml à la scene affichée
	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	// Permet de relier les fichiers FXML aux controleurs associés
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Fonction permetant d'enregistrer les historiques et de fermer les différents Threads en cours en cas de fermeture de la scène principale
	@Override
	public void stop() throws Exception{
        // Arret de la caméra et du thread associé
		Main.cam.setClosed();
        super.stop();
	    
        // Sauvegarde des .txt
	   listeCartes.saveFiles(pathImages);
	   listeCartesTemoin.saveFiles(pathImagesTemoin);
	    System.out.println("Stopped and saved");
	}

}
