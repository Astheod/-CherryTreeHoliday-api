package dev.peerat.training.cherrytreeholiday.routes.holidays;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class Export implements Response{
	
	private Repository repo;
	
	public Export(Repository repo){
		this.repo = repo;
	}
	
	@Route(path = "/holiday/(\\d+)/schedule")
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		
		Calendar calendar = new Calendar();
		calendar.add(new ProdId("-//CherryTreeHoliday//iCal4j 1.0//FR"));
		calendar.add(ImmutableVersion.VERSION_2_0);
		calendar.add(ImmutableCalScale.GREGORIAN);
		
		for(Activity activity : repo.getActivities(holidayId)) calendar.add(new VEvent(activity.getStartDate().toInstant(), activity.getEndDate().toInstant(), activity.getName()));
	
		context.response(200, "Content-Type: application/octet-stream");
		writer.write(calendar.toString());
	}

}
