package dev.peerat.training.cherrytreeholiday;

import static dev.peerat.framework.RequestType.OPTIONS;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Locker;
import dev.peerat.framework.Locker.Key;
import dev.peerat.framework.RequestType;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.Router;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.models.Invitation;
import dev.peerat.training.cherrytreeholiday.repository.MySQLRepository;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.routes.Contact;
import dev.peerat.training.cherrytreeholiday.routes.UserNumber;
import dev.peerat.training.cherrytreeholiday.routes.activities.CreateActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.DeleteActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.EditActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.JoinActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.LeaveActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.RemoveUserActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.SwitchOwnerActivity;
import dev.peerat.training.cherrytreeholiday.routes.activities.ViewActivity;
import dev.peerat.training.cherrytreeholiday.routes.holidays.Chat;
import dev.peerat.training.cherrytreeholiday.routes.holidays.CreateHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.DeleteHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.EditHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.Export;
import dev.peerat.training.cherrytreeholiday.routes.holidays.RemoveUserHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.SwitchOwnerHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.ViewActivities;
import dev.peerat.training.cherrytreeholiday.routes.holidays.ViewHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.ViewHolidayUsers;
import dev.peerat.training.cherrytreeholiday.routes.holidays.Weather;
import dev.peerat.training.cherrytreeholiday.routes.holidays.invitations.InviteHoliday;
import dev.peerat.training.cherrytreeholiday.routes.holidays.invitations.InviteList;
import dev.peerat.training.cherrytreeholiday.routes.holidays.invitations.InviteNotifier;
import dev.peerat.training.cherrytreeholiday.routes.holidays.invitations.ReactInviteHoliday;
import dev.peerat.training.cherrytreeholiday.routes.users.ExternalLogin;
import dev.peerat.training.cherrytreeholiday.routes.users.Login;
import dev.peerat.training.cherrytreeholiday.routes.users.Register;
import dev.peerat.training.cherrytreeholiday.services.auth.GoogleAuth;
import dev.peerat.training.cherrytreeholiday.services.auth.OAuthService;
import dev.peerat.training.cherrytreeholiday.routes.Country;

public class Main{
	
	public static void main(String[] args) throws Exception{
		Router<CherryUser> router = new Router<CherryUser>().configureJwt(
				(builder) -> builder.setExpectedIssuer("http://localhost"),
				(claims) -> {
					claims.setIssuer("http://localhost"); // who creates the token and signs it
					claims.setExpirationTimeMinutesInTheFuture(100);
				}, 
				(claims) -> new CherryUser(claims)) //to complete
				.addDefaultHeaders(RequestType.GET, "Access-Control-Allow-Origin: *")
				.addDefaultHeaders(RequestType.POST, "Access-Control-Allow-Origin: *")
				.addDefaultHeaders(RequestType.PUT, "Access-Control-Allow-Origin: *")
				.addDefaultHeaders(RequestType.OPTIONS,
						"Access-Control-Allow-Origin: *",
						"Access-Control-Allow-Methods: *",
						"Access-Control-Allow-Headers: *")
				.activeReOrdering();
		
		router.setDefault((matcher, context, reader, writer) -> {
			context.response(404);
		});
		
		router.register(new Response(){
			@Route(path = "^(.*)$", type = OPTIONS)
			public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception {
				context.response(200);
			}

		});
		
		Repository repo = new MySQLRepository("192.168.132.200",13306,"E200033","E200033",new String(new byte[] {48, 48, 51, 51}));
		
		registerRoutes(router, repo);
		
		new Thread(new Runnable(){
			public void run(){
				Key key = new Key();
				
				Locker<Context> locker = router.getLogger();
				
				locker.init(key);
				try {
					while(true){
						locker.lock(key);
						Context instance = locker.getValue(key);
						if(instance == null) continue;
						System.out.println("["+((instance.isLogged()) ? instance.<CherryUser>getUser().getPseudo():"?")+"] "+instance.getType()+" "+instance.getPath()+" -> "+instance.getResponseCode());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				locker.remove(key);
			}
		}).start();
		
		router.useTomcat();
//		router.listen(4000, false);
	}
	
	//check perms in routes
	private static void registerRoutes(Router<CherryUser> router, Repository repo){
		Map<String, OAuthService> services = new HashMap<>();
		services.put("google", new GoogleAuth());
		
		Locker<Invitation> invites = new Locker<>();
		
		 router.register(new Register(router, repo))
		 	.register(new Login(repo, router))
		 	.register(new ExternalLogin(router, repo, services))
		 	.register(new Chat(repo))
		 	.register(new CreateHoliday(repo))
		 	.register(new EditHoliday(repo))
		 	.register(new DeleteHoliday(repo))
		 	.register(new InviteHoliday(repo, invites))
		 	.register(new ReactInviteHoliday(repo))
		 	.register(new InviteNotifier(repo, invites))
		 	.register(new ViewHoliday(repo))
		 	.register(new ViewHolidayUsers(repo))
		 	.register(new RemoveUserHoliday(repo))
		 	.register(new SwitchOwnerHoliday(repo))
		 	.register(new CreateActivity(repo))
		 	.register(new EditActivity(repo))
		 	.register(new SwitchOwnerActivity(repo))
		 	.register(new DeleteActivity(repo))
		 	.register(new JoinActivity(repo))
		 	.register(new LeaveActivity(repo))
		 	.register(new RemoveUserActivity(repo))
		 	.register(new RemoveUserHoliday(repo))
		 	.register(new UserNumber(repo))
		 	.register(new Export(repo))
		 	.register(new Contact("", 0, "", "", ""))
		 	.register(new InviteList(repo))
		 	.register(new Country(repo))
		 	.register(new Weather(repo))
		 	.register(new ViewActivity(repo))
		 	.register(new ViewActivities(repo));
	}
}
