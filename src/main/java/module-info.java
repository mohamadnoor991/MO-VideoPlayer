module com.example.moplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;



    opens com.example.moplayer to javafx.fxml;
    exports com.example.moplayer;
}