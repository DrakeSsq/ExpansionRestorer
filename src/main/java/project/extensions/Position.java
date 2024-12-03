/**
 * @author Daniil Ersov IKB-32
 */

package project.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Класс, представляющий текущее положение в файловой системе.
 *
 * @version 1.0
 */
public class Position {

    /**
     * Логгер для записи событий класса.
     */
    private static final Logger logger = LogManager.getLogger(Position.class);

    /**
     * Текущий путь в файловой системе.
     */
    private static Path path;

    /**
     * Текущий файл.
     */
    private static File file;

    /**
     * Конструктор класса.
     *
     * @param path начальный путь в файловой системе
     */
    public Position(Path path) {
        if (Files.exists(path)) {
            Position.path = path;
            Position.file = null;
        }
    }

    /**
     * Возвращает текущий путь в файловой системе.
     *
     * @return текущий путь
     */
    public static Path getPath() {
        return path;
    }

    /**
     * Устанавливает текущий путь в файловой системе.
     *
     * @param path новый путь
     */
    public void setPath(Path path) {
        Position.path = path;

    }

    /**
     * Устанавливает текущий файл.
     *
     * @param string имя файла
     * @throws IllegalArgumentException если имя файла пустое
     * @throws RuntimeException если файл не найден
     */
    public static void setFile(String string) {
        if (string.isEmpty()) {
            logger.error("invalid file path");
            throw new IllegalArgumentException("invalid file path");

        } else {
            logger.info("successful file selection");
            Path path = getPath().resolve(Paths.get(string));
            if (path.toFile().exists()) {
                file = path.toFile();
            } else {
                logger.error("file not found");
                throw new RuntimeException("file not found");
            }
        }
    }

    /**
     * Возвращает текущий файл.
     *
     * @return текущий файл
     */
    public static File getFile() {
        return file;
    }

    /**
     * Возвращает имя текущего файла.
     *
     * @return имя текущего файла
     */
    public static String getFileName() {
        if (file==null) {
            logger.error("the file is not selected");
            MainApp.appendToConsole("the file is not selected");
        } else {
            logger.info("viewing the selected file");
            return file.getName();
        }
        return "";
    }

    /**
     * Возвращает список файлов в текущей директории.
     *
     * @return список файлов
     * @throws RuntimeException если возникает ошибка при чтении директории
     */
    public ArrayList<Path> getFilesDirectory() {
        ArrayList<Path> arr = new ArrayList<>();
        logger.info("trying to view directory files");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path data : stream) {
                arr.add(data.getFileName());
            }
        } catch (IOException e) {
            logger.error("an unsuccessful to view the files of the directory");
            throw new RuntimeException(e);
        }
        logger.info("a successful attempt to view the files of the directory");
        return arr;
    }

    /**
     * Переходит в другую директорию.
     *
     * <p>Метод поддерживает следующие варианты:
     * <ul>
     *     <li>имя директории - переходит в указанную директорию</li>
     *     <li>".." - переходит назад в родительскую директорию</li>
     * </ul>
     *
     * @param string имя директории или ".." для перехода назад
     */
    public void changePath(String string) {
        if (string.equals("..")) {
            if (path.getParent()!=null) {
                logger.info("moving back through the directory");
                setPath(path.getParent());
            } else {
                logger.error("directory navigation error (end of file explorer)");
                MainApp.appendToConsole("is home dir");

            }
        } else {
            Path targetPath = path.resolve(string).normalize();

            if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {
                setPath(targetPath);
                logger.info("successful transition to another directory");
            } else {
                logger.error("unsuccessful attempt to find the directory");
                MainApp.appendToConsole("directory not found: " + string);
            }
        }
    }
}