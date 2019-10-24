package pack.view.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import pack.threads.Parsing;


import java.net.URL;
import java.util.ResourceBundle;

/**
 *  Created by v4e on 13.07.2019
 */

/**
 * Панель для запуска обработки сайтов
 * @author v4e
 */
public class CollectViewController implements Initializable {

    @FXML
    private TextArea textInfo;
    @FXML
    private CheckBox chLogGenerate;
    @FXML
    private Button btnStartParsing;
    @FXML
    private Label labelLogInfo;
    @FXML
    private AnchorPane aPane;

    private static Boolean logGenerate;
    private static SimpleBooleanProperty blockElements;
    private Service<Void> service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        blockElements = new SimpleBooleanProperty(Boolean.FALSE);
        chLogGenerate.disableProperty().bind(blockElements);
        btnStartParsing.disableProperty().bind(blockElements);
        btnStartParsing.addEventHandler(ActionEvent.ACTION, event -> {
            logGenerate = chLogGenerate.isSelected();
            blockElements.setValue(Boolean.TRUE);
            Parsing.run();
            // TODO: Придумать что нибудь с блокировкой кнопок
            blockElements.setValue(Boolean.FALSE);
        });
        labelLogInfo.setTooltip(new Tooltip("Убедитесь что вы указали верный путь генерации лога в параметрах"));

    }

    public synchronized TextArea getTextInfo() {
        return textInfo;
    }

    public static Boolean getLogGenerate() {
        return logGenerate;
    }

    public static SimpleBooleanProperty getBlockElementsProperty() {
        return blockElements;
    }

    AnchorPane getPane() {
        return aPane;
    }
}


//if (service == null) {
//                service = new Service<Void>() {
//                    @Override
//                    protected Task<Void> createTask() {
//                        return new Task<Void>() {
//                            @Override
//                            protected Void call() {
//                                logGenerate = chLogGenerate.isSelected();
//                                blockElements.setValue(Boolean.TRUE);
//                                Parsing.run();
//                                blockElements.setValue(Boolean.FALSE);
//                                return null;
//                            }
//                        };
//                    }
//                };
//                service.start();
//            }
//            else {
//                service.restart();
//            }