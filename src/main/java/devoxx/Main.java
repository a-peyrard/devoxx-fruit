package devoxx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Optional;

/**
 * @author apeyrard
 */
public class Main {

	/**
	 * simple class to manage the input, output from the console
	 */
	static class Console implements AutoCloseable {
		final int maxWrongTypeTry = 3;

		final BufferedReader reader;
		final BufferedWriter writer;

		public Console() {
			reader = new BufferedReader(new InputStreamReader(System.in));
			writer = new BufferedWriter(new OutputStreamWriter(System.out));
		}

		public void writeLine(String line) throws IOException {
			writer.write(line);
			writer.newLine();
			writer.flush();
		}

		public void enterEnter(String message) throws IOException {
			writeLine(message);
			reader.readLine();
		}

		public <T> T ask(Class<T> responseType) throws IOException {
			return ask(responseType, Optional.empty());
		}

		public <T> T ask(Class<T> responseType, String question) throws IOException {
			return ask(responseType, Optional.ofNullable(question));
		}

		public String ask() throws IOException {
			return ask(String.class, Optional.empty());
		}

		public String ask(String question) throws IOException {
			return ask(String.class, Optional.ofNullable(question));
		}

		private <T> T ask(Class<T> responseType, Optional<String> question) throws IOException {
			for (int i = 0; i < maxWrongTypeTry; i++) {
				try {
					String q;
					if (question.isPresent()) {
						q = question.get() + " ";
					} else {
						q = "";
					}
					q += "? ";

					writer.write(q);
					writer.flush();
					String response = reader.readLine();
					if (response == null || response.trim().equals("")) {
						writeLine("empty response not allowed, try again...");
						continue;
					}
					return cast(responseType, response);
				} catch (ClassCastException ex) {
					ex.printStackTrace();
					writeLine("the response does not have the expected type: " + responseType);
					writeLine("try again...");
				}
			}
			throw new IllegalStateException("too much try without success...");
		}

		@SuppressWarnings("unchecked")
		private <T> T cast(Class<T> type, String value) {
			if (type == String.class) {
				return (T) value;
			}
			if (type == Integer.class) {
				return (T) Integer.valueOf(value);
			}
			throw new IllegalStateException("unknown type: " + type);
		}

		@Override
		public void close() throws Exception {
			reader.close();
			writer.close();
		}
	}

	public static void main(String[] args) throws Exception {
		try (final Console console = new Console()) {
			Integer age = console.ask(Integer.class, "what is the age of the capitain");
			console.writeLine("the age is: " + age);
		}
	}
}
