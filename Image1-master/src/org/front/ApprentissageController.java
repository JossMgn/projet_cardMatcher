 package org.front;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.ResourceBundle;

import org.matching.CsvFile;
import org.matching.Descriptors;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

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


/**
 * Mode Apprentissage de l'interface
 * 
 */
public class ApprentissageController implements Initializable{
    @FXML
    private ImageView objFind;
    @FXML
    private Button apprentissageButton;
    @FXML
    private ImageView apprentissageScreen;
    @FXML
    private Button captureButton;
    @FXML
    private ListView<String> myListCard;
    @FXML
    private Button deleteButton;
    @FXML
    private Label descriptionAppretissage;
    
   	private static Stage stage= new Stage();
   		
   	private String lastCapturePath;
   	
   	private File pickUpFile;
   	
    private InputStream stream;
   

    
   	/**
   	 * Montre dans la console qu'on est bien passé en mode apprentissage
   	 */
	public ApprentissageController() {
		System.out.println("Apprentissage Controller");
		CameraFX.setRecoMod(false);
	}
	
	/**
	 * Permet de fermer la fenêtre d'enregistrement d'une nouvelle carte
	 */
	public static void closeStage() {
		stage.close();
	}
	
	/**
	 * Permet de passer au mode Reconnaissance de cartes 
	 * @throws IOException
	 */
	@FXML
	private void switchToReconaissance() throws IOException {
		Main.setRoot("reconaissance");
		if(stream != null) {
			stream.close();
		}
	}
	
	/**
	 * Récupère la valeur d'une carte
	 * @param number représente le chiffre de la carte (de 1 à 13)
	 * @return le {@link String} de la valeur de la carte
	 */
	public static String valueFolderName(int number, char color) {
		String valueFolderName;
		
		// valueFolderName prend la valeur du numéro de la carte
		// Cas particulier des cartes à têtes et de l'as
		if (number == 1) {
			valueFolderName = "A/";
		} 
		else if (number == 11) {
			valueFolderName = "J/";
		} 
		else if (number == 12) {
			valueFolderName = "Q/";
		} 
		else if (number == 13) {
			valueFolderName = "K/";
		} 
		else {
			valueFolderName = String.valueOf(number) + "/";
		}

		return valueFolderName;
	}
	
	/**
	 * Récupère la couleur d'une carte
	 * @param color représente la couleur de la carte (club, diamond, heart, spade)
	 * @return le {@link String} de la couleur de la carte
	 */
	public static String colorFolderName(int number, char color) {
		
		// colorFolderName prend la valeur de la couleur de la carte
		String colorFolderName = String.valueOf(color) + "/";
		
		return colorFolderName;
	}
	
	/**
	 * Enregistre le chemin pour placer la photo de la carte dans le bon fichier : 
	 * dans le fichier de sa valeur qui est lui même dans le fichier de sa couleur.
	 * @param number représente le chiffre de la carte (de 1 à 13)
	 * @param color représente la couleur de la carte (club, diamond, heart, spade)
	 * @return le {@link String} du chemin d'accès
	 */
	public static String folderPath(int number, char color) {
		String folderPath;
		folderPath = Main.pathImages + colorFolderName(number, color) + valueFolderName(number, color);
		return folderPath;
	} 

	
	/**
	 * Ouvre la fenêtre de capture d'une carte et enregistre la capture d'écran
	 * @throws IOException
	 */
    @FXML
    void captureIt() throws IOException {
    	
    	// Mise en pause de la caméra et récupération de la bonne image rognée.
    	CameraFX.setPause(true);
    	Mat cropped = new Mat();
    	Parent root;
    	Size size = CameraFX.getFrame().size();
    	int rows = (int) size.height;
    	int cols = (int) size.width;
    	// Proportions de rognage trouvées expérimentalement
    	// Si mise en place d'une scène adaptative en fonction de la caméra utilisée. Il faudra changer cela 
    	cropped = CameraFX.getFrame().submat(rows/2-145,rows/2+180,cols/2-110,cols/2+110);//submat(84, 425,200, 431);

    	// Affichage du popup de sélection du nom de la carte
    	stage = new Stage();
    	root = FXMLLoader.load(getClass().getResource("popup.fxml"));
    	stage.setScene(new Scene(root));
    	stage.initModality(Modality.APPLICATION_MODAL);
    	stage.initOwner(captureButton.getScene().getWindow());
    	stage.showAndWait();
    	
    	if(PopupController.isFlag()) {
    		
    		// Gestion de la fin de la nomenclature de la carte capturée
    		String[] card = PopupController.getName();
    		String name = card[0]+"_"+card[1]+"_"+new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
    		int number = Integer.parseInt(card[1]);
    		char color = card[0].charAt(0);
    		
        	try {
        		// Calcul des descripteurs
            	Mat descriptor = Descriptors.descriptors(cropped);
        		
            	// Ajout d'une image dans les fichiers csv
				CsvFile.addImageToCsvFile(name, descriptor, Descriptors.keypoints(cropped));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		//Check si le dossier d'enregistrement existe, sinon création de celui ci
    		File dossier = new File(folderPath(number, color));
    		boolean creaDos = dossier.mkdirs();
    		
    	    if(creaDos) {
    	        System.out.println("Le dossier a été créé.");
    	      }
    	      else {
    	        System.out.println("Le dossier existe déja.");
    	      }
    	     
    		// Mise à jour des différentes listes et affichage de la carte capturée
    	    lastCapturePath = folderPath(number, color) + name + ".jpg";
    		Imgcodecs.imwrite(lastCapturePath, cropped);
    		stream = new FileInputStream(lastCapturePath);
    		
    		// Update de la dernière capture effectuée
    		Image retourCapture = new Image(stream);
    		Main.cam.updateImageView(objFind, retourCapture);
    		stream.close();
    		
    		// Mise à jour des différentes listes 
    		Main.listeCartes.updateList(lastCapturePath);
    		myListCard.getItems().add(Main.listeCartes.getListNomsCards().get(Main.listeCartes.getListNomsCards().size()-1));
    		System.out.println(lastCapturePath);
    	}
    	// Dès que le popup est fermé on réactive l'update de la caméra
    	CameraFX.setPause(false);
    }
    
    // Gestion du bouton de suppressin lié à la base d'apprentissage
    @FXML
    void deletePickUpFile() throws IOException {
    	if (pickUpFile.exists()) {
    		// Fermeture de l'InputStream au cas ou
    		stream.close();
    		
    		// Supression de l'élément selectionné dans les différentes liste, fichiers CSV et supression du fichier en tant que tel 
    		myListCard.getItems().remove(pickUpFile.getName().replaceAll(".jpg", ""));
    		//CsvFile.deleteCsvFile(pickUpFile.getName().replaceAll(".jpg", "")); //Supression des CSV non appliquée ici car tout les cas de figure ne sont pas pris en compte
    		Main.listeCartes.remove(pickUpFile);
    		
    		// Update du l'affichage de la carte sélectionné avec la dernière de la liste
    		stream = new FileInputStream(Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().size()-1));
			Image retourCapture = new Image(stream);
			Main.cam.updateImageView(objFind, retourCapture);
			stream.close();
			
    	} else {
    		System.out.println("Pas de fichier selectionné");
    	}
    }
   	
    /**
     *  Fonction d'initialisation des différents éléments du controleur
     *  A savoir la listView, la caméra et gestion de l'affichage de la carte selectionné
     * @param arg0
     * @param arg1
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		// Changement de scene de la caméra pour pouvoir l'afficher
		Main.cam.screen = apprentissageScreen;
		
		// Initialisation de la listeView liée au carte de la base d'apprentissage
		myListCard.getItems().addAll(Main.listeCartes.getListNomsCards());
		
	    // Gestion de l'affichage de la carte sélectionnée dans l'encadré en haut à droite en fonction de al sélection souris
		myListCard.setOnMouseClicked(event -> {
			
			try {
				pickUpFile = Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().indexOf(myListCard.getSelectionModel().getSelectedItem()));
				stream = new FileInputStream(pickUpFile);
				Image retourCapture = new Image(stream);
				Main.cam.updateImageView(objFind, retourCapture);
				descriptionAppretissage.setText(pickUpFile.getName().replaceAll(".jpg", ""));
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});
   	}
	
}