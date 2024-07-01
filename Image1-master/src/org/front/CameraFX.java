package org.front;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.matching.Descriptors;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


// Classe permetant de gérer la Caméra, thread de lecture compris
public class CameraFX  {
	private static int cameraId = 0;
	private VideoCapture capture = new VideoCapture();
    private ScheduledExecutorService timer;
	public static ImageView objFindReco;
    private static Mat frame = new Mat();
    
    private static boolean pause = false;
    private static boolean recoMod = false;
    private static boolean hasBeenUpdated = false;
    @FXML
	public ImageView screen;
	
    // Constructeur
	public CameraFX() {
		this.setFrame(new Mat());
	}
	
	// Enssemble getter et setter pour accéder à la matrice lié à la caméra
	@SuppressWarnings("exports")
	public void setFrame( Mat mat) {
		frame = mat;
		
	}
	
	@SuppressWarnings("exports")
	public static Mat getFrame() {
		return frame;
	}

	// Méthode lié à la capture et à l'affichage de la caméra dans les imagesView selon la scène
	@FXML
	public void startCamera(){

		// Début de la capture de la vidéo venant de la caméra
		this.capture.open(cameraId);
		
		// regarder si la caméra est disponible
		if (this.capture.isOpened())
		{
			// Mise en place de la récupération et l'afficahge de la caméra dans différentes situation via
			// un thread et un taux de rafraichissemetn de l'iamge précis
			Runnable frameGrabber = new Runnable() {
				
				// Convertion de la matrice OpenCV en Image JavaFX pour pouvoir l'afficher dans la scene
				Image imageToShow = Utils.mat2Image(frame);
				
				public void run()
				{
					// Prise de la frame et traitement de celle ci (avec affichage compris)
					if(screen != null && !pause) {
						
						// Cas de la page Reconnaissance
						if(recoMod) {
							// Cas de la reconnaissance live (traitement de la frame à part)
							if(hasBeenUpdated) {
								readFrame(frame);
							}else {
								readFrame(frame);
								// Traitement pour afficher les points clés
								frame = Descriptors.descriptorDetection(frame);
								imageToShow =  Utils.mat2Image(frame);
								updateImageView(screen, imageToShow);
							}
						// Cas de la page Apprentissage
						}else {
							readFrame(frame);
							imageToShow =  Utils.mat2Image(frame);
							updateImageView(screen, imageToShow);
						}
					}
				}
				
			};
			
			// Contrôle de l'exécution du thread à un framerate précis pour ne pas surchargé le processeur 
			this.timer = Executors.newSingleThreadScheduledExecutor();
			if (hasBeenUpdated && recoMod) {
				// Si l'on est en reconaissance live on passe à 15 images par secondes pour laisser le temps 
				// aux algorithmes (lourds) de tourner et ne pas accumuler du retard dans l'affichage
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 66, TimeUnit.MILLISECONDS);
			}else {
				// Dans le cas général on est à 30 images psr secondes
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	// Permet de lire la grame
	private void readFrame(Mat frame)
	{
		
		// test pour voir si la caméra est allumée
		if (this.capture.isOpened())
		{
			try
			{
				// lecture de la frame actuelle
				this.capture.read(frame);
				this.setFrame(frame);
			}
			catch (Exception e)
			{
				// log de l'erreur
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
	}
	
	// Permet de gérer la fermeture de la caméra et la libération du thread associé
	private void stopAcquisition()
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				this.timer.shutdown();
				// On laisse le temps de process la dernière frame au cas ou
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// On libère la caméra
			this.capture.release();
		}
	}
	
	// Permet de gérer la partie affichage dans JavaFX et de lié l'image à l'imageView finale
	public void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	// Setter de la fermeture de l'acquisiation de la caméra
	protected void setClosed()
	{
		this.stopAcquisition();
	}
	
	// Getter et Setter liés aux différents flags de gestion de la caméra 
	public boolean isPause() {
		return pause;
	}

	public static void setPause(boolean p) {
		pause = p;
	}
	public static void setRecoMod(boolean p) {
		recoMod = p;
	}
	public static void setHasBeenUpdated(boolean p) {
		hasBeenUpdated = p;
	}

	public static boolean getHasBeenUpdated() {
		return hasBeenUpdated;
	}
}
