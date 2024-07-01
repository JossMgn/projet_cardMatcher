package org.matching;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.KeyPoint;


// Classe de gestion des CSV un pour les noms des fichiers, un pour les descripteurs et un pour les keypoints.
// La lecture est totalement fonctionnelle mais la suprpression ne gère pas tout les cas de figure.
// De ce fait cette classe n'est que partiellement implémenté au reste du projet.
public class CsvFile {
		
	// rows est la liste de tableaux de String extraite du fichier csv dont chaque ligne représente une image, avec son nom, ses descripteurs et ses points d'intérêt
	private static List<String[]> rowsNames = new ArrayList<>();
	private static List<String[]> colsDescriptors = new ArrayList<>();
	private static List<String[]> rowsDescriptors = new ArrayList<>();
	private static List<String[]> rowsKeyPoints = new ArrayList<>();

	/**
	 * Add the name, the descriptors and the key points of an image in a csv files
	 * @param name
	 * 				name of the image
	 * @param descriptor
	 * 					descriptor of the image
	 * @param keyPoints
	 * 					key points of the image
	 * @throws Exception
	 */
	public static void addImageToCsvFile(String name, Mat descriptor, MatOfKeyPoint keyPoints) throws Exception {
		addNameToCsvFile(name);
		addDescriptorToCsvFile(descriptor);
		addKeyPointsToCsvFile(keyPoints);
	}
	
	/**
	 * Add the name of an image in a csv file
	 * @param name
	 * 				name of the image
	 * @throws IOException
	 */
	public static void addNameToCsvFile(String name) throws IOException {
		// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/name.csv", true);  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        // Name of the card
        bufferedWriter.write(name + ',');
        
        // New line for next image
        bufferedWriter.newLine();
        
        // Close the file
        bufferedWriter.close();
	}

	/**
	 * Add the descriptors of an image in a csv file
	 * @param descriptor
	 * 					descriptor of the image
	 * @throws IOException
	 */
	public static void addDescriptorToCsvFile(Mat descriptor) throws IOException {
		// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/descriptor.csv", true);  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                
        // Descriptor       
        for (int i = 0; i < descriptor.rows(); i++) {
        	StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j <descriptor.cols(); j++) {
            	stringBuilder.append(descriptor.get(i, j)[0]).append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.write(";");
        }
        
        // New line for next image
        bufferedWriter.newLine();
        
        // Close the file
        bufferedWriter.close();
	}

	/**
	 * Add the key points of an image in a csv file
	 * @param keyPoints
	 * 					key points of the image
	 * @throws IOException
	 */
	public static void addKeyPointsToCsvFile(MatOfKeyPoint keyPoints) throws IOException {
		// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/keyPoints.csv", true);  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        // KeyPoint       
        for (int i = 0; i < keyPoints.rows(); i++) {
        	StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j <keyPoints.cols(); j++) {
            	stringBuilder.append(keyPoints.get(i, j)[0]).append(",");
            }
            bufferedWriter.write(stringBuilder.toString());
        }
        
        // New line for next image
        bufferedWriter.newLine();
        
        // Close the file
        bufferedWriter.close();
	}
	
	/**
	 * Read a csv file and get the name, the descriptors and the key points of an image
	 * @param i
	 * 			the line of the image in the csv file
	 * @throws Exception
	 */
	public static void readCsvFile(int i) throws Exception {
		readNameFromCsvFile(i);
		readDescriptorFromCsvFile(i);
		readKeyPointsFromCsvFile(i);
	}
	
	/**
	 * Read and get the name of an image in a csv file
	 * @param i
	 * 			the line of the image in the csv file
	 * @return
	 * 		    the name of the image
	 * @throws IOException
	 */
	public static String readNameFromCsvFile(int i) throws IOException {
		System.out.println("i = " + i);
		FileReader fileReader = new FileReader("./images/name.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String name;
		String line;
	
		while ((line = bufferedReader.readLine()) != null) {
		    rowsNames.add(line.split(","));
		}
		
		System.out.println(i);
		System.out.println(rowsNames.size());
		if (i == -1) {
			name = null;
		}
		else {
			name = rowsNames.get(i)[0];
		}		

		// Close the BufferedReader
		bufferedReader.close();

		return name;
	}
	
	/**
	 * Read and get the descriptors of an image in a csv file
	 * @param i
	 * 			the line of the image in the csv file
	 * @return
	 * 		    the descriptors of the image
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static Mat readDescriptorFromCsvFile(int i) throws IOException {
		FileReader fileReader = new FileReader("./images/descriptor.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);	
		String line;
		
		colsDescriptors = new ArrayList<>();
		
		if (i == -1) {
			return null;
		}
		
		while ((line = bufferedReader.readLine()) != null) {
		    rowsDescriptors.add(line.split(";"));		   
		}
		
		
		for (int m = 0; m < rowsDescriptors.get(i).length-1; m++) {
	    	colsDescriptors.add(rowsDescriptors.get(i)[m].split(","));
	    }
	    
	    System.out.println(colsDescriptors.get(0)[0]);
	    System.out.println(colsDescriptors.get(1)[0]);

	    // Descriptor creation
	    Mat descriptor = new Mat(rowsDescriptors.get(i).length, 128, CvType.CV_32F);
	    System.out.println(descriptor);
	    
	    for (int m = 0; m < rowsDescriptors.get(i).length-1; m++) {
	    	for (int n = 0; n < colsDescriptors.get(m).length-1; n++) {
	    		descriptor.put(m, n, Double.parseDouble(colsDescriptors.get(m)[n]));
	    	}	    	
	    }
	    System.out.println(descriptor);
	    
	    	    
		// Close the BufferedReader
		bufferedReader.close();

		return descriptor;
	}

	/**
	 * Read and get the key points of an image in a csv file
	 * @param i
	 * 			the line of the image in the csv file
	 * @return
	 * 		    the key points of the image
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static MatOfKeyPoint readKeyPointsFromCsvFile(int i) throws IOException {
		FileReader fileReader = new FileReader("./images/keypoints.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);	
		String line;
		
		if (i == -1) {
			return null;
		}
		
		while ((line = bufferedReader.readLine()) != null) {
		    rowsKeyPoints.add(line.split(","));		   
		}
		
		int j = 0; 
		
		while(j < rowsKeyPoints.get(i).length) {	    		    		
    		if (rowsKeyPoints.get(i)[j] == ",") {
    			j +=1 ;
    		}
    		else{
    			j+=1;
    		}	    			    	
	    }
	    
	    // KeyPoints creation
	    MatOfKeyPoint keyPoints = new MatOfKeyPoint();
	    
	    j = 0;
	    int k = 0;
	    while(j <= rowsKeyPoints.get(i).length) {	
    		if (rowsKeyPoints.get(i)[j] == ",") {
    			j +=1 ;
    		} 		
    		else {
    			double keypoint = Double.parseDouble(rowsKeyPoints.get(i)[j]);
    			keyPoints.put(k, 0, keypoint);
		    	j += 1; 
		    	k += 1;
    		}    	

	    	if (j >= rowsKeyPoints.get(i).length) {
	    		break;
	    	}
	    	
	    }
		// Close the BufferedReader
		bufferedReader.close();

		return keyPoints;
	}
	
	public static void deleteCsvFile(String name) throws IOException {
		int k = getLineOfCsvFile(name);
		deleteNameFromCsvFile(k);
		deleteDescriptorFromCsvFile(k);
		//deleteKeyPointsFromCsvFile(k);
	}
	
	public static void deleteNameFromCsvFile(int k) throws IOException {
				// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/name.csv");  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);   
        
		// Add all the names in the Csv file except the one we have to delete
        for (int i = 0; i < rowsNames.size(); i++) {
        	if (i < k) {
        		// Name of the card
        		bufferedWriter.write(rowsNames.get(i)[0] + ',');
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        	else if (i == rowsNames.size()-1 ){
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        	else {
        		// Name of the card
        		bufferedWriter.write(rowsNames.get(i+1)[0] + ',');
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        }
		
             
        // Close the file
        bufferedWriter.close();
	}
	
	public static void deleteDescriptorFromCsvFile(int k) throws IOException {
		// Open the file for reading
		FileReader fileReader = new FileReader("./images/descriptor.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);	
		String line;
		
		while ((line = bufferedReader.readLine()) != null) {
		    rowsDescriptors.add(line.split(";"));		   
		}
					
		// Close the BufferedReader
		bufferedReader.close();
	    		
		
		// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/descriptor.csv");  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                
        // Descriptor       
        for (int i = 0; i < rowsDescriptors.size(); i++) {
        	colsDescriptors = new ArrayList<>();
        	
        	if (i < k) {
        		for (int m = 0; m < rowsDescriptors.get(i).length-1; m++) {
        			colsDescriptors.add(rowsDescriptors.get(i)[m].split(","));
        		}
        		
        		for (int m = 0; m < rowsDescriptors.get(i).length-1; m++) {
        			for (int n = 0; n < colsDescriptors.get(m).length-1; n++) {
        				bufferedWriter.write(colsDescriptors.get(m)[n] + ',');
        			}
        			
        			bufferedWriter.write(';');
        		}      
        		
        		bufferedWriter.newLine();
        	}
        	
        	else if (i == rowsDescriptors.size()-1 ){
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        	
        	else {
        		for (int m = 0; m < rowsDescriptors.get(i+1).length-1; m++) {
        			colsDescriptors.add(rowsDescriptors.get(i+1)[m].split(","));
        		}
        		
        		for (int m = 0; m < rowsDescriptors.get(i+1).length-1; m++) {
        			for (int n = 0; n < colsDescriptors.get(m).length-1; n++) {
        				bufferedWriter.write(colsDescriptors.get(m)[n] + ',');
        			}
        			
        			bufferedWriter.write(';');
        		}    
        		
        		bufferedWriter.newLine();
        	}
        }
        	       	
        bufferedWriter.close();
}

	public static void deleteKeyPointsFromCsvFile(int k) throws IOException {
		// Open the file for reading
		FileReader fileReader = new FileReader("./images/keyPoints.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);	
		String line;
		
		while ((line = bufferedReader.readLine()) != null) {
			rowsKeyPoints.add(line.split(","));		   
		}
			
		// Close the BufferedReader
		bufferedReader.close();
		
		
		// Open the file for writing
        FileWriter fileWriter = new FileWriter("./images/keyPoints.csv", false);  // Set "true" to append to the file and don't overwrite
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                
        // KeyPoints       
        for (int i = 0; i < rowsKeyPoints.size(); i++) {
        	if (i < k) {
        		for (int j = 0; j < rowsKeyPoints.get(i).length; j++) {
	        		bufferedWriter.write(rowsKeyPoints.get(i)[j] + ',');
        		}   
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        	else if (i == rowsKeyPoints.size()-1 ){
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        	else {
        		for (int j = 0; j < rowsKeyPoints.get(i+1).length; j++) {
	        		bufferedWriter.write(rowsKeyPoints.get(i+1)[j] + ',');	        		
        		}
        		// New line for next image
        		bufferedWriter.newLine();
        	}
        }     
        
        // Close the file
        bufferedWriter.close();
}
	
	/**
	 * Get the length of the different csv files (they all have the same length)
	 * @return
	 * 			the length
	 * @throws IOException
	 */
	public static int getLength() throws IOException {
		FileReader fileReader = new FileReader("./images/name.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int i = 0;
		
		while ((bufferedReader.readLine()) != null) {
		    i += 1;
		}
		
		// Close the BufferedReader
		bufferedReader.close();

		return i;
	}
	
	public static int getLineOfCsvFile(String name) throws IOException {
		FileReader fileReader = new FileReader("./images/name.csv");
		try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
			    rowsNames.add(line.split(","));
			}
					
				
			for (int i = 0; i < rowsNames.size(); i++) {
				if (name.compareTo(rowsNames.get(i)[0]) == 0) {
					return i;
				}
			}

			// Close the BufferedReader
			bufferedReader.close();
		}
		return -1;
	}
	
 	public static Mat stringToMat(String descriptorS) {
		// the descriptor string should look like something like that : "3x3 [[1.0, 2.0, 3.0], [4.0, 5.0, 6.0], [7.0, 8.0, 9.0]]"

		// Extract the dimensions of the matrix
		int rowsDim = Integer.parseInt(descriptorS.substring(0, 1));
		int colsDim = Integer.parseInt(descriptorS.substring(2, 3));

		// Create a new Mat object with the appropriate dimensions
		Mat descriptor = new Mat(rowsDim, colsDim, CvType.CV_32F);

		// Split the string into rows
		String[] row = descriptorS.substring(5, descriptorS.length() - 2).split("], ");

		// Iterate over the elements of the matrix and set the values of the Mat object
		for (int i = 0; i < row.length; i++) {
		    String[] values = row[i].split(", ");
		    for (int j = 0; j < values.length; j++) {
		    	descriptor.put(i, j, Float.parseFloat(values[j]));
		    }
		}
		
		return descriptor;
	}
	
	public static MatOfKeyPoint stringToMatOfKeyPoint(String keyPointsS) {

		// the keyPoints string should look like something like that : "1.0, 2.0, 3.0, 4.0; 2.0, 3.0, 4.0, 5.0"

		// Split the string by ';' and then by ','
		String[] keypointsArray = keyPointsS.split(";");
		KeyPoint[] keypoints = new KeyPoint[keypointsArray.length];
		for (int i = 0; i < keypointsArray.length; i++) {
			String[] point = keypointsArray[i].split(",");
			keypoints[i] = new KeyPoint(Float.parseFloat(point[0]), Float.parseFloat(point[1]),
					Float.parseFloat(point[2]), Float.parseFloat(point[3]), 0, 0, 0);
		}

		// create a new MatOfKeyPoint object
		MatOfKeyPoint matOfKeyPoints = new MatOfKeyPoint();
		matOfKeyPoints.fromArray(keypoints);
		return matOfKeyPoints;

	}
}