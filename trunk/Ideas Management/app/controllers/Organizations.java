package controllers;

import models.MainEntity;
import models.Organization;
import models.User;

public class Organizations extends CRUD{
	
	
	/**
	 * 
	 * This Method returns the Privacy level of an organization given the organization Id
	 * 
	 * @author 	Fadwa sakr
	 * 
	 * @story 	C2S34
	 * 
	 * @param 	id 	: the id of the organization for which the privacy level is needed
	 *
	 * @return	short
	 */

	 public static short getPrivacyLevel(Long id){
		 Organization organization = Organization.findById(id);
		 if(organization!=null)
			 return organization.privacyLevel;
		return -1;
	 }
	 /**
		 * 
		 * This Method returns the Privacy level of an organization given the organization Id
		 * 
		 * @author 	Fadwa
		 * 
		 * @story 	C2S4
		 * 
		 * @param 	organizationId 	: the id of the organization for which the preferences is being enabled
		 *
		 * @return	void
		 */

	 public static void enableTags(Long id){
		 Organization organization = Organization.findById(id);
		    notFoundIfNull(organization);
			 organization.createTag=true;
	 }
	 /**
		 * 
		 * This Method returns the Privacy level of an organization given the organization Id
		 * 
		 * @author 	Fadwa
		 * 
		 * @story 	C2S4
		 * 
		 * @param 	organizationId 	: the id of the organization for which the preferences is being disabled
		 *
		 * @return	void
		 */
	 public static void disableTags(Long id){
		 Organization organization = Organization.findById(id);
		    notFoundIfNull(organization);
			 organization.createTag=false;
	 }
}
