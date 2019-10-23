package pack.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/*
 *  Created by v4e on 23.07.2019
 */
public class AnalysisViewController implements Initializable {
    
    @FXML
    private AnchorPane aPane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    }
    
    public AnchorPane getPane() {
        return aPane;
    }
}
