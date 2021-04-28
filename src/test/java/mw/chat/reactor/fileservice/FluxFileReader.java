package mw.chat.reactor.fileservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import reactor.core.publisher.Flux;

public class FluxFileReader {

    public Flux<String> fileContent(Path file) {
        try {
            return Flux.fromStream(Files.readAllLines(file).stream());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
