package dev.peerat.training.cherrytreeholiday.models;

import java.util.Date;

import dev.peerat.framework.utils.json.JsonMap;

public class Activity {
	
	private int id;
	private String name;
	private String description;
	private Date date_start;
	private Date date_end;
	private Address address;
	private int holidayId;

	public Activity(int id, String name, String description, Date date_start, Date date_end, Address address, int holidayId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.date_start = date_start;
		this.date_end = date_end;
		this.address = address;
	}
	
	public Activity(String name, String description, Date date_start, Date date_end, Address address, int holidayId) {
		this.name = name;
		this.description = description;
		this.date_start = date_start;
		this.date_end = date_end;
		this.address = address;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return date_start;
	}

	public void setStartDate(Date dateStart) {
		this.date_start = dateStart;
	}

	public Date getEndDate() {
		return date_end;
	}

	public void setEndDate(Date dateEnd) {
		this.date_end = dateEnd;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public int getHolidayId(){
		return this.holidayId;
	}

	public JsonMap toJson() {
		JsonMap json = new JsonMap();
		json.set("id", id);
		json.set("name", name);
		json.set("description", description);
		json.set("dateStart", date_start.toString());
		json.set("dateEnd", date_end.toString());
		json.set("address", address.toJson());
		return json;
	}
}
