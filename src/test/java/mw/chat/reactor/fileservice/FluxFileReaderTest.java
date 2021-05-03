package mw.chat.reactor.fileservice;

import java.nio.file.Path;
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
}
