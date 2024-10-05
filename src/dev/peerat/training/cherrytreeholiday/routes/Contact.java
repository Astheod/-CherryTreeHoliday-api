package dev.peerat.training.cherrytreeholiday.routes;

import static dev.peerat.framework.RequestType.POST;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Contact extends FormResponse{
	
	private Session session;
	private String fromAddress;
	
	public Contact(String host, int port, String username, String password, String fromAddress){
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		Authenticator auth = new Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(username, password);
			}
		};
		session = Session.getInstance(props, auth);
		this.fromAddress = fromAddress;
		
		require((json) -> ((JsonMap)json).has("reply"));
		require((json) -> ((JsonMap)json).has("content"));
		hasLength((j)->j, (json) -> ((JsonMap)json).get("reply"), 5, 500);
		hasLength((j)->j, (json) -> ((JsonMap)json).get("content"), 1, 1000);
	}

	@Route(path = "/contact", type = POST)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		JsonMap json = readJson(reader);
		
		if(!isValid(() -> context.response(400))) return;
		
		context.response(200);
		send(fromAddress, "Call an ambulance, call an ambulance, but for "+json.get("reply"), json.get("content"));
	}

	public void send(String toAddress, String subject, String text){
    	try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(fromAddress, "Peer-at Code"));
	      msg.setReplyTo(InternetAddress.parse(fromAddress, false));
	      msg.setSubject(subject, "UTF-8");
	      msg.setText(text, "UTF-8");
	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
	      Transport.send(msg);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
    }
}
