package dev.peerat.training.cherrytreeholiday.routes.users;

import dev.peerat.framework.*;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.routes.FormResponse;

import java.util.regex.Matcher;

import static dev.peerat.framework.RequestType.POST;

public class Login extends FormResponse{
	
	private Repository repo;
	private Router<CherryUser> router;
	public Login(Repository repo, Router<CherryUser> router){
		this.repo = repo;
		this.router = router;
		
		require((json) -> ((JsonMap)json).has("pseudo"));
		require((json) -> ((JsonMap)json).has("password"));
		hasLength((j)->j, (json) -> ((JsonMap)json).get("pseudo"), 1, 50);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("password"), 1, 1000);
	}

	@Route(path = "/login",type = POST)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		if(context.isLogged()){
			context.response(403);
			return;
		}

		JsonMap readed = readJson(reader);
		if(!isValid(() -> context.response(400))) return;

		// Je suis pas sur de comment utiliser le framework à cette endroit là
		String pseudo = readed.get("pseudo");
		if(repo.login(pseudo,readed.get("password"))) {
			context.response(200,
					"Access-Control-Expose-Headers: Authorization",
					"Authorization: Bearer " + this.router.createAuthUser(new CherryUser(pseudo)));
		} else {
			context.response(404);
		}
	}
}