package dev.peerat.training.cherrytreeholiday.routes.holidays;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Locker;
import dev.peerat.framework.Locker.Key;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.ChatNotifier;
import dev.peerat.training.cherrytreeholiday.models.ChatStatus;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.models.Message;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class Chat implements Response{
	
	private Repository repo;
	private Locker<ChatNotifier> syncMessage;
	private Map<String, Integer> onlines;
	
	public Chat(Repository repo){
		this.repo = repo;
		this.syncMessage = new Locker<>();
		this.onlines = new HashMap<>();
	}

	@Route(path = "/holiday/(\\d+)/chat", websocket = true, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		CherryUser user = context.getUser();
		
		List<CherryUser> users = repo.getHolidayUsers(holidayId);
		synchronized (onlines){
			for(CherryUser participant : users){
				if( participant.getPseudo().equals(user.getPseudo()) || onlines.containsKey(participant.getPseudo())) writer.write(new ChatStatus(participant, holidayId, true).toJson().toString());
			}
			
		}
		List<Message> messages = repo.loadChat(holidayId);
		for(Message message : messages) writer.write(message.toJson().toString());
		
		new Thread(() -> {
			try {
				while(!reader.isClosed()){
					JsonMap json = reader.readJson();
					Message message = new Message(json.get("content"), LocalDate.now().toEpochDay(), user, holidayId);
					if(repo.sendMessage(message)) syncMessage.setValue(message);
				}
			}catch(Exception e){
				Integer alive;
				synchronized(onlines){
					alive = onlines.get(user.getPseudo());
					if(alive < 2){
						onlines.remove(user.getPseudo());
					}else onlines.put(user.getPseudo(), alive-1);
				}
				if(alive == 1) syncMessage.setValue(new ChatStatus(user, holidayId, false));
			}
		}).start();
		
		Integer alive;
		synchronized (onlines){
			alive = onlines.get(user.getPseudo());
			if(alive == null){
				onlines.put(user.getPseudo(), 1);
			}else onlines.put(user.getPseudo(), alive+1);
		}
		if(alive == null) syncMessage.setValue(new ChatStatus(user, holidayId, true));
		
		Key key = new Key();
		syncMessage.init(key);
		try {
			while(!reader.isClosed()){
				syncMessage.lock(key);
				ChatNotifier notif = syncMessage.getValue(key);
				if(notif.getHolidayId() != holidayId) continue;
				writer.write(notif.toJson().toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		syncMessage.remove(key);
	}
	
}
