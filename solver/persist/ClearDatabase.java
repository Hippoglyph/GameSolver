package solver.persist;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;

public class ClearDatabase {

	public static void main(String[] args) {
		try {
			Database.init();
			Database.wipeAllData();
			Database.close();
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
