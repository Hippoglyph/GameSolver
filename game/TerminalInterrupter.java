package game;

import java.util.Scanner;
import java.util.concurrent.Executors;

public class TerminalInterrupter {

	public TerminalInterrupter(Runnable runnable) {
		Executors.newSingleThreadExecutor().execute(() -> {
			Scanner inputReader = new Scanner(System.in);

			while (true) {
				if (inputReader.hasNext()) {
					String responseLine = inputReader.next();
					if (responseLine.contains("Q")) {
						runnable.run();
						break;
					}
				}
			}
			inputReader.close();
		});
	}
}
