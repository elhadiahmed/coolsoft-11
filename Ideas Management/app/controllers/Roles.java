package controllers;

import java.util.ArrayList;
import java.util.List;

import play.*;
import models.*;

public class Roles extends CRUD{

	/*
	 * retrieve the role by name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param: role is the name of the role
	 * 
	 * @return Role
	 */

	public static Role getRoleByName(String role) {

		return Role.find("byRoleName", role).first();

	}
	/**
	 * This methods returns all the DEFAULT actions permitted to a certain role
	 * @author nada ossama
	 * @param roleName is the name of the role
	 * @return list of actions of that role
	 */
	
   public static List<String> getRoleActions (String roleName){
		
		String actionsString = (Role.find("select r.actions from Role r where r.roleName like ? ",roleName ).first());
		String [] actionsArray = actionsString.split(";");
		List<String> actionsList = new ArrayList();
		for (int i = 0; i <actionsArray.length ; i++){
			actionsList.add(actionsArray[i]);
		}
		return actionsList;
	}
   //13
	/**
	 * this method returns the list of Organizer actions that are related to Topics not the whole entity
	 * @author nada  ossama
	 * @param 
	 */
	public static List <String> getOrganizerTopicActions (){
		
		List<String> allActions = getRoleActions("organizer");
		List<String> topicActions = new ArrayList<String>();
		for (int i = 13 ; i < allActions.size() ; i++ ){
		 topicActions.add(allActions.get(i));
		}
		return topicActions;
	}
}
