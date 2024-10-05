package dev.peerat.training.cherrytreeholiday.models;

import dev.peerat.framework.utils.json.JsonMap;

public class Message extends ChatNotifier{

	private String content;
	private long sent;
	
	public Message(String content, long sent, CherryUser author, int holidayId){
		super(author, holidayId);
		this.content = content;
		this.sent = sent;
	}

	public String getContent(){
		return content;
	}

	public long getSent(){
		return sent;
	}
	
	public JsonMap toJson(){
		JsonMap json = super.toJson();
		json.set("content", content);
		json.set("origin", sent);
		return json;
	}
}
