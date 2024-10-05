package dev.peerat.training.cherrytreeholiday.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.password4j.Password;

import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.models.Holiday;
import dev.peerat.training.cherrytreeholiday.models.Invitation;
import dev.peerat.training.cherrytreeholiday.models.Message;
import dev.peerat.training.cherrytreeholiday.models.Address;

public class MySQLRepository implements Repository{
	
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	
	private Connection con;
	
	public MySQLRepository(String host, int port, String database, String user, String password) throws Exception{
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		
		Class.forName("com.mysql.cj.jdbc.Driver");
	}
	
	private void ensureConnection() throws SQLException {
		if (con == null || (!con.isValid(5))) {
			this.con = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "",
					user, password);
		}
	}

	@Override
	public int getNumberOfUser(){
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT COUNT(*) FROM user");
			ResultSet result = stmt.executeQuery();
			if(result.next()) return result.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public Map<String, Integer> getUserHolidayByCountry(String date) {
		Map<String, Integer> result = new HashMap<>();
;		try {
			// nombres d'utilisateurs en vacances pour une date encodée, répartis par pays de destination
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT addr.country, count(h.id_holiday) + count(u.id_user) as vacancier\n"
					+ "FROM holiday h\n"
					+ "JOIN address addr ON h.id_address = addr.id_address\n"
					+ "LEFT JOIN participate_holiday ph on h.id_holiday = ph.id_holiday\n"
					+ "LEFT JOIN user u ON ph.id_user = u.id_user\n"
					+ "WHERE ? BETWEEN h.start AND h.end\n"
					+ "GROUP BY addr.country");
			stmt.setDate(1, Date.valueOf(date));
			ResultSet set = stmt.executeQuery();
			while(set.next()) {
				result.put(set.getString("country"), set.getInt("vacancier"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean register(CherryUser user, String password) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO user (pseudo,email,firstname,lastname,password,is_admin) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, user.getPseudo());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, Password.hash(password).withArgon2().getResult());
			stmt.setBoolean(6, false);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean login(String username, String password){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT id_user, password FROM user WHERE pseudo = ? OR email = ?");
			stmt.setString(1, username);
			stmt.setString(2, username);
			ResultSet result = stmt.executeQuery();
			return (result.next() && Password.check(password, result.getString("password")).withArgon2());
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean register(CherryUser user) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO user (pseudo,email,firstname, lastname,password,is_admin) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, user.getPseudo());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, "");
			stmt.setBoolean(6, false);
			boolean use = stmt.executeUpdate() > 0;
			System.out.println(use);
			return use;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean login(String email) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT id_user FROM user WHERE email = ?");
			stmt.setString(1, email);
			return stmt.executeQuery().next();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public List<Message> loadChat(int holidayId){
		List<Message> result = new ArrayList<>();
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT m.*, u.* FROM message m JOIN user u ON u.id_user = m.id_author WHERE m.id_holiday = ?");
			stmt.setInt(1, holidayId);
			ResultSet set = stmt.executeQuery();
			while(set.next()) result.add(new Message(set.getString("m.content"), set.getDate("m.sent").toLocalDate().toEpochDay(), new CherryUser(set.getString("u.pseudo")), holidayId)); //to verify
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean sendMessage(Message message){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO message (content,sent,id_author,id_holiday) VALUES (?,?,?,?)");
			stmt.setString(1, message.getContent());
			stmt.setDate(2, Date.valueOf(LocalDate.ofEpochDay(message.getSent())));
			stmt.setInt(3, getUserId(message.getAuthor().getPseudo()));
			stmt.setInt(4, message.getHolidayId());
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public int getUserId(String pseudo) throws Exception{
		PreparedStatement stmt = this.con.prepareStatement("SELECT id_user FROM user WHERE pseudo = ?");
		stmt.setString(1, pseudo);
		ResultSet set = stmt.executeQuery();
		if(!set.next()) throw new NullPointerException();
		return set.getInt("id_user");
	}
	

	@Override
	public boolean createHoliday(CherryUser user, Holiday holiday) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO holiday (name,description,start,end,id_address,id_owner) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, holiday.getName());
			stmt.setString(2, holiday.getDescription());
			stmt.setDate(3, new Date(holiday.getStartDate().getTime()));
			stmt.setDate(4, new Date(holiday.getEndDate().getTime()));
			stmt.setInt(5, createAddress(holiday.getAddress()));
			stmt.setInt(6, getUserId(user.getPseudo()));
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	private int createAddress(Address address) throws Exception{
		PreparedStatement stmt = this.con.prepareStatement("INSERT INTO address (country,city,code_postal,street,num) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, address.getCountry());
		stmt.setString(2, address.getCity());
		stmt.setString(3, address.getCodePostal());
		stmt.setString(4, address.getStreet());
		stmt.setString(5, address.getNumber());
		if(stmt.executeUpdate() < 1) throw new NullPointerException();
		ResultSet result = stmt.getGeneratedKeys();
		if(!result.next()) throw new NullPointerException();
		return result.getInt(1);
	}

	@Override
	public boolean deleteHoliday(int holidayId) {
		try{
			ensureConnection();
			PreparedStatement stmtActivity = this.con.prepareStatement("DELETE FROM activity WHERE id_holiday = ?");
			stmtActivity.setInt(1, holidayId);
			stmtActivity.executeUpdate();
				
			PreparedStatement stmt = this.con.prepareStatement("DELETE FROM holiday WHERE id_holiday = ?");
			stmt.setInt(1, holidayId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public List<Holiday> getHolidays(CherryUser user){
		List<Holiday> result = new ArrayList<>();
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT h.*,a.* FROM holiday h JOIN address a ON a.id_address = h.id_address JOIN user u ON u.id_user = h.id_owner LEFT JOIN participate_holiday ph ON ph.id_holiday = h.id_holiday LEFT JOIN user pu ON pu.id_user = ph.id_user WHERE u.pseudo = ? OR pu.pseudo = ?");
			stmt.setString(1, user.getPseudo());
			stmt.setString(2, user.getPseudo());
			ResultSet set = stmt.executeQuery();
			while (set.next()) result.add(
					new Holiday(
							set.getInt("h.id_holiday"),
							set.getString("h.name"),
							set.getString("h.description"),
							set.getDate("h.start"),
							set.getDate("h.end"),
							new Address(
									set.getInt("a.id_address"),
									set.getString("a.country"),
									set.getString("a.city"),
									set.getString("a.code_postal"),
									set.getString("a.street"),
									set.getString("a.num"))));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	
	@Override
	public List<Activity> getActivities(int holidayId) {
		List<Activity> result = new ArrayList<>();
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT ac.*,ad.* FROM activity ac JOIN address ad ON ac.id_address = ad.id_address WHERE ac.id_holiday = ?");
			stmt.setInt(1, holidayId);
			ResultSet set = stmt.executeQuery();
			while (set.next()) result.add(
					new Activity(
							set.getInt("ac.id_activity"),
							set.getString("ac.name"),
							set.getString("ac.description"),
							set.getDate("ac.start"),
							set.getDate("ac.end"),
							new Address(
									set.getInt("ad.id_address"),
									set.getString("ad.country"),
									set.getString("ad.city"),
									set.getString("ad.code_postal"),
									set.getString("ad.street"),
									set.getString("ad.num")),
							set.getInt("ac.id_holiday")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Holiday getHoliday(int holidayId){
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT h.*,a.* FROM holiday h JOIN address a ON a.id_address = h.id_address JOIN user u ON u.id_user = h.id_owner WHERE h.id_holiday = ?");
			stmt.setInt(1, holidayId);
			ResultSet set = stmt.executeQuery();
			if(set.next()) 
					return new Holiday(
							set.getInt("h.id_holiday"),
							set.getString("h.name"),
							set.getString("h.description"),
							set.getDate("h.start"),
							set.getDate("h.end"),
							new Address(
									set.getInt("a.id_address"),
									set.getString("a.country"),
									set.getString("a.city"),
									set.getString("a.code_postal"),
									set.getString("a.street"),
									set.getString("a.num")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Activity getActivity(int activityId) {
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT ac.*,ad.* FROM activity ac JOIN address ad ON ac.id_address = ad.id_address WHERE ac.id_activity = ?");
			stmt.setInt(1, activityId);
			ResultSet set = stmt.executeQuery();
			if(set.next()) 
					return new Activity(
							set.getInt("ac.id_activity"),
							set.getString("ac.name"),
							set.getString("ac.description"),
							set.getDate("ac.start"),
							set.getDate("ac.end"),
							new Address(
									set.getInt("ad.id_address"),
									set.getString("ad.country"),
									set.getString("ad.city"),
									set.getString("ad.code_postal"),
									set.getString("ad.street"),
									set.getString("ad.num")),
							set.getInt("ac.id_holiday"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean editHoliday(CherryUser user, int holidayId, Holiday holiday) {
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("UPDATE holiday SET name = ?,description = ?,start = ?,end = ?,id_address = ? WHERE id = ?");
			stmt.setString(1, holiday.getName());
			stmt.setString(2, holiday.getDescription());
			stmt.setDate(3, new Date(holiday.getStartDate().getTime()));
			stmt.setDate(4, new Date(holiday.getEndDate().getTime()));
			stmt.setInt(5, createAddress(holiday.getAddress()));
			stmt.setInt(6, holidayId);
			return (stmt.executeUpdate() > 0);
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean isHolidayOwner(CherryUser user, int holidayId){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT u.id_user FROM holiday h JOIN user u ON h.id_owner = u.id_user WHERE h.id_holiday = ? AND u.pseudo = ?");
			stmt.setInt(1, holidayId);
			stmt.setString(2, user.getPseudo());
			return stmt.executeQuery().next();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean isActivityOwner(CherryUser user, int activityId) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT u.id_user FROM activity a JOIN user u ON a.id_owner = u.id_user WHERE a.id_activity = ? AND u.pseudo = ?");
			stmt.setInt(1, activityId);
			stmt.setString(2, user.getPseudo());
			return stmt.executeQuery().next();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<CherryUser> getHolidayUsers(int holidayId) {
		List<CherryUser> result = new ArrayList<>();
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT u.pseudo, pu.pseudo FROM holiday h JOIN user u ON h.id_owner = u.id_user LEFT JOIN participate_holiday ph ON ph.id_holiday = h.id_holiday LEFT JOIN user pu ON pu.id_user = ph.id_user  WHERE h.id_holiday = ?");
			stmt.setInt(1, holidayId);
			ResultSet set = stmt.executeQuery();
			while(set.next()){
				if(result.size() == 0) result.add(new CherryUser(set.getString("u.pseudo")));
				String username = set.getString("pu.pseudo");
				if(username != null) result.add(new CherryUser(username));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public List<CherryUser> getActivityUsers(int holidayId){
		
		return null;
	}
	
	@Override
	public boolean inviteHoliday(String username, int holidayId){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO invitation (id_user, id_holiday) VALUES ((SELECT id_user FROM user WHERE pseudo = ?),?)");
			stmt.setString(1, username);
			stmt.setInt(2, holidayId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean inviteHoliday(CherryUser user, int holidayId, boolean accepted){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("DELETE FROM invitation WHERE id_holiday = ? AND id_user = (SELECT id_user FROM user WHERE pseudo = ?)");
			stmt.setInt(1, holidayId);
			stmt.setString(2, user.getPseudo());
			if(stmt.executeUpdate() > 0){
				if(accepted){
					stmt = this.con.prepareStatement("INSERT INTO participate_holiday (id_user, id_holiday) VALUES ((SELECT id_user FROM user WHERE pseudo = ?),?)");
					stmt.setString(1, user.getPseudo());
					stmt.setInt(2, holidayId);
					return (stmt.executeUpdate() > 0);
				}
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<Invitation> getInvitations(CherryUser user){
		List<Invitation> result = new ArrayList<>();
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("SELECT h.id_holiday, h.name FROM invitation i JOIN holiday h ON h.id_holiday = i.id_holiday JOIN user u ON u.id_user = i.id_user WHERE u.pseudo = ?");
			stmt.setString(1, user.getPseudo());
			ResultSet set = stmt.executeQuery();
			while(set.next()) result.add(new Invitation(set.getInt("h.id_holiday"), set.getString("h.name")));
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean removeHolidayUser(List<String> user, int holidayId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean switchHolidayOwner(int holidayId, String username) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("UPDATE holiday SET user_id = (SELECT id_user FROM user WHERE pseudo = ?) WHERE id_holiday = ?");
			stmt.setString(1, username);
			stmt.setInt(2, holidayId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean switchActivityOwner(int activityId, String username) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("UPDATE activity SET user_id = (SELECT id_user FROM user WHERE pseudo = ?) WHERE id_activity = ?");
			stmt.setString(1, username);
			stmt.setInt(2, activityId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean createActivity(CherryUser user, int holidayId, Activity activity) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO activity (name,description,start,end,id_address,id_owner,id_holiday) VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, activity.getName());
			stmt.setString(2, activity.getDescription());
			stmt.setDate(3, new Date(activity.getStartDate().getTime()));
			stmt.setDate(4, new Date(activity.getEndDate().getTime()));
			stmt.setInt(5, createAddress(activity.getAddress()));
			stmt.setInt(6, getUserId(user.getPseudo()));
			stmt.setInt(7, holidayId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteActivity(int activityId){
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("DELETE FROM activity WHERE id_activity = ?");
			stmt.setInt(1, activityId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean editActivity(CherryUser user, int activityId, Activity activity) {
		try {
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("UPDATE activity SET name = ?,description = ?,start = ?,end = ?,id_address = ? WHERE id = ?");
			stmt.setString(1, activity.getName());
			stmt.setString(2, activity.getDescription());
			stmt.setDate(3, new Date(activity.getStartDate().getTime()));
			stmt.setDate(4, new Date(activity.getEndDate().getTime()));
			stmt.setInt(5, createAddress(activity.getAddress()));
			stmt.setInt(6, activityId);
			return (stmt.executeUpdate() > 0);
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean joinActivity(CherryUser user, int activityId) {
		try{
			PreparedStatement stmt = this.con.prepareStatement("INSERT INTO participate_activity (id_user, id_activity) VALUES ((SELECT id_user FROM user WHERE pseudo = ?),?)");
			stmt.setString(1, user.getPseudo());
			stmt.setInt(2, activityId);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean leaveActivity(CherryUser user, int activityId) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("DELETE FROM participate_activity WHERE id_activity = ? AND id_user = (SELECT id_user FROM user WHERE pseudo = ?)");
			stmt.setInt(1, activityId);
			stmt.setString(2, user.getPseudo());
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean removeFromActivity(String pseudo, int activityId) {
		try{
			ensureConnection();
			PreparedStatement stmt = this.con.prepareStatement("DELETE FROM participate_activity WHERE id_activity = ? AND id_user = (SELECT id_user FROM user WHERE pseudo = ?)");
			stmt.setInt(1, activityId);
			stmt.setString(2, pseudo);
			return (stmt.executeUpdate() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isInHoliday(CherryUser user, int holidayId){
		try{
			if(isHolidayOwner(user, holidayId)) return true;
			PreparedStatement stmt = this.con.prepareStatement("SELECT u.id_user FROM holiday h JOIN participate_holiday ph ON ph.id_holiday = h.id_holiday JOIN user u ON u.id_user = ph.id_user WHERE h.id_holiday = ? AND u.pseudo = ?");
			stmt.setInt(1, holidayId);
			stmt.setString(2, user.getPseudo());
			return stmt.executeQuery().next();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isInActivity(CherryUser user, int activityid){
		try{
			if(isActivityOwner(user, activityid)) return true;
			PreparedStatement stmt = this.con.prepareStatement("SELECT u.id_user FROM activity a JOIN participate_activity pa ON pa.id_activity = a.id_activity JOIN user u ON u.id_user = pa.id_user WHERE a.id_activity = ? AND u.pseudo = ?");
			stmt.setInt(1, activityid);
			stmt.setString(2, user.getPseudo());
			return stmt.executeQuery().next();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
}
