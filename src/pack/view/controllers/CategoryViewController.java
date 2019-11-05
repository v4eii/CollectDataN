package pack.view.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.db.entity.exceptions.NonexistentEntityException;
import pack.services.Service;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Панель просмотра и редактирования компетенций по специализациям
 * @author v4e
 */
public class CategoryViewController implements Initializable {

    @FXML
    private ListView<Skills> skillsListView;
    @FXML
    private ListView<Category> categoryListView;
    @FXML
    private BorderPane bPane;
    @FXML
    private Button btnDeleteSkill, btnAddSkill;

    private List<Category> categoryList;
    private List<Skills> skillsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        categoryList = FXCollections.observableArrayList(DBBean.getInstance().getCategoryJPAController().findCategoryEntities());
        btnAddSkill.setOnAction(event -> {
            if (categoryListView.getSelectionModel().getSelectedItem() != null) {
                TextInputDialog t = new TextInputDialog();
                t.setHeaderText("Введите название компетенции");
                Optional<String> result = t.showAndWait();
                result.ifPresent(s -> {
                    if (Service.showConfirmDialog("Добавление компетенций", "Подтверждение", "Вы уверены что хотите добавить компетенцию \""
                            + t.getEditor().getText() + "\" в категорию \"" + categoryListView.getSelectionModel().getSelectedItem().getName() + "\"?").get() == ButtonType.OK) {
                        Skills skill = new Skills();
                        skill.setName(t.getEditor().getText());
                        skill.setIdCategory(categoryListView.getSelectionModel().getSelectedItem());
                        skill.setHitCount(0);
                        DBBean.getInstance().getSkillsJPAController().create(skill);
                        skillsList.add(skill);
                        skillsListView.refresh();
                    }
                });
            }
            else {
                Service.showWarningDialog("Не выбрано категорий", "Выберите одну из категорий");
            }
        });

        btnDeleteSkill.setOnAction(event -> {
            if (skillsListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    if (Service.showConfirmDialog("Удаление компетенций", "Подтверждение", "Вы уверены что хотите удалить компетенцию?").get() == ButtonType.OK) {
                        DBBean.getInstance().getSkillsJPAController().destroy(skillsListView.getSelectionModel().getSelectedItem().getIdSkill());
                        skillsList.remove(skillsListView.getSelectionModel().getSelectedItem());
                        skillsListView.refresh();
                    }
                }
                catch (NonexistentEntityException e) {
//                    Service.showErrorDialog(e, "Ошибка удаления", "Произошла ошибка удаления данных");
                }
            }
            else {
                Service.showWarningDialog("Не выбрано компетенций", "Выберите одну из компетенций");
            }
        });

        skillsListView.setCellFactory(param -> new ListCell<Skills>() {
            @Override
            protected void updateItem(Skills item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty)
                    setText(item.getName());
                else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        categoryList.sort((o1, o2) -> {
            if (o1.getIdCategory() > o2.getIdCategory())
                return 1;
            else
                return -1;
        });
        categoryListView.setItems((ObservableList<Category>) categoryList);
        categoryListView.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty)
                    setText(item.getIdCategory() + ". " + item.getName());
                else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        categoryListView.setOnMouseReleased(event -> {
            skillsListView.getItems().clear();
            List<Skills> skillsEntities = DBBean.getInstance().getSkillsJPAController().findSkillsEntities();
            skillsList = FXCollections.observableArrayList(skillsEntities.stream().filter(skills -> skills.getIdCategory().equals(categoryListView.getSelectionModel().getSelectedItem())).collect(Collectors.toList()));
            skillsListView.setItems((ObservableList<Skills>) skillsList);
        });
    }

    public BorderPane getbPane() {
        return bPane;
    }
}
