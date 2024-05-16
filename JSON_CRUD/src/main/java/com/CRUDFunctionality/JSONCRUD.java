package com.CRUDFunctionality;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.repo.JDBCConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class JSONCRUD {
    JDBCConnection jdbcConnection = new JDBCConnection();
    Connection connection = jdbcConnection.establishConnection();
    Scanner ip = new Scanner(System.in);

    public void createJSONData() {

        String insertQuery = "INSERT INTO students (student_data) VALUES (?)";
        System.out.println("enter the JSON data");
        String JSONData = ip.nextLine();
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

            e.printStackTrace();
        }
    }

    public void readJSONData() {

        String selectQuery = "SELECT student_data FROM students WHERE student_id=?";
        int choice;
        JSONObject jsonObject = null;

        try {
            if (connection != null) {
                System.out.println("Enter the Student ID Whose Details are to be Retrieved");
                int studentID = ip.nextInt();
                PreparedStatement selectJSONStatement = connection.prepareStatement(selectQuery);
                selectJSONStatement.setInt(1, studentID);
                ResultSet rs = selectJSONStatement.executeQuery();
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data Found for the Student ID " + studentID + "Enter a Valid Student ID.");
                } else {
                    while (rs.next()) {
                        String studentData = rs.getString("student_data");
                        jsonObject = covertStringToJSON(studentData);

                    }
                    System.out.println(
                            "Do You Want To Get Any Data Based on the Key \nIf it is a Concrete Value Enter 1 \nIf is is a Array Press 2 \nIf it  is a JSON Object Press 3 \nIf you Want the Whole Data Press Any Key ");
                    choice = ip.nextInt();
                    switch (choice) {
                        case 1:

                            System.out.println("Enter the key for the concrete value:");
                            String concreteKey = ip.next();
                            try {
                                if (jsonObject != null && jsonObject.has(concreteKey))
                                    System.out.println("Value: " + jsonObject.getString(concreteKey));
                                else
                                    System.out.println("Enter a valid Key");
                            } catch (JSONException e) {
                                System.out.println("The value Corresponding to Key '" + concreteKey
                                        + "' is Not a Concrete JSON String Value.");
                            }
                            break;
                        case 2:
                            System.out.println("Enter the Key for the Array:");
                            String arrayKey = ip.next();
                            try {
                                if (jsonObject != null && jsonObject.has(arrayKey))
                                    System.out.println("JSON Array: " + jsonObject.getJSONArray(arrayKey));
                                else
                                    System.out.println("Enter a Valid Key");
                            } catch (JSONException e) {
                                System.out
                                        .println("The Value Corresponding to Key '" + arrayKey + "' is Not a JSON Array.");
                            }
                            break;
                        case 3:

                            System.out.println("Enter the Key for the JSON Object:");
                            String objectKey = ip.next();
                            try {
                                if (jsonObject != null && jsonObject.has(objectKey)) {
                                    System.out.println("JSON Nested Object: " + jsonObject.getJSONObject(objectKey));
                                } else {
                                    System.out.println(" Enter a Valid Key");
                                }
                            } catch (JSONException e) {
                                System.out.println(
                                        "The Value Corresponding To Key '" + objectKey + "' Is Not a JSON Object.");
                            }

                            break;
                        default:
                            System.out.println("Whole JSON: " + jsonObject);
                            break;
                    }

                }
            } else {
                System.out.println("Connection Failed With the DataBase");
            }
        } catch (InputMismatchException e) {
            System.out.println("Enter a valid Number of type Integer");
            e.printStackTrace();
        } catch (SQLException e) {

            e.printStackTrace();
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
        String fetchQuery = "SELECT student_data FROM students WHERE student_id=?";
        JSONObject jsonObject = null;
        int choise;

        try {
            if (connection != null) {
                System.out.println("Enter the Student ID Whose Details  are to be Modified");
                int studentID = ip.nextInt();
                PreparedStatement selectJSONStatement = connection.prepareStatement(fetchQuery);
                selectJSONStatement.setInt(1, studentID);
                ResultSet rs = selectJSONStatement.executeQuery();
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data Found for the Student ID " + studentID + "Enter a Valid Student ID.");
                } else {
                    while (rs.next()) {
                        String studentData = rs.getString("student_data");
                        jsonObject = covertStringToJSON(studentData);

                    }
                    System.out.println("To detele a entire key pair value press 1\n To update a data press 2(if exist the values gets updated else a new pair value will be created)");
                    choise = ip.nextInt();
                    switch (choise) {
                        case 1: {
                            System.out.println("Enter the key value to be deleted");
                            String deleteKeyValue = ip.next();
                            if (jsonObject != null && jsonObject.has(deleteKeyValue)) {
                                jsonObject.remove(deleteKeyValue);
                                String updatedStudentData = jsonObject.toString();
                                String updateQuery = "UPDATE students SET student_data = ? WHERE student_id = ?";
                                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);


                                updateStatement.setString(1, updatedStudentData);
                                updateStatement.setInt(2, studentID);


                                int rowsAffected = updateStatement.executeUpdate();


                                if (rowsAffected == 1) {
                                    System.out.println("Student data updated successfully.");
                                } else {
                                    System.out.println("Failed to update student data.");
                                }
                            } else {
                                System.out.println("Enter a Valid Key");
                            }


                        }
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    JSONObject covertStringToJSON(String JSONString) {
        return new JSONObject(JSONString);
    }
}
