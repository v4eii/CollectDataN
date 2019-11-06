package pack.view.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pack.util.Config;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Окно параметров, для редактирования полей класса Config
 * @author v4e
 */
public class OptionViewController implements Initializable {
    
    @FXML
    private Spinner<Integer> spinThreadCount;
    @FXML
    private CheckMenuItem chHH,
            chGorRab;
    @FXML
    private Button btnAccept,
            btnCancel,
            btnSelectLogPath,
            btnSelectExcelPath;
    @FXML
    private TextField fieldLogPath,
            fieldExcelPath;
    @FXML
    private Label labelExcelTypeInfo,
            labelPathExcelInfo,
            labelPathLogInfo,
            labelThreadCountInfo,
            labelSiteParsInfo;
    @FXML
    private ComboBox<String> cbExcelType;

    private DirectoryChooser dirChooser;
    private FileChooser fileChooser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinThreadCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5, Config.getThreadCount()));
        fieldLogPath.setText(Config.getLogGeneratePath());
        fieldExcelPath.setText(Config.getExcelPath().equals("null") ? "Не указан файл" : Config.getExcelPath());
        cbExcelType.getItems().addAll("xls", "xlsx");
        cbExcelType.getSelectionModel().select(Config.getExcelType());
        
        labelThreadCountInfo.setTooltip(new Tooltip("Количество потоков для обработки заявлений.\n" +
                "Каждая категория на сайте будет обработана указанным числом потоков.\n" +
                "При медленной скорости интернета увеличение числа потоков будет иметь меньший эффект"));
        labelPathLogInfo.setTooltip(new Tooltip("Расположение создания файла лога сбора данных"));
        labelSiteParsInfo.setTooltip(new Tooltip("Выбор обрабатываемых сайтов. \n" +
                "Для более достоверного результата рекомендуется обрабатывать все доступные сайты"));
        labelExcelTypeInfo.setTooltip(new Tooltip("Расширение файла excel. При выборе файла устанавливается автоматически.\n" +
                "Измените, если вы уверены что расширение установлено не верно. От расширения файла зависит алгоритм его обработки.\n"));
        labelPathExcelInfo.setTooltip(new Tooltip("Расположение обрабатываемого файла excel"));
        
        btnAccept.addEventHandler(ActionEvent.ACTION, event -> {
            Config.setHHSelect(chHH.isSelected());
            Config.setGorRabSelect(chGorRab.isSelected());
            Config.setThreadCount(spinThreadCount.getValue());
            Config.setLogGenerateSource(fieldLogPath.getText());
            Config.setExcelPath(fieldExcelPath.getText());
            Config.setExcelType(cbExcelType.getValue());
            Config.saveConfig();
            MainViewController.getOptionStage().close();
        });
        btnCancel.setOnAction(event -> MainViewController.getOptionStage().close());
        btnSelectLogPath.setOnAction(event -> {
            if (dirChooser == null)
                dirChooser = new DirectoryChooser();
    
            String oldSource = fieldLogPath.getText();
            File dir = dirChooser.showDialog(new Stage());
            if (dir != null)
                fieldLogPath.setText(dir.getAbsolutePath());
            else
                fieldLogPath.setText(oldSource);
            
        });
        btnSelectExcelPath.setOnAction(event -> {
            if (fileChooser == null)
                fileChooser = new FileChooser();
            
            String oldSource = fieldExcelPath.getText();
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null)
                fieldExcelPath.setText(file.getAbsolutePath());
            else
                fieldExcelPath.setText(oldSource);
    
            String substring = file != null ? file.getAbsolutePath().substring(file.getAbsolutePath().length() - 5) : null;
            if (substring.contains("xlsx"))
                cbExcelType.getSelectionModel().select("xlsx");
            else
                cbExcelType.getSelectionModel().select("xls");
        });
        //TODO: добавить вопрос о закрытии при не сохранении данных
        chGorRab.setSelected(Config.getGorRabSelect());
        chHH.setSelected(Config.getHHSelect());
    }

    /**
     * Обновление формы на случай изменения параметров
     */
    void refreshData() {
        fieldExcelPath.setText(Config.getExcelPath());
        fieldLogPath.setText(Config.getLogGeneratePath());
        cbExcelType.getSelectionModel().select(Config.getExcelType());
        spinThreadCount.getValueFactory().setValue(Config.getThreadCount());
    }
    
}
