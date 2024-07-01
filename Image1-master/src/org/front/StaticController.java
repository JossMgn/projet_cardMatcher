package org.front;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;

// Classe controller qui permet de gérer tout le mode de reconaissance statique 
// Cette classe a été faite indépendament de la classe Descriptors pour des raisons techniques lors des sprints associés 
public class StaticController implements Initializable {
	@FXML
	private Label descriptionStatic;
	@FXML
	private Label descriptionStaticTemoin;
	@FXML
	private ImageView matchView;
	@FXML
	private ListView<String> myListCardStatic;
	@FXML
	private ListView<String> myListCardStaticTemoin;
	@FXML
	private ImageView objFindStatic;
	@FXML
	private ImageView objFindStaticTemoin;
	@FXML
	private ImageView objFindStaticScreen;
	@FXML
	private ImageView imageTemoin;
	@FXML
	private  Label resStatic;
	@FXML
	private Button select;

	private String nameTest;
	
	private String nameTemoin;

	// Constructeur
	public StaticController() {
		System.out.println("Static Controller");
	}

	// Permet d'afficher le résultat du match entre les deux images sélectionnées
	public void showImage() throws IOException {
		matchImages(nameTest, nameTemoin);
	}

	// Initialisation des listViews et controle des sélections souris associés
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialisation des listViews 
		myListCardStatic.getItems().addAll(Main.listeCartes.getListNomsCards());
		myListCardStaticTemoin.getItems().addAll(Main.listeCartesTemoin.getListNomsCards());

		// Gestion du clique souris
		myListCardStatic.setOnMouseClicked(event -> {
			InputStream stream;
			try {
				nameTest = myListCardStatic.getSelectionModel().getSelectedItem();
				stream = new FileInputStream(Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().indexOf(nameTest)));
				Image retourCapture = new Image(stream);
				Main.cam.updateImageView(objFindStatic, retourCapture);
				descriptionStatic.setText(Main.listeCartes.getListNomsCards().get(Main.listeCartes.getListNomsCards().indexOf(myListCardStatic.getSelectionModel().getSelectedItem())));	
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		myListCardStaticTemoin.setOnMouseClicked(event1 -> {
			InputStream stream1;
			try {
				nameTemoin = myListCardStaticTemoin.getSelectionModel().getSelectedItem();
				stream1 = new FileInputStream(Main.listeCartesTemoin.getListCompleteCards().get(Main.listeCartesTemoin.getListNomsCards().indexOf(nameTemoin)));
				Image retourCapture = new Image(stream1);
				Main.cam.updateImageView(objFindStaticTemoin, retourCapture);
				descriptionStaticTemoin.setText(Main.listeCartesTemoin.getListNomsCards().get(Main.listeCartesTemoin.getListNomsCards().indexOf(myListCardStaticTemoin.getSelectionModel().getSelectedItem())));
				stream1.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	// Méthode de comparaison des deux images sélectionnée et affichage du résultat dans l'ImageView associée
	public void matchImages(String nameTest, String nameTemoin) throws IOException {
		//Chargement des images
		Mat imgTest = Imgcodecs.imread(Main.listeCartes.getListCompleteCards().get(Main.listeCartes.getListNomsCards().indexOf(nameTest)).getPath());
		Mat imgTemoin = Imgcodecs.imread(Main.listeCartesTemoin.getListCompleteCards().get(Main.listeCartesTemoin.getListNomsCards().indexOf(nameTemoin)).getPath());
		
		// Redimentionnement des images pour un meilleur affichage
		Size size = new Size(imgTest.cols(), imgTest.rows()); 
		Imgproc.resize(imgTemoin, imgTemoin, size);
		
		// Détection des points clés et les descripteurs
		MatOfKeyPoint keypointsTest = new MatOfKeyPoint();
		MatOfKeyPoint keypointsTemoin = new MatOfKeyPoint();

		Mat descriptorTest = new Mat();
		Mat descriptorTemoin = new Mat();
		SIFT sift = SIFT.create(60);
		sift.detectAndCompute(imgTest, new Mat(), keypointsTest, descriptorTest);
		sift.detectAndCompute(imgTemoin, new Mat(), keypointsTemoin, descriptorTemoin);

		// Comparaison des descripteurs
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptorTest, descriptorTemoin, matches);

		double max_dist = Double.MIN_VALUE;
		double min_dist = Double.MAX_VALUE;
		for (DMatch match : matches.toList()) {
			double dist = match.distance;
			if (dist < min_dist)
				min_dist = dist;
			if (dist > max_dist)
				max_dist = dist;
		}

		// Affichage des correspondances
		Mat res = new Mat();
		Features2d.drawMatches(imgTest, keypointsTest, imgTemoin, keypointsTemoin, matches, res);
		
		// Enregistrement du résultat en cas de problème
		Imgcodecs.imwrite("tmp.png", res);
				
		// Charger l'image enregistrée en utilisant la classe Image de JavaFX
		Image res_img = new Image("file:tmp.png", res.width(), res.height(),true,true);

		// Affichage du taux de correspondance
		double match_percent = 100.0 * (1 - (min_dist / max_dist));	
		double match_percent_temp = match_percent * 100;
		match_percent = ((double)((int) match_percent_temp))/100;
		resStatic.setText("Taux de correspondance: "+ String.valueOf(match_percent)+"%");
		objFindStaticScreen.setImage(res_img);
	}}