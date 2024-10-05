package dev.peerat.training.cherrytreeholiday.routes.holidays.invitations;

import static dev.peerat.framework.RequestType.POST;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Locker;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.Invitation;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public class InviteHoliday implements Response{
	
	private Repository repo;
	private Locker<Invitation> invites;
	
	public InviteHoliday(Repository repo, Locker<Invitation> locker){
		this.repo = repo;
		this.invites = locker;
	}

	@Route(path = "/holiday/(\\d+)/users",type = POST, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		if(!repo.isHolidayOwner(context.getUser(), holidayId)){
			context.response(401);
			return;
		}
		JsonMap json = reader.readJson();
		if(repo.inviteHoliday(json.get("pseudo"), holidayId)){
			context.response(200);
			invites.setValue(new Invitation(holidayId, repo.getHoliday(holidayId).getName(), json.get("pseudo")));
		}else{
			context.response(403);
		}
	}

}
