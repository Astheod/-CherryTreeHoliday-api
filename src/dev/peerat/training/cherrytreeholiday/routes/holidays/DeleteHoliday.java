package dev.peerat.training.cherrytreeholiday.routes.holidays;

import static dev.peerat.framework.RequestType.*;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class DeleteHoliday implements Response{
	
	private Repository repo;
	
	public DeleteHoliday(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/holiday/(\\d+)/",type = DELETE, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		if(!repo.isHolidayOwner(context.getUser(), holidayId)){
			context.response(401);
			return;
		}
		if(repo.deleteHoliday(holidayId)){
			context.response(200);
		}else{
			context.response(403);
		}
	}

}
