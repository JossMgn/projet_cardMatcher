package org.matching;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

// Classe de gestion de l'appairaiment d'images, elle se base sur l'algorithme SIFT.
public class Descriptors {
	private static Mat referenceImageGray = new Mat();
	private static SIFT sift = SIFT.create(40);
	private static MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
	private static Mat descriptors1 = new Mat();
	private static Mat trashCanMat = new Mat();
	private static Mat frameBdd = new Mat();
	private static MatOfKeyPoint keypointsBdd = new MatOfKeyPoint();
	private static Mat descriptorBdd = new Mat();
	private static MatOfKeyPoint keypointsCam = new MatOfKeyPoint();
	private static Mat descriptorCam = new Mat();
	private static MatOfDMatch matches = new MatOfDMatch();
	private static DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
	private static List<String> lines = new ArrayList<>();
	private static List<Double> resList = new ArrayList<>();

	// Calcul simple des descripteurs et points d'intéret d'une image, retourne l'image avec les points d'intérets affichés
	public static Mat descriptorDetection(Mat carte) {
		sift.detect(carte, keypoints1);
		Features2d.drawKeypoints(carte, keypoints1, carte);
		clear();
		return carte;
	}
	
	// Calcul des descripteurs d'une image
	public static Mat descriptors(Mat carte) throws IOException {
		sift.detectAndCompute(carte, trashCanMat , keypoints1, descriptors1);
		clear();
		return descriptors1;
	}
	
	// Calcul des points d'intéret d'une image
	public static MatOfKeyPoint keypoints(Mat carte) throws IOException {
		sift.detectAndCompute(carte, trashCanMat, keypoints1, descriptors1);
		clear();
		return keypoints1;
	}
	
	// Calcul du la compraison entre deux ensembles de descripteurs
	public static double comparaison(Mat descriptor, Mat descriptors) {
		matcher.match(descriptor, descriptors, matches);
		double distanceMatch = 0; 
		for (DMatch match : matches.toList()) {
			distanceMatch += match.distance;
		}
		distanceMatch = distanceMatch / matches.size().height;
		clear();
		return distanceMatch;
		
	}

	// Calcul d'une comparaison entre une image et la base de d'images d'apprentissage
	// Retourne les deux images trouvées collée avec leurs points d'intérêt commnuns reliés
	public static Mat liveMatch(Mat frame) throws IOException {
		
		// Calcul des descripteurs et points d'intéret 
		sift.detectAndCompute(frame, new Mat(), keypointsCam, descriptorCam);

		//Récupère les chemins des cartes
        try (BufferedReader reader = new BufferedReader(new FileReader("./images/sauvegarde.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Comparaison aux images
        for (String linne : lines) {
        	frameBdd = Imgcodecs.imread(linne);
        	Imgproc.cvtColor(frameBdd, referenceImageGray, Imgproc.COLOR_BGR2GRAY);
        	
			sift.detectAndCompute(frameBdd, new Mat(), keypointsBdd, descriptorBdd);
			// Comparer les descripteurs
			matcher.match(descriptorCam, descriptorBdd, matches);
			double max_dist = Double.MIN_VALUE;
			double min_dist = Double.MAX_VALUE;
			for (DMatch match : matches.toList()) {
				double dist = match.distance;
				if (dist < min_dist)
					min_dist = dist;
				if (dist > max_dist)
					max_dist = dist;
			}
			// Afficher le taux de correspondance
			double match_percent = 100.0 * (1 - (min_dist / max_dist));
			double match_percent_temp = match_percent * 100;
			match_percent = ((double)((int) match_percent_temp))/100;
			resList.add(match_percent);
			
        }
        
        //Détermination du meilleur choix
        double max_res = Collections.max(resList);
        int maxIndex = resList.indexOf(max_res);
       
        //On recalcule
        frameBdd = Imgcodecs.imread(lines.get(maxIndex));
   		Imgproc.cvtColor(frameBdd, referenceImageGray, Imgproc.COLOR_BGR2GRAY);
   		keypointsBdd.release();
   		descriptorBdd.release();
		sift.detectAndCompute(frameBdd, new Mat(), keypointsBdd, descriptorBdd);
		
		// Comparer les descripteurs
		matcher.match(descriptorCam, descriptorBdd, matches);
		double max_dist = Double.MIN_VALUE;
		double min_dist = Double.MAX_VALUE;
		for (DMatch match : matches.toList()) {
			double dist = match.distance;
			if (dist < min_dist)
				min_dist = dist;
			if (dist > max_dist)
				max_dist = dist;
		}
		
		// Afficher les correspondances
		Mat res = new Mat();
		
		// Sécurité pour savoir si les listes de descripteurs sont bien égales
		Size size1 = keypointsBdd.size();
		Size size2 = keypointsCam.size();
		if (size1.height < size2.height) {
			Imgproc.resize(keypointsBdd,keypointsBdd,size2);
		} else if (size1.height > size2.height) {
			Imgproc.resize(keypointsCam,keypointsCam,size1);
		}
		
		// dessin des matches sur la matrice que l'on retourne.
		Features2d.drawMatches(frameBdd, keypointsBdd, frame, keypointsCam, matches, res);


		// Afficher le taux de correspondance
		double match_percent = 100.0 * (1 - (min_dist / max_dist));
		
		double match_percent_temp = match_percent * 100;
		match_percent = ((double)((int) match_percent_temp))/100;
		
		clear();
		return res;
	}
	
	// Permet de réinitialisaer les variables de la classe et l'algorithme SIFT pour ne pas avoir une surcharge mémoire
	private static void clear() {
		keypointsBdd.release();
		descriptorBdd.release();
		keypointsCam.release();
		descriptorCam.release();
		matches.release();
		lines.clear();
		resList.clear();
		matcher.clear();
		// Foroage de l'appelle du garbage collector car OpenCV le gère mal
		System.gc();
	}
	
	// Getters et Setters associés aux ensembles de descripteurs et points d'intérets
	public static MatOfKeyPoint getKeypoints1() {
		return keypoints1;
	}

	public static void setKeypoints1(MatOfKeyPoint keypoints1) {
		Descriptors.keypoints1 = keypoints1;
	}

	public static Mat getDescriptors1() {
		return descriptors1;
	}

	public static void setDescriptors1(Mat descriptors1) {
		Descriptors.descriptors1 = descriptors1;
	}



}
