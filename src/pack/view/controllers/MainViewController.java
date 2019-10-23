package pack.view.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.db.entity.Skills;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
 *  Created by v4e on 13.07.2019
 */
public class MainViewController implements Initializable {
    
    @FXML
    private MenuItem mDataAnalysis,
            mAbout,
            mDataCollection,
            mOption,
            mCompetence;
    @FXML
    private BorderPane bPane;
    
    private static CollectViewController collectViewController;
    private static CategoryViewController categoryViewController;
    private static OptionViewController optionViewController;
    private static AnalysisViewController analysisViewController;
    
    private static Stage optionStage;
    
    private XSSFSheet xSheet;
    private HSSFSheet hSheet;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mCompetence.addEventHandler(ActionEvent.ACTION, event -> {
            try {
                if (categoryViewController == null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pack/view/CategoryView.fxml"));
                    BorderPane sPane = loader.load();
                    categoryViewController = loader.getController();
                    bPane.setCenter(sPane);
                }
                else {
                    bPane.setCenter(categoryViewController.getbPane());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        });

        mOption.addEventHandler(ActionEvent.ACTION, event -> {
            if (optionStage == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pack/view/OptionView.fxml"));
                    AnchorPane aPane = loader.load();
                    Scene scene = new Scene(aPane);
                    optionViewController = loader.getController();
                    optionStage = new Stage();
                    optionStage.setScene(scene);
                    optionStage.initStyle(StageStyle.DECORATED);
                    optionStage.setTitle("Параметры");
                    optionStage.initModality(Modality.APPLICATION_MODAL);
                    optionStage.showAndWait();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                optionViewController.refreshData();
                optionStage.showAndWait();
            }
            
        });

        mDataCollection.addEventHandler(ActionEvent.ACTION, event -> {
            try {
                if (collectViewController == null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pack/view/CollectView.fxml"));
                    AnchorPane aPane = loader.load();
                    collectViewController = loader.getController();
                    bPane.setCenter(aPane);
                    mOption.disableProperty().bind(CollectViewController.getBlockElementsProperty());
                }
                else {
                    bPane.setCenter(collectViewController.getPane());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
        });
        
        mDataAnalysis.setOnAction(event -> {
            if (analysisViewController == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pack/view/AnalysisView.fxml"));
                    AnchorPane aPane = loader.load();
                    analysisViewController = loader.getController();
                    bPane.setCenter(aPane);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                bPane.setCenter(analysisViewController.getPane());
            }
        });
        
        //TODO: tmp
        mAbout.setOnAction(event -> {
//            ExcelService.connectFile();
//            Iterator<Row> rowIterator;
//            if (Config.getExcelType().equals("xlsx")) {
//                xSheet = ExcelService.getXWorkbook().getSheetAt(0);
//                rowIterator = xSheet.iterator();
//            }
//            else {
//                hSheet = ExcelService.getHWorkbook().getSheetAt(0);
//                rowIterator = hSheet.iterator();
//            }
//
//            while(rowIterator.hasNext())
//            {
//                Row row = rowIterator.next();
//                Iterator<Cell> cellIterator = row.cellIterator();
//                while(cellIterator.hasNext())
//                {
//                    Cell cell = cellIterator.next();
//                    CellType cellType = cell.getCellType();
//                    FormulaEvaluator evaluator;
//                    switch (cellType)
//                    {
//                        case _NONE:
//                        case BLANK:
//                            System.out.println();
//                            System.out.println("\t");
//                            break;
//                        case BOOLEAN:
//                            System.out.println(cell.getBooleanCellValue());
//                            System.out.println("\t");
//                            break;
//                        case FORMULA:
//                            System.out.println(cell.getCellFormula());
//                            System.out.println("\t");
//                            if (Config.getExcelType().equals("xlsx")) {
//                                evaluator = ExcelService.getXWorkbook().getCreationHelper().createFormulaEvaluator();
//                            }
//                            else {
//                                evaluator = ExcelService.getHWorkbook().getCreationHelper().createFormulaEvaluator();
//                            }
//                            System.out.println(evaluator.evaluate(cell).getNumberValue());
//                            break;
//                        case NUMERIC:
//                            System.out.println(cell.getNumericCellValue());
//                            System.out.println("\t");
//                            break;
//                        case STRING:
//                            System.out.println(cell.getStringCellValue());
//                            System.out.println("\t");
//                            break;
//                        case ERROR:
//                            System.out.println("!");
//                            System.out.println("\t");
//                            break;
//
//                    }
//                }
//                System.out.println();
//            }
        });
    }
    
    synchronized static Stage getOptionStage() {
        return optionStage;
    }
    
    public static synchronized CollectViewController getCollectViewController() {
        return collectViewController;
    }
    
    public static OptionViewController getOptionViewController() {
        return optionViewController;
    }
}



//    TableColumn<Category, String> colName = new TableColumn<>();
//    TableColumn<Category, Skills> colSkill = new TableColumn<>();
//            colSkill.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(DBBean.getInstance().getSkillsJPAController().findSkills(param.getValue().getIdCategory()));
//        colSkill.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Skills>() {
//@Override
//public String toString(Skills object) {
//        return object == null ? "" : object.getName();
//        }
//
//@Override
//public Skills fromString(String string) {
//        for (Skills s : DBBean.getInstance().getSkillsJPAController().findSkillsEntities()) {
//        if (s.getName().equals(string)) {
//        return s;
//        }
//        }
//        }
//        }));
//        colSkill.setOnEditCommit(event1 -> {
//
//        });
//        TableView<Category> tableView = new TableView<>();