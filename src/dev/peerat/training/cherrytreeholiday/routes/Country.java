package dev.peerat.training.cherrytreeholiday.routes;

import java.util.Map.Entry;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class Country implements Response {
	
	private Repository repo;

	public Country(Repository repo) {
		this.repo = repo;
	}
	
	
	@Route(path = "/statistics/country/(\\d{4}-\\d{2}-\\d{2})/")
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		JsonMap json = new JsonMap();
		
		
		for(Entry<String, Integer> entry : repo.getUserHolidayByCountry(matcher.group(1)).entrySet()) json.set(entry.getKey(), entry.getValue());
		context.response(200);
		writer.write(json.toString());
	}
}