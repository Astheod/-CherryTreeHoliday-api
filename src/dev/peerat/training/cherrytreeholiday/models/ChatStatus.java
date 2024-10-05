package dev.peerat.training.cherrytreeholiday.models;

import dev.peerat.framework.utils.json.JsonMap;

public class ChatStatus extends ChatNotifier{

	private boolean online;
	
	public ChatStatus(CherryUser author, int holidayId, boolean online){
		super(author, holidayId);
		this.online = online;
	}
	
	public boolean isOnline(){
		return this.online;
	}
	
	@Override
	public JsonMap toJson(){
		JsonMap json = super.toJson();
		json.set("status", online);
		return json;
	}

}
