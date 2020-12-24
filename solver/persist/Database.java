package solver.persist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedConnection;
import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedDatabase;
import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;
import nl.cwi.monetdb.embedded.resultset.QueryResultSet;

public class Database {

	private static final String BASE_FOLDER_NAME = "storage";
	private static final String PEDNING_STORAGE_NAME = "pending";
	private static final String ENCODED_NAME = "encodedState";
	private static final String SCORE_NAME = "score";

	public static void init(String tableName) throws MonetDBEmbeddedException {
		init();
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		connection.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + ENCODED_NAME + " bigint, "
				+ SCORE_NAME + " double)");

		connection.close();
	}

	public static void init() throws MonetDBEmbeddedException {
		if (!isRunning()) {
			System.out.println("Connecting to database...");
			MonetDBEmbeddedDatabase.startDatabase(getBaseFolder().getAbsolutePath());
			System.out.println("Connected");
		}
	}

	public static void store(String tableName, List<Pair<Long, Double>> inputs) throws MonetDBEmbeddedException {

		File file = createCSVFile(inputs);

		int numberOfInsertions = storeFromFile(file.getAbsolutePath().replace("\\", "\\\\"), tableName);

		if (inputs.size() != numberOfInsertions || !file.delete())
			throw new MonetDBEmbeddedException("Invalid Insertion");
	}

	public static synchronized int storeFromFile(String filePath, String tableName) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();

		int numberOfInsertions = connection
				.executeUpdate("COPY INTO " + tableName + " FROM '" + filePath + "' USING DELIMITERS '|', '\n', ''");

		connection.close();
		return numberOfInsertions;
	}

	public static Pair<Long, Double> fetch(String tableName, long encodedState) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection
				.executeQuery("SELECT * FROM " + tableName + " WHERE " + ENCODED_NAME + "=" + encodedState);
		Pair<Long, Double> pair = result.getNumberOfRows() < 1 ? null
				: new Pair<>(result.getLongByColumnIndexAndRow(1, 1), result.getDoubleByColumnIndexAndRow(2, 1));
		result.close();
		connection.close();
		return pair;
	}

	public static List<Pair<Long, Double>> fetch(String tableName, int limit) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection.executeQuery("SELECT * FROM " + tableName + " LIMIT " + limit);
		List<Pair<Long, Double>> pairs = new ArrayList<Pair<Long, Double>>();
		for (int row = 1; row <= result.getNumberOfRows(); row++) {
			pairs.add(new Pair<Long, Double>(result.getLongByColumnIndexAndRow(1, row),
					result.getDoubleByColumnIndexAndRow(2, row)));
		}
		result.close();
		connection.close();
		return pairs;
	}

	public static int size(String tableName) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection.executeQuery("SELECT COUNT(*) FROM " + tableName);
		int size = result.getIntegerByColumnIndexAndRow(1, 1);
		result.close();
		connection.close();
		return size;
	}

	public static void wipeAllData() throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection.executeQuery("SELECT t.name FROM sys.tables t WHERE t.system=false");

		for (int row = 1; row <= result.getNumberOfRows(); row++) {
			connection.executeUpdate("DROP TABLE " + result.getStringByColumnIndexAndRow(1, row));
		}
		System.out.println("Wiped " + result.getNumberOfRows() + " tables");
		result.close();
		connection.close();
	}

	private static File createCSVFile(List<Pair<Long, Double>> inputs) {
		String fileName = UUID.randomUUID().toString();
		File file = new File(getPendingFolder(), fileName + ".csv");
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (Pair<Long, Double> pair : inputs) {
				writer.write(String.format(Locale.US, "%d|%f\n", pair.first(), pair.second()));
			}

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;
	}

	private static File getBaseFolder() {
		File baseFolder = new File(BASE_FOLDER_NAME);
		if (!baseFolder.exists())
			baseFolder.mkdir();
		return baseFolder;
	}

	private static File getPendingFolder() {
		File pendingFolder = new File(getBaseFolder(), PEDNING_STORAGE_NAME);
		if (!pendingFolder.exists())
			pendingFolder.mkdir();
		return pendingFolder;
	}

	public static boolean isRunning() {
		return MonetDBEmbeddedDatabase.isDatabaseRunning();
	}

	public static void close() {
		try {
			MonetDBEmbeddedDatabase.stopDatabase();
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
