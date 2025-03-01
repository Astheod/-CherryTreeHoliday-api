package dev.peerat.training.cherrytreeholiday.routes.activities;

import static dev.peerat.framework.RequestType.POST;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.models.Address;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.routes.FormResponse;

public class CreateActivity extends FormResponse{
	
	private Repository repo;
	private DateFormat dateFormatter;
	
	public CreateActivity(Repository repo){
		this.repo = repo;
		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		require((json) -> ((JsonMap)json).has("name"));
		require((json) -> ((JsonMap)json).has("description"));
		require((json) -> ((JsonMap)json).has("date_start"));
		require((json) -> ((JsonMap)json).has("date_end"));
		require((json) -> ((JsonMap)json).has("address"));
		require((json) -> ((JsonMap)json).has("holidayId"));
		require((json) -> ((JsonMap)json).<JsonMap>get("address").has("country"));
		require((json) -> ((JsonMap)json).<JsonMap>get("address").has("city"));
		require((json) -> ((JsonMap)json).<JsonMap>get("address").has("code_postal"));
		require((json) -> ((JsonMap)json).<JsonMap>get("address").has("street"));
		require((json) -> ((JsonMap)json).<JsonMap>get("address").has("number"));
		
		hasLength((j)->j, (json) -> ((JsonMap)json).get("name"), 1, 100);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("description"), 0, 500);
		hasLength((j)->j, (json) -> ((JsonMap)json).<JsonMap>get("address").get("country"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).<JsonMap>get("address").get("city"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).<JsonMap>get("address").get("code_postal"), 1, 10);
		hasLength((j)->j, (json) -> ((JsonMap)json).<JsonMap>get("address").get("street"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).<JsonMap>get("address").get("number"), 1, 10);
	}

	@Route(path = "/activity/",type = POST, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		JsonMap json = readJson(reader);
		
		if(!isValid(() -> context.response(400))) return;
		
		Object id = json.get("holidayId");
		int holidayId = (id instanceof String) ? Integer.parseInt((String)id) : ((Long)id).intValue();

		JsonMap addr = json.get("address");
		Activity activity = new Activity(
				json.get("name"),
				json.get("description"),
				dateFormatter.parse(json.get("date_start")),
				dateFormatter.parse(json.get("date_end")),
				new Address(
						addr.get("country"),
						addr.get("city"),
						addr.get("code_postal"),
						addr.get("street"),
						addr.get("number")),
				holidayId);
		if(repo.createActivity(context.getUser(), holidayId, activity)){
			context.response(200);
		}else{
			context.response(401);
		}
	}

}
