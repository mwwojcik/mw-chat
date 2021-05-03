package mw.chat.reactor.fileservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

public class FluxBufferedFileReader {

    public Flux<String> readFileContent(Path file) {
        return Flux.generate(create(file), readLine(), close());
    }

    private Callable<BufferedReader> create(Path path) {
        return () -> Files.newBufferedReader(path);
    }

    private BiFunction<BufferedReader, SynchronousSink<String>, BufferedReader> readLine() {
        return (br, sink) -> {

            try {
                var line = br.readLine();

                if (Objects.isNull(line)) {
                    sink.complete();
                } else {
                    sink.next(line);
                }
            } catch (IOException e) {
                sink.error(e);
            }
            return br;
        };
    }

    private Consumer<BufferedReader> close() {
        return (br) -> {
            try {
                br.close();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }

}
