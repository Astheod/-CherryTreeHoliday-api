package dev.peerat.training.cherrytreeholiday.routes.users;

import static dev.peerat.framework.RequestType.POST;

import java.util.Map;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.Router;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.services.auth.OAuthService;

public class ExternalLogin implements Response{
	
	private Router<CherryUser> router;
	private Repository repo;
	private Map<String, OAuthService> services;
	
	public ExternalLogin(Router<CherryUser> router, Repository repo, Map<String, OAuthService> services){
		this.router = router;
		this.repo = repo;
		this.services = services;
	}

	@Route(path = "^/externalLogin/([a-zA-Z]{3,20})$",type = POST)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		if(context.isLogged()){
			context.response(403);
			return;
		}
		
		OAuthService service = this.services.get(matcher.group(1));
		if(service == null){
			context.response(404);
			return;
		}
		
		CherryUser user = service.auth(reader.readJson());
		if(user == null){
			context.response(401);
			return;
		}
		
		if(repo.register(user) || repo.login(user.getEmail())){
			context.response(200,
					"Access-Control-Expose-Headers: Authorization",
					"Authorization: Bearer " + this.router.createAuthUser(user));
			return;
		}
		
		context.response(401);
		return;
	}

}
