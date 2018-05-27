package fr.umlv.ditto.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Parser {

	public static void parse(Path path, Consumer<String[]> consumer) throws IOException {
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				String[] tokens = line.split(" ");
				consumer.accept(tokens);
			});
		}
	}
	
	public static void parseSkipFirst(Path path, Consumer<String[]> consumer) throws IOException {
		try (Stream<String> lines = Files.lines(path)) {
			lines.skip(1).forEach(line -> {
				String[] tokens = line.split(" ");
				consumer.accept(tokens);
			});
		}
	}
}
