/**
 * @author Daniil Ersov IKB-32
 */

package project.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Класс, расширяющий функциональность класса {@link Position} и позволяющий
 * определять тип файла по его сигнатурам и переименовывать файлы с неизвестным
 * расширением.
 *
 */
public class Expander extends Position{

    /**
     * Карта, содержащая сигнатуры файлов для различных типов файлов.
     */
    private static final HashMap<String, ArrayList<String>> extensions = new HashMap<>();

    /**
     * Логгер для записи событий класса.
     */
    private static final Logger logger = LogManager.getLogger(Expander.class);

    /**
     * Конструктор класса.
     *
     * @param path путь к файлу или директории
     */
    public Expander(Path path) {
        super(path);
    }

    /**
     * инициализирует карту сигнатур файлов.
     */
    public static void init() {
        extensions.put("png", new ArrayList<>(List.of("89 50 4E 47")));
        extensions.put("zip", new ArrayList<>(List.of("50 4B 03 04")));
        extensions.put("jpg", new ArrayList<>(List.of("FF D8 FF DB", "FF D8 FF E0", "FF D8 FF E1")));
        extensions.put("webp",new ArrayList<>(List.of("52 49 46 46")));
        extensions.put("doc", new ArrayList<>(List.of("0D 44 4F 43")));
        extensions.put("gif", new ArrayList<>(List.of("47 49 46 38")));
        extensions.put("pdf", new ArrayList<>(List.of("25 50 44 46")));
        extensions.put("RAR", new ArrayList<>(List.of("52 61 72 21")));
        extensions.put("mp3", new ArrayList<>(List.of("49 44 33")));
        extensions.put("mp4", new ArrayList<>(List.of("66 74 79 70")));
        extensions.put("7z", new ArrayList<>(List.of("37 7A BC AF")));
        extensions.put("iso", new ArrayList<>(List.of("43 44 30 30")));
        extensions.put("bmp", new ArrayList<>(List.of("42 4D")));
    }

    /**
     * Возвращает сигнатуру файла.
     *
     * @return сигнатура файла в виде строки
     * @throws IllegalStateException если файл не выбран
     */
    public static StringBuilder getExtensions() {
        StringBuilder result = new StringBuilder();

        if (Position.getFile() == null) {
            logger.error("an attempt to view the file. the file is not selected");
            throw new IllegalStateException("the file is not selected!");
        } else {


            try (FileInputStream input = new FileInputStream(Position.getFile())) {
                byte[] bytes = new byte[4];
                input.read(bytes);

                for (byte b : bytes) {
                    result.append(String.format("%02X ", b));
                }
                logger.info("getting the file extension");
                return result;


            } catch (IOException e) {
                logger.error("ioexception");
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * Переименовывает файл с неизвестным расширением в соответствии с его типом.
     *
     * @throws Exception если возникает ошибка при переименовании файла
     */
    public static void renameFile() throws Exception {
        File file = Position.getFile();

        if (file == null) {
            logger.error("an attempt to view the fiIe. the file is not selected");
            throw new IllegalStateException("the file is not selected!");

        } else {
            logger.info("an attempt to rename a file");
            StringBuilder extension = getExtensions();
            String signature = extension.toString().strip();


            String newExtension = null;
            for (String key : extensions.keySet()) {

                for (String value : extensions.get(key)) {

                    if (value.equalsIgnoreCase(signature)) {
                        newExtension = key;
                        break;
                    }
                }
            }

            if ("zip".equals(newExtension)) {
                newExtension = detectZipFileType(file);
            }


            if (newExtension != null) {

                String newFileName = file.getParent() + File.separator + file.getName().split("\\.")[0] + "." + newExtension;


                File renamedFile = new File(newFileName);
                if (file.renameTo(renamedFile)) {

                    Position.setFile(renamedFile.getAbsolutePath());
                    logger.info("successful file renaming attempt");
                    MainApp.appendToConsole("file renamed to: " + renamedFile.getName());
                } else {
                    logger.error("failed to rename file");
                    throw new IllegalStateException("failed to rename file");
                }
            } else {
                logger.error("unknown file type. cannot rename");
                throw new IllegalStateException("unknown file type. cannot rename");
            }
        }
    }

    /**
     * Определяет тип файла в формате zip (docx, pptx, xlsx или zip).
     *
     * @param file файл в формате zip
     * @return тип файла (docx, pptx, xlsx или zip)
     */
    private static String detectZipFileType(File file) {
        logger.info("trying to determine the zip extension");
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.startsWith("word/")) {
                    logger.info("the file is a docx");
                    return "docx";
                } else if (name.startsWith("ppt/")) {
                    logger.info("the file is a pptx");
                    return "pptx";
                } else if (name.startsWith("xl/")) {
                    logger.info("the file is a xlsx");
                    return "xlsx";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("the file is a zip");
        return "zip";
    }
}
