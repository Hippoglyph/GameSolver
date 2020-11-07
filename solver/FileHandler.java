package solver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public class FileHandler {

	private static final String baseFolderName = "storage";

	public static void save(String gameName, List<String> batch) throws IOException, URISyntaxException {
		String fileName = UUID.randomUUID().toString();
		File baseFolder = new File(baseFolderName);
		if (!baseFolder.exists())
			baseFolder.mkdir();
		File gameFolder = new File(baseFolder, gameName);
		if (!gameFolder.exists())
			gameFolder.mkdir();
		File newFile = new File(gameFolder, fileName);
		System.out.println("Storing: " + newFile.getAbsolutePath());
		FileWriter writer = new FileWriter(newFile);
		for (String line : batch) {
			writer.write(line + "\n");
		}
		writer.close();
	}
}
