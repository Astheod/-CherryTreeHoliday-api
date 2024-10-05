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
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public class ViewHolidayUsers implements Response{
	
	private Repository repo;
	
	public ViewHolidayUsers(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/holiday/(\\d+)/users", needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));

		List<CherryUser> users = repo.getHolidayUsers(holidayId);
		context.response(200);
		JsonArray array = new JsonArray();
		boolean admin = true;
		for(CherryUser user : users){
			JsonMap map = new JsonMap();
			if(admin){
				map.set("owner", admin);
				admin = false;
			}
			map.set("username", user.getPseudo());
			array.add(map);
		}
		writer.write(array.toString());
	}


}
