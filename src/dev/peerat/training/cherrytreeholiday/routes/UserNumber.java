package dev.peerat.training.cherrytreeholiday.routes;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class UserNumber implements Response {

	private Repository repo;
	
	public UserNumber(Repository repo){
		this.repo = repo;
	}
	
	@Route(path = "/statistics/users")
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		JsonMap json = new JsonMap();
		json.set("number", repo.getNumberOfUser());
		
		context.response(200);
		writer.write(json.toString());
	}
}