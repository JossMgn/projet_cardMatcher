package org.front;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;


// Classe controller lié au Popup d'enregistrement d'une nouvelle image
public class PopupController {
	
	@FXML
	private TextField nameTxt;

    @FXML
    private Button cancelButton;

    @FXML
    private MenuButton colorButton;

    @FXML
    private Button validateButton;

    @FXML
    private MenuButton valueButton;
   
    protected static String[] name = {"",""};
    
    protected static boolean flag;
    
    // Gestion JavaFX du choix du nom
    @FXML
    void clubs() {
    	name[0]="C";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void diamonds() {
    	name[0]="D";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }
    
    @FXML
    void hearts() {
    	name[0]="H";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void spades() {
    	name[0]="S";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void ace() {
    	name[1]="1";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void two() {
    	name[1]="2";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void three() {
    	name[1]="3";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void four() {
    	name[1]="4";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void five() {
    	name[1]="5";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void six() {
    	name[1]="6";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }
    
    @FXML
    void seven() {
    	name[1]="7";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void height() {
    	name[1]="8";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void nine() {
    	name[1]="9";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void ten() {
    	name[1]="10";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }
    @FXML
    void jack() {
    	name[1]="11";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }
    
    @FXML
    void queen() {
    	name[1]="12";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }

    @FXML
    void king() {
    	name[1]="13";
    	nameTxt.setPromptText(name[0]+"_"+name[1]);
    }


    @FXML
    void validate() {
    	if(!name[0].isEmpty()&& !name[1].isEmpty()) {
	    	flag = true;
	    	ApprentissageController.closeStage();
    	}
    }
    
    @FXML
    void cancel() {
    	flag = false;
    	ApprentissageController.closeStage();
    }

    // Getters et Setters associés aux variables de la classe
	public static String[] getName() {
		return name;
	}

	public void setName(String[] nom) {
		name = nom;
	}

	public static boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean drapeau) {
		flag = drapeau;
	}


}

    
    

