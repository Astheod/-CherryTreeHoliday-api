package dev.peerat.training.cherrytreeholiday.routes.holidays.invitations;

import java.util.List;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonArray;
import dev.peerat.training.cherrytreeholiday.models.Invitation;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class InviteList implements Response{
	
	private Repository repo;
	
	public InviteList(Repository repo){
		this.repo = repo;
	}
	
	@Route(path = "/holiday/invite/", needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		
		List<Invitation> invites = repo.getInvitations(context.getUser());
		
		context.response(200);
		JsonArray array = new JsonArray();
		for(Invitation invite : invites) array.add(invite.toJson());
		writer.write(array.toString());
	}

}
