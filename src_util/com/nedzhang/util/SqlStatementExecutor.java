package com.nedzhang.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public abstract class SqlStatementExecutor {

	private static final int VALUE_FIELD_TRUNCATE_SIZE = 4000;

	public List<List<Pair<String, Object>>> executeSql(final String driver,
			final String url, final String username,
			final String password, final String sqlStatement)
			throws SQLException, ClassNotFoundException {

		final Connection connection = getConnection(driver, url,
				username, password);

		try {
			final PreparedStatement stm = connection
					.prepareStatement(sqlStatement);

			final ResultSet resultSet = stm.executeQuery();

			final List<List<Pair<String, Object>>> resultObjects = new ArrayList<List<Pair<String, Object>>>();

			while (resultSet.next()) {

				final List<Pair<String, Object>> blockSession = parseResultRow(resultSet);

				resultObjects.add(blockSession);
			}

			resultSet.close();

			stm.close();

			return resultObjects;

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private void printElement(final BufferedWriter outputStream,
			final String value, final boolean prependSeparator)
			throws IOException {

		if (prependSeparator) {
			outputStream.write(',');
		}

		outputStream.write('"');

		if (value == null) {
			outputStream.write("NULL");
		} else if (value.length() > VALUE_FIELD_TRUNCATE_SIZE - 1000
				|| value.contains("\"")) {
			// Build the print out string
			// StringBuilder printoutBuilder = new
			// StringBuilder(Math.min(value.length()+32,
			// VALUE_FIELD_TRUNCATE_SIZE));
			int outputPos = 0;
			for (int inputPos = 0; inputPos < value.length()
					&& outputPos < VALUE_FIELD_TRUNCATE_SIZE - 3; inputPos++) {

				char inputChar = value.charAt(inputPos);

				if (inputChar == '"') {
					outputStream.write("\"\"");
					outputPos += 2;
				} else {
					outputStream.write(inputChar);
					outputPos++;
				}

				if (outputPos >= VALUE_FIELD_TRUNCATE_SIZE - 4) {
					outputStream.write("...");
				}
			}

		} else {
			outputStream.write(value);
		}

		outputStream.write('"');
	}

	public void printResult(List<List<Pair<String, Object>>> result,
			OutputStream streamToWriteTo, String encoding) throws IOException {

		final OutputStreamWriter writer = new OutputStreamWriter(
				streamToWriteTo, encoding);

		try {
			final BufferedWriter bufferedWriter = new BufferedWriter(writer);

			if (result != null && result.size() > 0) {
				// Write header
				List<Pair<String, Object>> firstRow = result.get(0);
				for (int i = 0; i < firstRow.size(); i++) {
					printElement(bufferedWriter, firstRow.get(i).getValue1(),
							i > 0);
				}
				bufferedWriter.write('\n');

				for (List<Pair<String, Object>> row : result) {
					for (int i = 0; i < row.size(); i++) {
						printElement(bufferedWriter, fieldToString(row, i),
								i > 0);
					}
					bufferedWriter.write('\n');
				}
			} else {
				bufferedWriter.write("*** Empty resultset ***");
			}
			
			bufferedWriter.close();
		} finally {
			try {
				writer.close();
			} catch (Throwable e) {
				// We are in a finally block. Don't throw another exception
				e.printStackTrace();
			}
		}

	}

	protected String fieldToString(List<Pair<String, Object>> row, int columnIndex) {

		Object valueObject = row.get(columnIndex).getValue2();
		
		return  valueObject == null ? "NULL" : valueObject.toString();
		
	}

	private Connection getConnection(final String driver, final String url,
			final String username, final String password) throws SQLException,
			ClassNotFoundException {

		Class.forName(driver); // "oracle.jdbc.driver.OracleDriver");

		final Connection connection = DriverManager.getConnection(url,
				username, password);

		return connection;

	}

	public abstract List<Pair<String, Object>> parseResultRow(ResultSet resultSet) throws SQLException;
}
