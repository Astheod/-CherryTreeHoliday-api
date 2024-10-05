package dev.peerat.training.cherrytreeholiday.models;

import dev.peerat.framework.utils.json.JsonMap;

public class Address {
    private int id;
    private String country;
    private String city;
    private String code_postal;
    private String street;
    private String number;
    
    
	public Address(int id, String country, String city, String code_postal, String street, String number) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.code_postal = code_postal;
		this.street = street;
		this.number = number;
	}


	public Address(String country, String city, String code_postal, String street, String number) {
		this.country = country;
		this.city = city;
		this.code_postal = code_postal;
		this.street = street;
		this.number = number;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}

	public String getCodePostal() {
		return code_postal;
	}

	public void setCodePostal(String code_postal) {
		this.code_postal = code_postal;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public JsonMap toJson(){
		JsonMap json = new JsonMap();
		json.set("country", country);
		json.set("city", city);
		json.set("code_postal", code_postal);
		json.set("street", street);
		json.set("number", number);
		return json;
	}
}
