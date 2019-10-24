package pack.services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.Optional;

/**
 *  Created by v4e on 23.07.2019
 */

/**
 * Сервис содержащий методы для вызова диалоговых окон
 * @author v4e
 */
// TODO: Объединить все типы диалогов в один с добавлением аргумента key
public class Service {
    
    public static void showInformationDialog(String header, String content) {
        Dialog dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Информация");
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);
        if (!content.isEmpty())
            dialog.setContentText(content);
        
        dialog.showAndWait();
    }
    
    public static void showWarningDialog(String header, String content) {
        Dialog dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle("Внимание");
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);
        if (!content.isEmpty())
            dialog.setContentText(content);
    
        dialog.showAndWait();
    }
    
    public static void showErrorDialog(Throwable ex, String header, String content) {
        ExceptionDialog dialog = new ExceptionDialog(ex);
        dialog.setTitle("Ошибка");
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);
        if (!content.isEmpty())
            dialog.setContentText(content);

        dialog.showAndWait();
    }
    
    public static Optional<ButtonType> showConfirmDialog(String title, String header, String content) {
        Dialog<ButtonType> dialog = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);
        if (!content.isEmpty())
            dialog.setContentText(content);
    
        return dialog.showAndWait();
    }
    
}
