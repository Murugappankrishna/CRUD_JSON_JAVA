package com.main;

import java.util.Scanner;

import com.CRUDFunctionality.JSONCRUD;

public class App {

	public static void main(String[] args)

	{
		int choise;
		Scanner ip = new Scanner(System.in);
		JSONCRUD jsonCrud = new JSONCRUD();
		while (true) {
			System.out.println(
					" 1 to create a JSON Data\n 2 to read JSON Data\n 3 to delete JSON Data\n 4 to update JSON Data\n 5 To Exit");
			choise = ip.nextInt();
			switch (choise) {
				case 1:
					jsonCrud.createJSONData();
					break;
				case 2:
					jsonCrud.readJSONData();
					break;
				case 3:
					jsonCrud.deleteJSONData();
					break;
				case 4:
					jsonCrud.updateJSONData();
					break;
				case 5:
					jsonCrud.exit();
				default:
					ip.close();
			}

		}
	}
}