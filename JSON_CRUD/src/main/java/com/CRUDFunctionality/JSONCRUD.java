package com.CRUDFunctionality;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.repo.JDBCConnection;

public class JSONCRUD {
    JDBCConnection jdbcConnection = new JDBCConnection();
    Connection connection = jdbcConnection.establishConnection();
    Scanner ip = new Scanner(System.in);

    public void createJSONData() {

        String insertQuery = "INSERT INTO students (student_data) VALUES (?)";
        System.out.println("enter the JSON data");
        String JSONData = ip.nextLine();
        if (JSONData != null) {
            try {
                if (connection != null) {
                    PreparedStatement insertJSONStatement = connection.prepareStatement(insertQuery);
                    insertJSONStatement.setString(1, JSONData);
                    int result = insertJSONStatement.executeUpdate();
                    if (result == 1) {
                        System.out.println("Data Inserted SuccessfullyY!");
                    }

                } else {
                    System.out.println("Connection Failed With the DataBase");
                }
            } catch (MysqlDataTruncation e) {
                System.out.println("Check the data Format of the Input JSON Data ");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Exception related to SQL");
                e.printStackTrace();
            } catch (JSONException e) {
                System.out.println("Exception related to JSON");
                e.printStackTrace();
            }
        }
    }

    public void readJSONData() {

        System.out.println("Enter the Student ID Whose Details are to be Retrieved");
        int studentID = ip.nextInt();
        JSONObject jsonObject = fetchJSONData(studentID);
        if (jsonObject != null) {
            try {

                int choice;
                System.out
                        .println("if you want the whole JSON data press 1 /n if you want a specific key value press 2");
                choice = ip.nextInt();
                if (choice == 1) {

                    System.out.println(jsonObject);
                } else if (choice == 2) {
                    System.out.println("entere the desired key");
                    String Key = ip.next();
                    parseJSONObject(jsonObject, Key);

                } else {
                    System.out.println("enter either 1 or 2");
                }

            } catch (InputMismatchException e) {
                System.out.println("enter a valid studentID of type integer");
            }
        }
    }

    public void deleteJSONData() {
        String deleteQuery = "DELETE FROM students WHERE  student_id=?";
        System.out.println("Enter the Student ID Whose Details are to be Deleted");

        try {
            int studentID = ip.nextInt();
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, studentID);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Data Deleted Successfully ");
            } else {
                System.out.println("Enter a Valid Student ID, The student ID " + studentID + " student Exists");
            }
        } catch (InputMismatchException e) {
            System.out.println("Enter a Valid Number of Type Integer");
            e.printStackTrace();
        } catch (SQLException e) {

            e.printStackTrace();
        }

    }

    public void updateJSONData() {
        int choice;
        System.out.println("Enter the student ID to be modified");
        int StudentID = ip.nextInt();
        String modifyKeyValue = null;
        JSONObject jsonData = fetchJSONData(StudentID);
        if (jsonData != null) {

            System.out.println(
                    "To delete a  key pair value press 1/n To update a data press 2(if exist the values gets updated else a new pair value will be created)");

            choice = ip.nextInt();
            switch (choice) {
                case 1: {
                    System.out.println("Enter the key value to be deleted");
                    modifyKeyValue = ip.next();
                    JSONObject updatedJSON = deleteKeyPairValue(jsonData, modifyKeyValue);
                    updateQuery(updatedJSON, StudentID);
                    break;

                }
                case 2: {
                    System.out.println("Enter the key value to be deleted");
                    modifyKeyValue = ip.next();
                    System.out.println("Enter the new value to be updated");
                    String newValue = ip.next();
                    JSONObject updatedJSON = updateValue(jsonData, modifyKeyValue, newValue);

                    updateQuery(updatedJSON, StudentID);
                    break;
                }
            }

        }
    }

    JSONObject covertStringToJSON(String JSONString) {

        return new JSONObject(JSONString);

    }

    void parseJSONObject(JSONObject json, String key) {
        boolean exists = json.has(key);
        if (exists) {
            System.out.println(json.get(key));
        } else {
            Iterator<?> keys = json.keys();
            while (keys.hasNext()) {
                String nextKey = (String) keys.next();
                try {
                    if (json.get(nextKey) instanceof JSONObject) {
                        parseJSONObject(json.getJSONObject(nextKey), key);

                    } else if (json.get(nextKey) instanceof JSONArray) {
                        JSONArray jsonArray = json.getJSONArray(nextKey);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.get(i) instanceof JSONObject) {
                                parseJSONObject(jsonArray.getJSONObject(i), key);

                            } else {
                                System.out.println(jsonArray.get(i));
                            }
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    JSONObject deleteKeyPairValue(JSONObject json, String key) {
        boolean exists = json.has(key);
        if (exists) {
            json.remove(key);
        } else {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String nextKey = (String) keys.next();
                try {
                    Object value = json.get(nextKey);
                    if (value instanceof JSONObject) {
                        deleteKeyPairValue((JSONObject) value, key);
                    } else if (value instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) value;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Object arrayItem = jsonArray.get(i);
                            if (arrayItem instanceof JSONObject) {
                                deleteKeyPairValue((JSONObject) arrayItem, key);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return json;
    }

    JSONObject fetchJSONData(int studentID) {
        String fetchQuery = "SELECT student_data FROM students WHERE student_id=?";
        JSONObject jsonObject = null;
        if (connection != null) {

            PreparedStatement selectJSONStatement;
            try {
                selectJSONStatement = connection.prepareStatement(fetchQuery);
                selectJSONStatement.setInt(1, studentID);
                ResultSet rs = selectJSONStatement.executeQuery();
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data Found for the Student ID " + studentID + "Enter a Valid Student ID.");
                    return null;
                } else {
                    while (rs.next()) {
                        String studentData = rs.getString("student_data");
                        jsonObject = covertStringToJSON(studentData);

                    }
                }
            } catch (SQLException e) {

                e.printStackTrace();
            }

        }
        return jsonObject;
    }

    JSONObject updateValue(JSONObject json, String key, String newValue) {
        boolean exists = json.has(key);
        if (exists) {
            json.put(key, newValue);
        } else {
            Iterator<?> keys = json.keys();
            while (keys.hasNext()) {
                String nextKey = (String) keys.next();
                try {
                    if (json.get(nextKey) instanceof JSONObject) {
                        updateValue(json.getJSONObject(nextKey), key, newValue);
                    } else if (json.get(nextKey) instanceof JSONArray) {
                        JSONArray jsonArray = json.getJSONArray(nextKey);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.get(i) instanceof JSONObject) {
                                updateValue(jsonArray.getJSONObject(i), key, newValue);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (!exists) {
                json.put(key, newValue);
            }
        }
        return json;
    }

    void updateQuery(JSONObject updatedJSON, int id) {
        String updatedStudentData = updatedJSON.toString();
        String updateQuery = "UPDATE students SET student_data = ? WHERE student_id = ?";
        PreparedStatement updateStatement;
        try {
            updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, updatedStudentData);
            updateStatement.setInt(2, id);

            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Student data updated successfully.");
            } else {
                System.out.println("Failed to update student data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void exit() {
        try {
            connection.close();
            ip.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}