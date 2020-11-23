package solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileHandler {

	private static final String baseFolderName = "storage";

	private static File getBaseFolder() {
		File baseFolder = new File(baseFolderName);
		if (!baseFolder.exists())
			baseFolder.mkdir();
		return baseFolder;
	}

	private static File getGameFolder(String gameName) {
		File gameFolder = new File(getBaseFolder(), gameName);
		if (!gameFolder.exists())
			gameFolder.mkdir();
		return gameFolder;
	}

	public static void save(String gameName, List<String> batch) throws IOException, URISyntaxException {
		String fileName = UUID.randomUUID().toString();
		File newFile = new File(getGameFolder(gameName), fileName);
		System.out.println("Storing: " + newFile.getAbsolutePath());
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		for (String line : batch) {
			writer.write(line + "\n");
		}
		writer.close();
	}

	public static void load(String gameName, Map<Long, Double> scoreMap) throws IOException, URISyntaxException {
		File gameFolder = getGameFolder(gameName);

		if (gameFolder.isDirectory()) {
			for (File file : gameFolder.listFiles()) {
				System.out.println(file.toString());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					String[] tokens = currentLine.trim().split(" ");
					if (tokens.length == 2) {
						long encodedState = Long.parseLong(tokens[0]);
						double score = Double.parseDouble(tokens[1]);
						if (scoreMap.containsKey(encodedState))
							continue;
						scoreMap.put(encodedState, score);
					}
				}
			}
		}

	}
}
