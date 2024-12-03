module project.extensions {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.logging;


    opens project.extensions to javafx.fxml;
    exports project.extensions;
}