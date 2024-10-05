package dev.peerat.training.cherrytreeholiday.models;

import dev.peerat.framework.utils.json.JsonMap;

public class ChatNotifier{
	
	private CherryUser author;
	private int holidayId;
	
	public ChatNotifier(CherryUser author, int holidayId){
		this.author = author;
		this.holidayId = holidayId;
	}
	
	public CherryUser getAuthor(){
		return this.author;
	}
	
	public int getHolidayId(){
		return this.holidayId;
	}
	
	public JsonMap toJson(){
		JsonMap json = new JsonMap();
		json.set("author", getAuthor().getPseudo());
		return json;
	}
}