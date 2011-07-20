package notifiers;

import play.mvc.*;
import java.util.*;

import controllers.Security;

import models.*;
/**
 * This class is for sending the emails
 * 
 * @author Mai Magdy
 *
 */
public class Mail extends Mailer {

	/**
	 * Sends a mail to the registered/unregistered inviting him to be
	 * organizer/idea developer
	 * 
	 * @author ${Mai.Magdy},${Fadwa.Sakr}
	 * 
	 * @story C1S6
	 * 
	 * @param email
	 *            String email the invitation will be sent to
	 * 
	 * @param role
	 *            String role whether idea developer/organizer
	 *            
	 * @param organization
	 *            String organization name that sends the invitation
	 * 
	 * @param source
	 *            String source name that sends the invitation whether its an
	 *            entity/topic
	 * 
	 * @param type
	 *            int type whether the email is sent from topic(1)or entity(0)
	 * 
	 */

	public static void invite(String email, String role, String organization,
			String source, int type) {
		addRecipient(email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Invitation");
		String url = "http://localhost:9008/invitations/view";
		User user = User.find("byEmail", email).first();
		int id = 1;
		int check = 1;
		if (user == null)
			id = 0;
		if (source == "")
			check = 0;
		if (role.equals("ideadeveloper"))
			role = " idea developer";
		send(user, role, url, organization, source, id, check, type);

	}

	/**
	 * Sends a mail to the user informing him that his account has been
	 *  deactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 * 
	 */

	public static void deactivate() {
		User user = Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Deactivation");
		String url = "http://localhost:9008";
		send(user, url);

	}

	/**
	 * Sends a mail to the user informing him that his account has been
	 * reactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 */

	public static void reactivate() {
		User user = Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Reactivation");
		send(user);

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
	 * 
	 * @param description : description of the idea
	 * 
	 * @param title : title of the idea
	 */

	public static void reportAsSpamMail(User topicOrganizer, User reporter,
			Idea reportedIdea, String description, String title) {
		long ideaId = reportedIdea.id;
		addRecipient(/*topicOrganizer.email*/"elhadiahmed3@gmail.com");
		String url = "http://localhost:9008/ideas/show?ideaId="+ideaId;
		setFrom("coolsoft-11@gmail.com");
		setSubject("An Idea is Reported as a Spam");
		send(topicOrganizer, reporter, reportedIdea, description, title, url);
	}

	/*
	 * this Method is responsible for sending a mail to the topic organizers
	 * and the entity organizers when a Topic is reported as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param reciever : the organizer of be topic
	 * 
	 * @param reporter : the user who reported the idea
	 * 
	 * @param reportedIdea : the reported topic
	 * 
	 * @param description : description of the topic
	 * 
	 * @param title : title of the topic
	 */

	public static void reportTopicMail(User reciever, User reporter,
		Topic reportedTopic, String description, String title) {
		long topicId = reportedTopic.id;
		String url = "http://localhost:9008/ideas/show?ideaId="+topicId;
		addRecipient("elhadiahmed3@gmail.com");
		setFrom("coolsoft-11@gmail.com");
		setSubject("A Topic has been Reported as a Spam");
		send(reciever, reporter, topicId, description, title,url);
	}
	/*
	 * this Method is responsible for sending a mail to the topic organizers
	 * 	and the idea author or the plan creator when a comment is reported as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
	 * 
	 * @param reciever : the user who will recieve the mail
	 * 
	 * @param reporter : the user who reported the idea
	 * 
	 * @param comment : the reported comment
	 * 
	 * @param comment2 : the comment written as String
	 */

	public static void reportCommentMail(User reciever, User reporter,
			Comment comment, String comment2) {
		long commentId = comment.id;
		long ideaId = comment.commentedIdea.id;
		String url = "http://localhost:9008/ideas/show?ideaId="+ideaId;
		addRecipient("elhadiahmed3@gmail.com");
		setFrom("coolsoft-11@gmail.com");
		setSubject("A Topic has been Reported as a Spam");
		send(reciever, reporter, commentId, comment,comment2,url);
		
	}

	/**
	 * Sends an e-mail to the user with a new generated password and gives him a
	 * hyper link to the login page
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 
	 * @param username
	 *            String the username of the receiver
	 * @param email
	 *            String the e-mail of the recipient
	 * @param password
	 *            String the new generated password
	 */

	public static void recoverPassword(String username, String email,
			String password) {
		System.out.println(email);
		addRecipient(email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("No Reply: Password recovery");
		String url = "http://localhost:9008/secure/login";
		send(username, password, url);
	}

	/**
	 *sends an email to the person who was added by the admin
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 * 
	 */
	public static void welcome(User user) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome");
		String url = "http://localhost:9008";
		send(user, url);

	}

	/**
	 *  sends an email to the person who was added by the admin to activate his account
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9 , C1S10
	 * 
	 * @param user
	 *            :User the user
	 * 
	 */
	public static void activation(User user,String activationKey) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome to CoolSoft, activate your account ");
		String url = "http://localhost:9008";
		send(user, activationKey,url);

	}

	/**
	 * sends an email to the person who was deleted by the admin , notifying him he was deleted and 
	 * displaying the reason he was deleted for
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 * 
	 * @param message
	 *            :String the reason for which the user has been deleted
	 */
	public static void deletion(User user, String message) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Your account has been deleted ! ");
		System.out.println(user);
		send(user, message);

	}
	
	/**
	 * sends an email to the person who was deleted by the admin, notifying him he was 
	 * brought back(undeleted)
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 */
	public static void forgiven(User user) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome back ! ");
		String url = "http://localhost:9008";
		send(user,url);

	}
	
	/**
	 * sends an email to the admins containing feedback, whether to notify a probelm or 
	 * offer a suggestion
	 * 
	 * @author Mostafa Ali
	 * 
	 * 
	 * 
	 * @param email
	 *            :String the email of the user who gave the feedback
	 *            
	 * @param feedback
	 *            :String the message the user wants to report
	 *            
	 * @param subject 
	 * 				:String the subject of the feedback (the feedback is regarding which 
	 *				entity or problem)
	 */
	public static void sendFeedback(String feedbackerEmail,String feedback,String subject)
	{
		addRecipient("mostafa.aly0@gmail.com");
		//addRecipient("ideas-management@googlegroups.com");
		setFrom(feedbackerEmail);
		setSubject(subject);
		send(feedback);
	}

	

}