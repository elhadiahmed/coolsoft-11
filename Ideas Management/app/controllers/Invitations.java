package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import play.data.validation.Required;
import play.mvc.Controller;
import models.Invitation;
import models.MainEntity;
import models.Notification;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;

public class Invitations extends CRUD {

	// invitation first page ,it has search or button 'invite by mail'
	/**
	 * 
	 * This method is responsible for rendering the organization, the entity to
	 * the invitation home page, that contains search for user to invite or
	 * invite by mail
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entt
	 *             the entity id that sends the invitation
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void invite(long orgId,long entId) {
         
		Organization org=Organization.findById(orgId);
        MainEntity ent= MainEntity.findById(entId);
		render(ent,org);

	}

	// Go to search page
	/**
	 * 
	 * This method is responsible for searching for a user to invite the result
	 * is a list not containing the organizers of this entity
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entt
	 *            the entity that sends the invitation
	 * 
	 * @param name
	 *            the entered user name
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void SearchUsers(long orgId,long entId,@Required String name) {

		//Organization org=Organization.findById(orgId);
        //MainEntity ent= MainEntity.findById(entId);
        
        Organization org=Organization.findById(((long)1));
        MainEntity ent= MainEntity.findById(((long)1));
		
		if (validation.hasErrors()) {
			flash.error("Please enter a name first!");
			invite(orgId,entId);
		}
         
	   // List <User> users=User.findAll();	
		List<User> users = new ArrayList<User>();
	//	User u=User.find("byEmail", name).first();
	//	users.add(u);
		users = Users.searchUser(name);
	/*	List<User> organizers = Users.getEntityOrganizers(ent);
		organizers.add(org.creator);

		List<User> users = new ArrayList<User>();
		for (int i = 0; i < filter.size(); i++) {
			if (!organizers.contains(filter.get(i)))
				users.add(filter.get(i));
		}*/

		render(users, ent,org);
	}

	/**
	 * 
	 * This method is responsible for rendering the organization, the entity and
	 * the user to the invitation page , if no user selected the view will
	 * enable a text box to enter the email
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entt
	 *            the entity id that sends the invitation
	 * 
	 * @param id
	 *            the id of the selected user , 0 if inviting by mail
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void Page(long orgId,long entId, long id) {
		
		Organization org=Organization.findById(orgId);
        MainEntity ent= MainEntity.findById(entId);
        //Organization org=Organization.findById(ent.organization);
		System.out.println(id);
		User user=User.findById(id);
		render(ent,org,id,user);
	}

	/**
	 * 
	 * This method is responsible for adding a new invitation and rendering the
	 * email then the Mail class sends the email, if sending to registered user
	 * he ll be notified with a notification
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param email
	 *            destination of the invitation
	 * 
	 * @param role
	 *            role that ll be assigned to the user in case accepted
	 * 
	 * 
	 * @param entt
	 *              entity id that sends the invitation
	 * 
	 * @return void
	 */


	 public static void send(@Required String email,String role,
			 long orgId,long entId,long id){
		  
		//   Organization org=Organization.findById(orgId);
	      //  MainEntity ent= MainEntity.findById(entId);
	        
	        Organization org=Organization.findById(((long)1));
	        MainEntity ent= MainEntity.findById(((long)1));
         
		    if (!rfc2822.matcher(email).matches()) {
			    flash.error("Invalid address");
			    Page(orgId,entId,id);
		    }
			if(role.equalsIgnoreCase("select")) {
			        flash.error("Please choose a Role");
			        Page(orgId,entId,id);
			    }
			
	         
		     Mail.invite(email,role,org.name,ent.name);
		    
	    	 
	    	 User user=Security.getConnected();
	          user.addInvitation(email,role,org,null);
	      /*  
	         User receiver=User.find("byEmail", email).first();
	         if(!receiver.equals(null)){
	        	 List<User> u=new ArrayList<User>();
	        	  u.add(receiver);
	        	//if(role.equalsIgnoreCase("organizer"))
	        	 Notifications.sendNotification(u, org.id, "organization",
	 					"You have received a new invitation from "
	 							+ org.name);
	            }

				//**fadwa
				List<User> organizers = Users.getEntityOrganizers(ent);
				if (!user.equals(org.creator)) {
					organizers.remove(user);
					organizers.add(org.creator);
				}
				Notifications.sendNotification(organizers, ent.id, "entity",
						"Invitation has been sent from entity "+ ent.name);
		                
		        //**
				*/
			 render(email);
		                 
	  }
	 private static final Pattern rfc2822 = Pattern.compile(
		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
		);
	 

	/**
	 * 
	 * This method is responsible for viewing the received invitations It
	 * renders the invitation list
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S4
	 * 
	 * @param
	 * 
	 * 
	 * @return void
	 */

	public static void view() {

		User user = Security.getConnected();
		List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		
		render(inv);

	}

	/**
	 * 
	 * This method is responsible for responding to the user (accept/reject) to
	 * the invitation It renders the invitation list and the id (0/1) and the
	 * invitation id
	 * 
	 * 
	 * @author ${Mai.Magdy} > if role is organizer
	 * 
	 * @story C1S4
	 * 
	 * @param choice
	 *            0 if reject or 1 if accept
	 * 
	 * @param id
	 *            the invitation id
	 * 
	 * 
	 * @return void
	 */

	public static void respond(int id,long i) {
		System.out.println("HERE");
		  Invitation invite=Invitation.findById(i);
	      Organization org=invite.organization;
	      org.invitation.remove(invite);
	      
	     MainEntity ent=invite.entity;
	     if(ent!=null){
	      ent.invitationList.remove(invite);
	      }
	      User u=invite.sender;
	      u.invitation.remove(invite);
	      invite.delete();
		/*Invitation invite = Invitation.findById(invId);
		 //  System.out.println(id);
		if (choice == 1) {
			String rolename = invite.role;
			Organization org = invite.organization;
			MainEntity ent = invite.entity;
			User user = User.find("byEmail", invite.email).first();
			Role role = Role.find("byName", rolename).first();

			// *fadwa
			List<User> organizers = Users.getEntityOrganizers(ent);
			if (!invite.sender.equals(org.creator)) {
				organizers.remove(invite.sender);
				organizers.add(org.creator);
			}
			Notifications.sendNotification(organizers, ent.id, "entity",
					"New organizer has been added as an organizer to entity  "
							+ ent.name);
			// *

			if (rolename.equalsIgnoreCase("organzier")) {

				UserRoleInOrganizations.addEnrolledUser(user, org, role);
				UserRoleInOrganizations.addEnrolledUser(user, org, role,
						ent.id, "entity");

			} else {
				// idea devoloper by ibrahim adel
				Role role2 = Role.find("byRoleName", "Idea Developer").first();
//				if (role2 == null) {
//					// role ???
//					role2 = new Role("Idea Developer", "");
//					role2._save();
//				}
				UserRoleInOrganization roleInOrg = new UserRoleInOrganization(
						user, org, role2);
				roleInOrg._save();
				user.userRolesInOrganization.add(roleInOrg);
				Notification n1 = new Notification("Invitation accepted",
						invite.sender, user.username
								+ " accepted th invitation");
				n1._save();
				User orgLead = org.creator;
				if (orgLead.id != invite.sender.id) {
					/**
					 * please use the method sendNotification 
					 * there is no need for this part.because I need to add this notification 
					 * to the user's notification profile
					 */
	/*				Notification n2 = new Notification("Invitation accepted",
							orgLead, user.username + " accepted th invitation");
					n2._save();
				}

			}

		}

		invite.delete(); */

	}

}
