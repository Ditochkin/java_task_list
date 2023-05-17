module com.example.tasklist {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tasklist to javafx.fxml;
    exports com.example.tasklist;
}