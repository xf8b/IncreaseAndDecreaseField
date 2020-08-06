package io.github.xf8b.increaseanddecreasefield;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldStorage {
    private final Map<String, Long> fields = new ConcurrentHashMap<>();

    private void checkIfFieldExists(String fieldName) {
        fields.putIfAbsent(fieldName, 0L);
    }

    private void save() {
        try {
            FieldsDatabaseHelper.write(this);
            FieldsDatabaseHelper.read(this);
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void setValueOfField(String fieldName, long newValue, boolean isFromDatabase) {
        checkIfFieldExists(fieldName);
        fields.replace(fieldName, newValue);
        if (!isFromDatabase) save();
    }

    public void increaseField(String fieldName) {
        checkIfFieldExists(fieldName);
        fields.replace(fieldName, fields.get(fieldName) + 1);
        save();
    }

    public void increaseField(String fieldName, long amount) {
        checkIfFieldExists(fieldName);
        fields.replace(fieldName, fields.get(fieldName) + amount);
        save();
    }

    public void decreaseField(String fieldName) {
        checkIfFieldExists(fieldName);
        fields.replace(fieldName, fields.get(fieldName) - 1);
        save();
    }

    public void decreaseField(String fieldName, long amount) {
        checkIfFieldExists(fieldName);
        fields.replace(fieldName, fields.get(fieldName) - amount);
        save();
    }

    public long getValueOfField(String fieldName) {
        return fields.get(fieldName);
    }

    public Map<String, Long> getFields() {
        return fields;
    }
}
