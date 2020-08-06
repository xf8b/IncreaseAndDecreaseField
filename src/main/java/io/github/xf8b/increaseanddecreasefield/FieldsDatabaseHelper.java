package io.github.xf8b.increaseanddecreasefield;

import java.sql.*;
import java.util.Map;

public class FieldsDatabaseHelper {
    public static void read(FieldStorage fieldStorage) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:fields.db");
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS fields (name, value);");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM fields;");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            fieldStorage.setValueOfField(resultSet.getString("name"), resultSet.getLong("value"), true);
        }

        connection.close();
        statement.close();
        resultSet.close();
        preparedStatement.close();
    }

    public static void write(FieldStorage fieldStorage) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:fields.db");
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS fields (name, value);");
        PreparedStatement preparedStatementForCheckingIfFieldExists = connection.prepareStatement("select name from fields;");

        ResultSet resultSet = preparedStatementForCheckingIfFieldExists.executeQuery();

        boolean doesExist = resultSet.next();

        PreparedStatement preparedStatement;

        if (!doesExist) {
            preparedStatement = connection.prepareStatement("INSERT INTO fields VALUES (?, ?);");
            for (Map.Entry<String, Long> set : fieldStorage.getFields().entrySet()) {
                preparedStatement.clearParameters();
                preparedStatement.setString(1, set.getKey());
                preparedStatement.setLong(2, set.getValue());
                preparedStatement.addBatch();
                connection.setAutoCommit(false);
                preparedStatement.executeBatch();
                connection.setAutoCommit(true);
            }
        } else {
            preparedStatement = connection.prepareStatement("update fields set value = ? where name = ?;");
            for (Map.Entry<String, Long> set : fieldStorage.getFields().entrySet()) {
                preparedStatement.setLong(1, set.getValue());
                preparedStatement.setString(2, set.getKey());
                preparedStatement.addBatch();

                connection.setAutoCommit(false);
                preparedStatement.executeUpdate();
                connection.setAutoCommit(true);
            }
        }

        connection.close();
        statement.close();
        resultSet.close();
        preparedStatement.close();
        preparedStatementForCheckingIfFieldExists.close();
    }
}
