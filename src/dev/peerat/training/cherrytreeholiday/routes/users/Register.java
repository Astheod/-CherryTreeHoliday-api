package dev.peerat.training.cherrytreeholiday.routes.users;

import static dev.peerat.framework.RequestType.POST;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.Router;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.routes.FormResponse;

public class Register extends FormResponse{
	
	private Router<CherryUser> router;
	private Repository repo;
	
	public Register(Router<CherryUser> router, Repository repo){
		this.router = router;
		this.repo = repo;
		
		require((json) -> ((JsonMap)json).has("pseudo"));
		require((json) -> ((JsonMap)json).has("email"));
		require((json) -> ((JsonMap)json).has("firstname"));
		require((json) -> ((JsonMap)json).has("lastname"));
		require((json) -> ((JsonMap)json).has("password"));
		hasLength((j)->j, (json) -> ((JsonMap)json).get("pseudo"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("email"), 1, 100);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("firstname"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("lastname"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("password"), 1, 1000);
	}

	@Route(path = "/register",type = POST)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		if(context.isLogged()){
			context.response(403);
			return;
		}
		
		JsonMap json = readJson(reader);
		if(!isValid(() -> context.response(400))) return;
		
		CherryUser user = new CherryUser(json.get("pseudo"), json.get("email"), json.get("firstname"), json.get("lastname"));
		if(repo.register(user, json.get("password"))){
			context.response(200,
					"Access-Control-Expose-Headers: Authorization",
					"Authorization: Bearer " + this.router.createAuthUser(user));
			return;
		}
		context.response(400);
		return;
	}

}
