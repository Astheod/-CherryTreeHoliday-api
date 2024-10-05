package dev.peerat.training.cherrytreeholiday.routes.holidays;

import java.util.List;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonArray;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.Holiday;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class ViewHoliday implements Response {

	private Repository repo;

	public ViewHoliday(Repository repo) {
		this.repo = repo;
	}
	
	@Route(path = "/holiday/((\\d+)/?)?", needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		String param = matcher.group(2);
		if(param != null){
			int holidayId = Integer.parseInt(param);
			Holiday holiday = repo.getHoliday(holidayId);
			context.response(200);
			JsonMap json = holiday.toJson();
			if(repo.isHolidayOwner(context.getUser(), holidayId)) json.set("owner", true);
			writer.write(json.toString());
			return;
		}
		
		context.response(200);
		JsonArray holidays = new JsonArray();
		List<Holiday> received = repo.getHolidays(context.getUser());
		if(!received.isEmpty()) {
			for(Holiday holiday : received) {
				holidays.add(holiday.toJson());
			}
		}
		writer.write(holidays.toString());
	}
}
