package dev.peerat.training.cherrytreeholiday.routes.holidays;

import java.util.List;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonArray;
import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class ViewActivities implements Response {

	private Repository repo;
	
	public ViewActivities(Repository repo) {
		this.repo = repo;
	}

	@Route(path = "/holiday/(\\d+)/activities", needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		
		context.response(200);
		JsonArray activities = new JsonArray();
		List<Activity> received = repo.getActivities(holidayId);
		if(!received.isEmpty()) {
			for(Activity activity : received) {
				activities.add(activity.toJson());
				
			}
		}
		writer.write(activities.toString());
	}

}
