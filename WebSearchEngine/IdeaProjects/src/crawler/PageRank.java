
package crawler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import database.DatabaseConnection;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.Vectors;
import org.la4j.matrix.DenseMatrix;
import org.la4j.vector.dense.BasicVector;


public class PageRank {

	static Connection connection = null;
	Statement statement = null;

	static DatabaseConnection databaseConnection = new DatabaseConnection();

	public static void main(final String[] args) throws Exception {
		computePageRank();
	}

	public static void computePageRank() throws ClassNotFoundException, SQLException {
		int number_of_iterations = 0;
		double distance = 1.0;
		HashMap<Integer, Integer> documentIds = new HashMap<Integer, Integer>();
		documentIds = getDocIds();
		int[][] allLinks = null;
		allLinks = getAllLinks(documentIds);
		Matrix transitionMatrix = generateTransitionMatrix(documentIds, allLinks);

		Vector rank = BasicVector.zero(documentIds.size());
		rank.set(0, 1.0);
		while (number_of_iterations < 60 || distance > 0.001) {
			number_of_iterations++;
			Vector newRank = rank.multiply(transitionMatrix);
			distance = rank.subtract(newRank).fold(Vectors.mkManhattanNormAccumulator());
			rank = newRank;
		}
		updatePageRankDB(documentIds,rank);
	}

	public static HashMap<Integer, Integer> getDocIds() throws ClassNotFoundException, SQLException {
		HashMap<Integer, Integer> documentIds = new HashMap<Integer, Integer>();

		//gets the db connection
		connection = databaseConnection.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			if (connection != null) {
				//Store document frequency in features table of DB
				String query = "SELECT DISTINCT docid FROM documents order by docid";
				preparedStatement = connection.prepareStatement(query);
				resultSet = preparedStatement.executeQuery();
				int index = 0;
				while (resultSet.next()) {
					int docId = resultSet.getInt("docid");
					documentIds.put(docId, index++);
				}

			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
		return documentIds;
	}

	public static int[][] getAllLinks(HashMap<Integer, Integer> documentIds) throws ClassNotFoundException, SQLException {
		int totalDocuments = documentIds.size();
		DatabaseConnection databaseConnection = new DatabaseConnection();
		connection = databaseConnection.getConnection();
		ResultSet resultset = null;
		PreparedStatement preparedStatement = null;
		int[][] allLinks = null;
		try {
			//System.out.println("Getting Links");
			String selectAllLinksSql = "SELECT * FROM links where to_docid IN (SELECT DISTINCT docid from documents) AND from_docid IN (SELECT DISTINCT docid from documents)";
			preparedStatement = connection.prepareStatement(selectAllLinksSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultset = preparedStatement.executeQuery();
			resultset.last();
			allLinks = new int[resultset.getRow()][2];
			int index = 0;
			resultset.beforeFirst();
			while (resultset.next()) {
				allLinks[index][0] = resultset.getInt("from_docid");
				allLinks[index][1] = resultset.getInt("to_docid");
				index++;
			}
			preparedStatement.close();
			//System.out.println("Got Links");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allLinks;
	}

	private static Matrix generateTransitionMatrix(HashMap<Integer, Integer> documentIds, int[][] allLinks) throws ClassNotFoundException, SQLException {
		int totalDocuments = documentIds.size();
		Matrix transitionMatrix = DenseMatrix.zero(totalDocuments, totalDocuments);
		int number_of_links = allLinks.length;
		for (int index = 0; index < number_of_links; index++) {
			int from_docId = allLinks[index][0];
			int to_docId = allLinks[index][1];
			Integer position_from = documentIds.get(from_docId);
			Integer position_to = documentIds.get(to_docId);
			if (position_from != null && position_to != null) {
				transitionMatrix.set(position_from, position_to, transitionMatrix.get(position_from, position_to) + 1);
			}
		}

		for (int index = 0; index < totalDocuments; index++) {
			Vector indexRow = transitionMatrix.getRow(index);
			double sumRow = indexRow.sum();
			if (sumRow > 0) {
				indexRow = indexRow.divide(sumRow);
				indexRow = indexRow.multiply(0.9);
				indexRow = indexRow.add(0.1 / totalDocuments);
			} else {
				indexRow = indexRow.add(1.0 / totalDocuments);
			}
			transitionMatrix.setRow(index, indexRow);
		}
		return transitionMatrix;
	}

	private static void updatePageRankDB(HashMap<Integer, Integer> documentIds, Vector rank) throws ClassNotFoundException, SQLException {

		PreparedStatement preparedStatement1 = null;
		PreparedStatement preparedStatement2 = null;
		connection = databaseConnection.getConnection();
		try {
			System.out.println("Updating PageRank into the database");
			String updateDocumentsQuery="UPDATE documents set page_rank = ? WHERE docid = ?;";
			String updateFeaturesQuery="UPDATE features set page_rank = ? WHERE docid = ?;";
			preparedStatement1 = connection.prepareStatement(updateDocumentsQuery);
			preparedStatement2 = connection.prepareStatement(updateFeaturesQuery);
			for (Map.Entry<Integer, Integer> matrixPos : documentIds.entrySet()) {
				preparedStatement1.setDouble(1, rank.get(matrixPos.getValue()));
				preparedStatement1.setInt(2, matrixPos.getKey());

				preparedStatement2.setDouble(1, rank.get(matrixPos.getValue()));
				preparedStatement2.setInt(2, matrixPos.getKey());
				preparedStatement1.executeUpdate();
				preparedStatement2.executeUpdate();
			}


			preparedStatement1.close();
			preparedStatement2.close();
			System.out.println("Pagerank Updated");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
