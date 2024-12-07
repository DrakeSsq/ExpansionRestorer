package project.extensions;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функциональности класса {@link Expander}.
 *
 * <p>Класс включает тесты для проверки восстановления расширения файлов на основе их сигнатур.
 * Использует временные файлы для имитации реальных файлов с различными сигнатурами.</p>
 *
 * @author Daniil Ershov IKB-32
 * @version 1.0
 */
class ExpanderTest {
    /**
     * Инициализация JavaFX Toolkit перед запуском тестов.
     *
     * <p>JavaFX Toolkit должен быть запущен перед созданием компонентов JavaFX, таких как {@link TextArea}.
     * Этот метод вызывается один раз перед всеми тестами.</p>
     */
    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }
    /**
     * Инициализация окружения для каждого теста.
     *
     * <p>Метод выполняет следующие действия:
     * <ul>
     *     <li>Инициализирует карту сигнатур файлов через {@link Expander#init()}.</li>
     *     <li>Устанавливает текущую директорию с помощью {@link Position}.</li>
     *     <li>Создает компонент {@link TextArea} для вывода сообщений в консоль.</li>
     * </ul>
     * </p>
     */
    @BeforeEach
    void setUp() {
        Expander.init();

        Path initialPath = Paths.get(System.getProperty("user.dir"));
        new Position(initialPath);

        MainApp.outputArea = new TextArea();

    }
    /**
     * Тестирует метод {@link Expander#renameFile()} для файла с сигнатурой PNG.
     *
     * <p>Сценарий теста:
     * <ul>
     *     <li>Создается временный файл без расширения.</li>
     *     <li>Записывается сигнатура PNG в файл.</li>
     *     <li>Устанавливается этот файл как текущий файл с помощью {@link Position#setFile(String)}.</li>
     *     <li>Вызывается метод {@link Expander#renameFile()}, который должен восстановить расширение файла.</li>
     *     <li>Проверяется, что файл был успешно переименован с добавлением расширения ".png".</li>
     * </ul>
     * </p>
     *
     * <p>После выполнения теста временные файлы удаляются.</p>
     *
     * @throws RuntimeException если возникает ошибка при выполнении метода {@link Expander#renameFile()}
     */
    @Test
    void testRenameFileForPng() {

        Path testFilePath = null;
        File testFile = null;

        try {
            testFilePath = Files.createTempFile("testfile", "");
            testFile = testFilePath.toFile();
            try (FileOutputStream fos = new FileOutputStream(testFile)) {

                byte[] pngSignature = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
                fos.write(pngSignature);
            }

            Position.setFile(testFile.getAbsolutePath());
            Expander.renameFile();
            File renamedFile = Position.getFile();

            assertNotNull(renamedFile, "the file must be renamed");
            assertTrue(renamedFile.exists(), "the renamed file must exist");
            assertTrue(renamedFile.getName().endsWith(".png"), "the file must be renamed with the .png extension");

        } catch (IOException e) {
            fail("failed to create or write a test file: " + e.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            if (testFile != null && testFile.exists()) {

                testFile.delete();

            }
            if (testFilePath != null && Files.exists(testFilePath)) {

                try {Files.delete(testFilePath);

                } catch (IOException ignored) {
                }
            }
        }
    }
}
