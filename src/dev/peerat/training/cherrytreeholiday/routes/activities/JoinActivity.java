package dev.peerat.training.cherrytreeholiday.routes.activities;

import static dev.peerat.framework.RequestType.PUT;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class JoinActivity implements Response {

	private Repository repo;
	
	public JoinActivity(Repository repo) {
		this.repo = repo;
	}
	
	@Route(path = "/activity/(\\d+)/join",type = PUT, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int activityId = Integer.parseInt(matcher.group(1));
		CherryUser user = context.getUser();
		if(repo.joinActivity(user, activityId)) {
			context.response(200);
		}else{
			context.response(403);
		}
	}
}
