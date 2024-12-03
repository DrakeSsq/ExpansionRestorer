package project.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    private Position position;
    private Path initialPath;

    @BeforeEach
    void setUp() {
        initialPath = Paths.get(System.getProperty("user.dir"));
        position = new Position(initialPath);
    }

    @Test
    void testConstructorSetsCorrectPath() {
        assertEquals(initialPath, Position.getPath(), "the constructor must set the initial path");
    }

    @Test
    void testSetPath() {
        Path newPath = initialPath.resolve("src");
        position.setPath(newPath);
        assertEquals(newPath, Position.getPath(), "setPath should change the current path");
    }

    @Test
    void testChangePath() {
        position.changePath("..");
        assertEquals(initialPath.getParent(), Position.getPath());
    }

}

