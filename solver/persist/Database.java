package solver.persist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedConnection;
import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedDatabase;
import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;
import nl.cwi.monetdb.embedded.resultset.QueryResultSet;

public class Database {

	private static final String BASE_FOLDER_NAME = "storage";
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
		System.out.println("Connecting to database...");
		MonetDBEmbeddedDatabase.startDatabase(getBaseFolder().getAbsolutePath());
		System.out.println("Connected");
	}

	public static void store(String tableName, List<Pair<Long, Double>> inputs) throws MonetDBEmbeddedException {

		String values = IntStream.range(0, inputs.size())
				.mapToObj(i -> "(" + inputs.get(i).first() + "," + inputs.get(i).second() + ")")
				.collect(Collectors.joining(","));

		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		connection.startTransaction();
		int numberOfInsertions = connection.executeUpdate(
				"INSERT INTO " + tableName + "(" + ENCODED_NAME + "," + SCORE_NAME + ")  VALUES " + values);
		connection.commit();

		if (inputs.size() != numberOfInsertions)
			throw new MonetDBEmbeddedException("Invalid Insertion");

		connection.close();
	}

	public static Pair<Long, Double> fetch(String tableName, long encodedState) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection
				.executeQuery("SELECT * FROM " + tableName + " WHERE " + ENCODED_NAME + "=" + encodedState);
		Pair<Long, Double> pair = result.getNumberOfRows() < 1 ? null
				: new Pair<>(result.getLongByColumnIndexAndRow(1, 1), result.getDoubleByColumnIndexAndRow(2, 1));
		connection.close();
		return pair;
	}

	public static List<Pair<Long, Double>> fetch(String tableName) throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection.executeQuery("SELECT * FROM " + tableName);
		List<Pair<Long, Double>> pairs = new ArrayList<Pair<Long, Double>>();
		for (int row = 1; row <= result.getNumberOfRows(); row++) {
			pairs.add(new Pair<Long, Double>(result.getLongByColumnIndexAndRow(1, row),
					result.getDoubleByColumnIndexAndRow(2, row)));
		}
		connection.close();
		return pairs;
	}

	public static void wipeAllData() throws MonetDBEmbeddedException {
		MonetDBEmbeddedConnection connection = MonetDBEmbeddedDatabase.createConnection();
		QueryResultSet result = connection.executeQuery("SELECT t.name FROM sys.tables t WHERE t.system=false");

		for (int row = 1; row <= result.getNumberOfRows(); row++) {
			connection.executeUpdate("DROP TABLE " + result.getStringByColumnIndexAndRow(1, row));
		}
		System.out.println("Wiped " + result.getNumberOfRows() + " tables");

		connection.close();
	}

	private static File getBaseFolder() {
		File baseFolder = new File(BASE_FOLDER_NAME);
		if (!baseFolder.exists())
			baseFolder.mkdir();
		return baseFolder;
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
