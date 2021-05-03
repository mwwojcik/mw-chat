package mw.chat.reactor.fileservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FluxFileReaderTest {

    private Path workingDir;
    private FluxFileReader fileReader = new FluxFileReader();
    private FluxBufferedFileReader bufferedFileReader = new FluxBufferedFileReader();

    @BeforeEach
    public void init() {
        this.workingDir = Path.of("", "src/test/resources/mw/chat/reactor/fileservice/");
    }

    @DisplayName("Should create flux from text file lines")
    @Test
    void shouldCreateFluxFromTextFileLines() {
        var file = workingDir.resolve(Path.of("content.txt"));
        fileReader.fileContent(file).take(2).subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should create flux from buffered file lines")
    @Test
    void shouldCreateFluxFromBufferedFileLines() {
        var file = workingDir.resolve(Path.of("content.txt"));

        bufferedFileReader.readFileContent(file).subscribe(new DefaultSimpleSubscriber());
    }

    @DisplayName("shoul generate content file")
    @Test
    void shoulGenerateContentFile()
        throws IOException {
        List<String> pLines = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            pLines.add(String.format("line %s", i));
        }
        var result = workingDir.resolve(Path.of("content.txt"));
        Files.write(result, pLines);
    }
}
