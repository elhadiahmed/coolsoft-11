package controllers;


import java.util.List;

import play.mvc.Controller;
import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;


public class Invitations extends CRUD {
	
	 public static void invite(){
		 
		      /*get user n list of organization&entity&topic tht he organizes*/
	    	//render(org,ent,top);
	  }
	 
	
	 public static void send(String email,String role,String organization,
			 String entity,String topic, String Comments){
		  
		 Mail.invite(email,role,organization,entity,topic);
		    
	    	Organization org= Organization.find("byName", organization).first();
	    	MainEntity ent=MainEntity.find("byName", entity).first();
	    	Topic top=Topic.find("byName", topic).first();
	    	 //**sender=the user from the session 
	        //**sender.addInvitation(email,role,org,ent,top);
	    	
		  //render(email,role,organization,entity,topic);
		    render(email);
	  }
	 
	 
	 public static void view(){
			
		   //**User user=get user from session
	       //<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
	       //render(inv);

		}

	public static void respond(int id,int i,User user){
		
		  //User user=get user from session
		  List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		  String rolename=inv.get(i).role;
		  Organization org=inv.get(i).organization;
		  MainEntity ent=inv.get(i).entity;
		  
		  Role role=Role.find("byName", rolename).first();
		  
		  List <User> organizers= Users.getEntityOrganizers(ent);
		  List <User> enrolled =Users.getEnrolledUsers(org);
		  
		  
		  boolean flag=false;
		  for(int j=0;j<organizers.size();j++){
			  if(organizers.get(j).equals(user))
				  flag=true;
		  }
		  
		  if(!flag){
		  UserRoleInOrganizations.addEnrolledUser(user,org,role);
		  UserRoleInOrganizations.addEnrolledUser(user,org,role,ent.id,"entity");
		  }
		  
		  flag=false;
		  for(int j=0;j<enrolled.size();j++){
			  if(enrolled.get(j).equals(user))
				  flag=true;
		  }
		  
		  if(org.privacyLevel!=2&&flag){
			  Role rol=Role.find("byName", "Idea Developer").first();
			  UserRoleInOrganizations.addEnrolledUser(user,org,rol);
		  }
		 
		 
		 Invitation invite=Invitation.findById(inv.get(i).id);
		 invite.delete();
		 
		 render(id,inv,i);
		 
		 
		
		
		
	}
        

	 
}
