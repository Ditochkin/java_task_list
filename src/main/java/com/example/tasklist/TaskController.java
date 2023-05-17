package com.example.tasklist;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TaskController {

    @FXML
    private TextField taskTF;
    @FXML
    private TextField dateTF;
    @FXML
    private TextField scoreTF;
    @FXML
    private TextField compDateTF;
    @FXML
    private TextField commentTF;
    @FXML
    private TextField setScoreTF;

    @FXML
    private TableView<Task> tableV;
    @FXML
    private TableColumn taskCol;
    @FXML
    private TableColumn dateCol;
    @FXML
    private TableColumn scoreCol;
    @FXML
    private TableColumn compDateCol;
    @FXML
    private TableColumn commentCol;
    @FXML
    private TableColumn setScoreCol;

    Integer lastId;

    ArrayList<Task> taskList;
    ObservableList<Task> filteredList;

    @FXML
    public void initialize() throws FileNotFoundException {
        taskList = new ArrayList<Task>();

        scoreTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    scoreTF.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        setScoreTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    setScoreTF.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        taskCol.setCellValueFactory(new PropertyValueFactory<>("Task"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("Score"));
        compDateCol.setCellValueFactory(new PropertyValueFactory<>("CompDate"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("Comment"));
        setScoreCol.setCellValueFactory(new PropertyValueFactory<>("SetScore"));

        taskList = readCSV("data.csv");
        filteredList = FXCollections.observableArrayList(taskList);
        tableV.setItems(filteredList);
    }
    public static boolean isValidDate(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    @FXML
    protected void onCLickAdd()
    {
        if (taskList.isEmpty())
        {
            lastId = 1;
        }
        else
        {
            lastId = taskList.get(taskList.size() - 1).getId() + 1;
        }

        Task task = new Task( 0,
                             taskTF.getText(),
                             dateTF.getText(),
                             scoreTF.getText(),
                     "-",
                     "-",
                      "-");

        if (taskTF.getText().isEmpty() || dateTF.getText().isEmpty() || dateTF.getText().isEmpty())
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Пустое поле!", "Пожалуйста, заполните все поля");
            return;
        }

        if (!isValidDate(dateTF.getText()))
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Неверный формат даты!", "Пожалуйста, введите дату в формате dd.MM.yyyy");
            return;
        }

        taskList.add(task);

        filteredList = FXCollections.observableArrayList(taskList);
        tableV.setItems(filteredList);

        taskTF.clear();
        dateTF.clear();
        scoreTF.clear();

        exportToCSV(taskList, "data.csv");
    }

    @FXML
    protected void onClickDelete() {
        Task selectedItem = tableV.getSelectionModel().getSelectedItem();
        taskList.remove(selectedItem);

        filteredList = FXCollections.observableArrayList(taskList);
        tableV.setItems(filteredList);

        exportToCSV(taskList, "data.csv");
    }

    @FXML
    protected void onClickRate() {
        Task selectedItem = tableV.getSelectionModel().getSelectedItem();

        var i = 0;
        for (i = 0; i < taskList.size(); i++)
        {
            if (selectedItem.getId() == taskList.get(i).getId())
            {
                break;
            }
        }

        if (i == taskList.size())
            return;

        Task curItem = taskList.get(i);

        if (compDateTF.getText().isEmpty() || commentTF.getText().isEmpty() || setScoreTF.getText().isEmpty())
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Пустое поле!", "Пожалуйста, заполните все поля");
            return;
        }

        if (compareStringsAsIntegers(setScoreTF.getText(), curItem.getScore()))
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Поставленный балл больше максиамльного!", "Пожалуйста, поменяйте оценку");
            return;
        }

        if (!isValidDate(compDateTF.getText()))
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Неверный формат даты!", "Пожалуйста, введите дату в формате dd.MM.yyyy");
            return;
        }

        if (compareDates(curItem.getDate(), compDateTF.getText()))
        {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Дата выполнения раньше, чем дата начала задания!", "Пожалуйста, введите другую дату");
            return;
        }

        Task newItem = new Task(curItem.getId(),
                                curItem.getTask(),
                                curItem.getDate(),
                                curItem.getScore(),
                                compDateTF.getText(),
                                commentTF.getText(),
                                setScoreTF.getText());

        taskList.set(i, newItem);

        filteredList = FXCollections.observableArrayList(taskList);
        tableV.setItems(filteredList);

        compDateTF.clear();
        commentTF.clear();
        setScoreTF.clear();

        exportToCSV(taskList, "data.csv");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean compareStringsAsIntegers(String str1, String str2) {
        int num1 = Integer.parseInt(str1);
        int num2 = Integer.parseInt(str2);

        return num1 > num2;
    }

    public static boolean compareDates(String dateStr1, String dateStr2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);

        try {
            Date date1 = dateFormat.parse(dateStr1);
            Date date2 = dateFormat.parse(dateStr2);

            return date1.compareTo(date2) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void exportToCSV(ArrayList<Task> dataList, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("Task,Date,Score,Completion Date,Comment,Set Score");
            writer.newLine();

            for (Task data : dataList) {
                writer.write(data.getTask() + "," +
                        data.getDate() + "," +
                        data.getScore() + "," +
                        data.getCompDate() + "," +
                        data.getComment() + "," +
                        data.getSetScore());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Task> readCSV(String fileName) {
        ArrayList<Task> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Пропуск заголовка
                }

                String[] fields = line.split(",");
                var i = 1;
                if (fields.length == 6) {
                    String task = fields[0].trim();
                    String date = fields[1].trim();
                    String score = fields[2].trim();
                    String compDate = fields[3].trim();
                    String comment = fields[4].trim();
                    String setScore = fields[5].trim();

                    Task taskData = new Task(i, task, date, score, compDate, comment, setScore);
                    i++;
                    dataList.add(taskData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}