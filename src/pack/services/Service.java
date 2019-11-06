package pack.services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * Сервис содержащий методы для вызова диалоговых окон
 * @author v4e
 */
public class Service {

    /**
     * Вызов диалогового окна
     * @param header текст заголовка
     * @param content текст контента
     * @param dialogType тип вызываемого диалога (ERROR/WARNING/INFORMATION)
     */
    public static void showDialog(String header, String content, Alert.AlertType dialogType) {
        Dialog dialog = new Alert(dialogType);
        dialog.setTitle(dialogType.name());
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);
        if (!content.isEmpty())
            dialog.setContentText(content);

        dialog.showAndWait();
    }
    
//    public static void showErrorDialog(Throwable ex, String header, String content) {
//        ExceptionDialog dialog = new ExceptionDialog(ex);
//        dialog.setTitle("Ошибка");
//        dialog.setHeaderText(header);
//        dialog.initStyle(StageStyle.UTILITY);
//        if (!content.isEmpty())
//            dialog.setContentText(content);
//
//        dialog.showAndWait();
//    }

    /**
     * Вызов диалогового окна для подтверждения некоторой информации
     * @param title текст заголовка окна
     * @param header текст заголовка
     * @param content текст контента
     * @return тип нажатой кнопки при работе с окном
     */
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
