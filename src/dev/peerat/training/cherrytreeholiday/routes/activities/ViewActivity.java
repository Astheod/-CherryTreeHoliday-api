package dev.peerat.training.cherrytreeholiday.routes.activities;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class ViewActivity implements Response {

	Repository repo;
	
	public ViewActivity(Repository repo) {
		this.repo = repo;
	}

	@Route(path = "/activity/(\\d+)/", needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int activityId = Integer.parseInt(matcher.group(1));
		
		Activity holiday = repo.getActivity(activityId);
		context.response(200);
		JsonMap json = holiday.toJson();
		if(repo.isActivityOwner(context.getUser(), activityId)) json.set("owner", true);
		writer.write(json.toString());
		return;
	}

}
