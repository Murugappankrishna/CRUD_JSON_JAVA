package com.main;

import CRUDFunctionality.JSONCRUD;

public class App {

	public static void main(String[] args)

	{
		JSONCRUD jsonCrud = new JSONCRUD();
		jsonCrud.createJSONData();
		jsonCrud.readJSONData();
		jsonCrud.deteteJSONData();
	}
}
