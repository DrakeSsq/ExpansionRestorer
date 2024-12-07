/**
 * @author Daniil Ersov IKB-32
 */

package project.extensions;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Основной класс приложения.
 *
 * @version 1.0
 */
public class MainApp extends Application {

    /**
     * Логгер для записи событий приложения.
     */
    private static final Logger logger = LogManager.getLogger(MainApp.class);

    /**
     * Объект, представляющий текущее положение в файловой системе.
     */
    private final Position position = new Position(Paths.get("").toAbsolutePath());

    /**
     * Текстовая область для вывода информации.
     */
    public static TextArea outputArea;

    /**
     * Поле для ввода команд.
     */
    private TextField inputField;

    /**
     * Метод, вызываемый при запуске приложения.
     *
     * @param stage сцена приложения
     */
    @Override
    public void start(Stage stage) {

        stage.setTitle("Extension Restorer");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: white;");

        inputField = new TextField();
        inputField.setPromptText("Enter command...");
        inputField.setStyle("-fx-control-inner-background: black; -fx-text-fill: white;");

        inputField.setOnAction(event -> handleInput());

        VBox layout = new VBox(10, outputArea, inputField);
        layout.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();

        Expander.init();

        appendToConsole("type \"help\" to view the commands");
        appendToConsole(Position.getPath() + "$ ");
    }


    /**
     * Метод, обрабатывающий ввод пользователя.
     */
    private void handleInput() {

        try {
            String input = inputField.getText().strip();
            inputField.clear();

            if (!input.isEmpty()) {
                processCommand(input);
            }

            appendToConsole(Position.getPath() + "$ ");
        } catch (Exception e) {
            appendToConsole("Error processing input: " + e.getMessage());
        }
    }


    /**
     * Метод, выполняющий команду, введенную пользователем.
     *
     * <p>Поддерживаемые команды:
     * <ul>
     *     <li>ls - вывод списка файлов в текущей директории</li>
     *     <li>cd - смена директории</li>
     *     <li>take - выбор файла для восстановления</li>
     *     <li>see - просмотр выбранного файла</li>
     *     <li>rename - восстановление расширения выбранного файла</li>
     *     <li>exit - выход из приложения</li>
     *     <li>help - вывод справки по командам</li>
     * </ul>
     *
     * @param input команда, введенная пользователем
     */
    private void processCommand(String input) {

        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        try {
            switch (command) {
                case "ls":
                    for (Path path : position.getFilesDirectory()) {
                        appendToConsole(" -> "+path.toString());
                    }
                    break;
                case "cd":
                    position.changePath(argument);
                    break;
                case "take":
                    Position.setFile(argument);
                    break;
                case "see":
                    appendToConsole(Position.getFileName());
                    break;
                case "rename":
                    Expander.renameFile();
                    break;
                case "exit":
                    appendToConsole("exiting...");
                    logger.info("еnd of the program");
                    System.exit(0);
                    break;
                case "help":
                    help();
                    break;
                default:
                    appendToConsole("unknown command: " + command);
                    break;
            }
        } catch (Exception e) {
            appendToConsole("error executing command: " + e.getMessage());
        }
    }


    /**
     * Метод, добавляющий текст в текстовую область.
     *
     * @param text текст, который нужно добавить
     */
    public static void appendToConsole(String text) {
        outputArea.appendText(text + "\n");
    }


    /**
     * Метод, выводящий справку по командам приложения.
     */
    public static void help() {
        appendToConsole("ls - look search");
        appendToConsole("cd <> - moving");
        appendToConsole("take <> - select a file for future recovery");
        appendToConsole("see - viewing the selected file");
        appendToConsole("rename - restore the extension of the selected file");
        appendToConsole("exit - exiting the program");
    }



    /**
     * Основной метод приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        logger.info("the program is running");
        launch(args);
    }
}
