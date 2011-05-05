package notifiers;

import play.mvc.*;
import java.util.*;

import models.*;

public class Mail extends Mailer {
	
	/**
	 * this Method is responsible for sending a mail to the registered/unregistered
	 * inviting him to be organizer/idea developer
	 * 
	 * @author ${Mai Magdy}
	 * 
	 * @story C1S6
	 * 
	 * @param role
	 *            the role whether idea developer/organizer
	 * 
	 * @param email
	 *             the email the invitation will be sent to
	 * 
	 * @param organization
	 *                      the organization name that sends the invitation
	 * 
	 * @param entity
	 *                     the entity name that sends the invitation
	 * 
	 */

	public static void invite(String email, String role, String organization,
			String entity) {
		addRecipient(email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Invitation");
		 String url = "http://localhost:9008/invitations/view" ;
		User user = User.find("byEmail", email).first();
		System.out.println(entity);
		int id=1;
		int check=1;
		if(user==null)
			id=0;
		if(entity=="")
		    check=0;
		send(user, role,url ,organization, entity,id,check);

	}

	/*
	 * this Method is responsible for sending a mail to the topic organizers
	 * when an idea inside the topic is reported as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param topicOrganizer : the organizer of be topic
	 * 
	 * @param reporter : the user who reported the idea
	 * 
	 * @param reportedIdea : the reported idea
	 */

	public static void ReportAsSpamMail(User topicOrganizer, User reporter,
			Idea reportedIdea) {
		addRecipient(topicOrganizer.email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("An Idea is Reported as a Spam");
		send(topicOrganizer, reporter, reportedIdea);
	}
}