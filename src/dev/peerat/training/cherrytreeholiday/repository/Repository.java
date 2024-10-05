package dev.peerat.training.cherrytreeholiday.repository;

import java.util.List;
import java.util.Map;

import dev.peerat.training.cherrytreeholiday.models.Activity;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.models.Holiday;
import dev.peerat.training.cherrytreeholiday.models.Message;
import dev.peerat.training.cherrytreeholiday.models.Invitation;

public interface Repository {
	
	int getNumberOfUser();
	
	boolean register(CherryUser user, String password);

	boolean login(String username, String password);
	
	boolean register(CherryUser user);

	boolean login(String email);
	
	List<Message> loadChat(int holidayId);
	
	boolean sendMessage(Message message);
	
	boolean createHoliday(CherryUser user, Holiday holiday);
	
	boolean deleteHoliday(int holidayId);
	
	List<Holiday> getHolidays(CherryUser user);
	
	List<Activity> getActivities(int holidayId);
	
	Holiday getHoliday(int holidayId);
	
	Activity getActivity(int activityId);
	
	boolean editHoliday(CherryUser user, int holidayId, Holiday holiday);
	
	boolean isHolidayOwner(CherryUser user, int holidayId);
	
	boolean isActivityOwner(CherryUser user, int activityId);
	
	List<CherryUser> getHolidayUsers(int holidayId);
	
	List<CherryUser> getActivityUsers(int holidayId);
	
	boolean inviteHoliday(String username, int holidayId);
	
	boolean inviteHoliday(CherryUser user, int holidayId, boolean accepted);
	
	List<Invitation> getInvitations(CherryUser user);
	
	boolean removeHolidayUser(List<String> user, int holidayId);
	
	boolean switchHolidayOwner(int holidayId, String username);
	
	boolean switchActivityOwner(int activityId, String username);
	
	boolean createActivity(CherryUser user, int holidayId, Activity activity);
	
	boolean deleteActivity(int activityId);
	
	boolean editActivity(CherryUser user, int activityId, Activity activity);
	
	boolean joinActivity(CherryUser user, int activityId);
	
	boolean leaveActivity(CherryUser user, int activityId);
	
	boolean removeFromActivity(String pseudo, int activityId);
	
	boolean isInHoliday(CherryUser user, int holidayId);
	
	boolean isInActivity(CherryUser user, int activityId);

	Map<String, Integer> getUserHolidayByCountry(String date);
	
	int getUserId(String pseudo) throws Exception;
}
