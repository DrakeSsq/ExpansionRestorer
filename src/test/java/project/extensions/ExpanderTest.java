package project.extensions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ExpanderTest {

    @BeforeEach
    void setUp() {
        Expander.init();

        Path initialPath = Paths.get(System.getProperty("user.dir"));
        new Position(initialPath);
    }

    @Test
    void testRenameFileForPng() {

        Path testFilePath = null;
        File testFile = null;

        try {

            testFilePath = Files.createTempFile("testfile", "");
            testFile = testFilePath.toFile();

            try (FileOutputStream fos = new FileOutputStream(testFile)) {
                byte[] pngSignature = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};  // Сигнатура PNG
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
                try {
                    Files.delete(testFilePath);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
