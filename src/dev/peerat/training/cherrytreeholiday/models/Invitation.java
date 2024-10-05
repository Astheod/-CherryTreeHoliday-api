package dev.peerat.training.cherrytreeholiday.models;

import dev.peerat.framework.utils.json.JsonMap;

public class Invitation{
	
	private int id;
	private String name;
	
	private String user;
	
	public Invitation(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public Invitation(int id, String name, String user){
		this(id, name);
		this.user = user;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}

	public String getUser(){
		return this.user;
	}
	
	public JsonMap toJson(){
		JsonMap map = new JsonMap();
		map.set("id", id);
		map.set("name", name);
		return map;
	}
}
