package io.github.xf8b.increaseanddecreasefield;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final FieldStorage fieldStorage = new FieldStorage();

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        //TODO: fix field += 2 not working
        Runtime.getRuntime().addShutdownHook(new Thread(Main::onShutdown));
        Scanner scanner = new Scanner(System.in);
        if (new File("fields.db").createNewFile()) System.out.println("Created field database file.");
        FieldsDatabaseHelper.read(fieldStorage);
        FieldsDatabaseHelper.write(fieldStorage);
        while (scanner.hasNext()) {
            String input = scanner.next();
            if (input.toLowerCase().equals("stop")) {
                System.exit(0);
                break;
            }
            if (scanner.hasNext("\\w+[-+]{2}") || scanner.hasNext("\\w+ [+-]= \\d+")) {
                String fieldName;
                long amount;
                try {
                    amount = Long.parseLong(input.substring(input.indexOf("=")));
                } catch (NumberFormatException exception) {
                    System.out.println("The amount must be a number!");
                    continue;
                }
                if (input.matches("\\w+--")) {
                    fieldName = input.substring(0, input.indexOf("-"));
                    fieldStorage.decreaseField(fieldName);
                    System.out.printf("%s == %d%n", fieldName, fieldStorage.getValueOfField(fieldName));
                } else if (input.matches("\\w+\\+\\+")) {
                    fieldName = input.substring(0, input.indexOf("+"));
                    fieldStorage.increaseField(fieldName);
                    System.out.printf("%s == %d%n", fieldName, fieldStorage.getValueOfField(fieldName));
                } else if (input.matches("\\w+ -= \\d+")) {
                    fieldName = input.substring(0, input.indexOf("-"));
                    fieldStorage.decreaseField(fieldName, amount);
                    System.out.printf("%s == %d%n", fieldName, fieldStorage.getValueOfField(fieldName));
                } else if (input.matches("\\w+ \\+= \\d+")) {
                    fieldName = input.substring(0, input.indexOf("+"));
                    fieldStorage.increaseField(fieldName, amount);
                    System.out.printf("%s == %d%n", fieldName, fieldStorage.getValueOfField(fieldName));
                }
            } else {
                System.out.println("Please input a valid field.");
            }
        }
    }

    private static void onShutdown() {
        try {
            FieldsDatabaseHelper.read(fieldStorage);
            FieldsDatabaseHelper.write(fieldStorage);
            System.out.println("Successfully saved fields!");
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            System.out.println("Fields may have failed to save!");
        }
    }
}
