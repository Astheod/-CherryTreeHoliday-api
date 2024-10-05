package dev.peerat.training.cherrytreeholiday.routes.holidays.invitations;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Locker;
import dev.peerat.framework.Locker.Key;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.training.cherrytreeholiday.models.Invitation;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public class InviteNotifier implements Response{

	private Locker<Invitation> invites;
	private Repository repo;
	
	public InviteNotifier(Repository repo, Locker<Invitation> locker){
		this.repo = repo;
		this.invites = locker;
	}
	
	
	@Route(path = "/holiday/invites", websocket=true, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		Key key = new Key();
		invites.init(key);
		try {
			while(!reader.isClosed()){
				invites.lock(key);
				Invitation invite = invites.getValue(key);
				if(invite.getUser().equals(context.<CherryUser>getUser().getPseudo())) writer.write(invite.toJson().toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		invites.remove(key);
	}
	

}
