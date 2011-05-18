package controllers;

import java.util.ArrayList;

import groovy.ui.text.FindReplaceUtility;

import javax.xml.transform.Source;

import play.db.jpa.Model;
import models.*;


/**
 * @author Mohamed
 * 
 * @story C2S5
 *
 */
public class TopicRelationships extends CRUD {

	/**
	 * creates a new relationship between two Topics
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param name
	 *            : name of the relationship
	 * 
	 * @param sourceId
	 *            : id of the first Topic to be related
	 * 
	 * @param destinationId
	 *            : id of the second Topic to be related
	 */
	public static void createRelationship(String name, long sourceId,
			long destinationId) {
		Topic source = Topic.findById(sourceId);
		Topic destination = Topic.findById(destinationId);

		TopicRelationship relation = new TopicRelationship(name, source,
				destination);
		
		relation.source.relationsSource.add(relation);
		relation.destination.relationsDestination.add(relation);
		if(!isDuplicate(name, relation.source.entity.organization.relationNames))
			relation.source.entity.organization.relationNames.add(name);
		
		relation.save();
		Organization organization = relation.source.entity.organization;
		organization.save();
		
		for (int i = 0; i < Users.getEntityOrganizers(source.entity).size(); i++) {
			Notifications.sendNotification(Users.getEntityOrganizers(source.entity)
					.get(i).id, source.entity.organization.id, "Organization",
					"a new relation \"" + name
							+ "\" is created now between topics \""
							+ source.title + "\" and \"" + destination.title
							+ "\".");
		}
		for (int i = 0; i < Users.getEntityOrganizers(destination.entity).size(); i++) {
			Notifications.sendNotification(Users.getEntityOrganizers(destination.entity)
					.get(i).id, source.entity.organization.id, "Organization",
					"a new relation \"" + name
							+ "\" is created now between topics \""
							+ source.title + "\" and \"" + destination.title
							+ "\".");
		}
	}

	/**
	 * renames a relationship after its created
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S6
	 * 
	 * @param relationToBeRenamedId
	 *            : the relation id that is being renamed
	 * 
	 * @param newName
	 *            : the new name to be set to the relationship
	 */
	public static void renameRelationship(long relationToBeRenamedId,
			String newName) {
		TopicRelationship relation = TopicRelationship
				.findById(relationToBeRenamedId);
		relation.name = newName;
		relation.save();

	}

	/**
	 * ends a relationship by deleting it
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S7
	 * 
	 * @param relationId
	 *            : the id of the relation being deleted
	 */
	public static void delete(long relationId) {
		TopicRelationship relation = TopicRelationship.findById(relationId);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		relation.delete();

	}

	/**
	 * checks if the user is able to create relationships or not
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param organizationId
	 *            : the id of the organization that the user belongs to
	 * 
	 * @return boolean value whether the user is allowed(True) or not(False)
	 */

	public static boolean isAllowedTo(long topicId) {
		User user = Security.getConnected();
		if (Users
				.isPermitted(
						user,
						"create relationships between entities/sub-entities/topics/tags",
						topicId, "topic"))
			return true;
		return false;
	}

	/**
	 * checks relation name for duplicate
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param relationName : name of the relation
	 * 
	 * @param relationNames : list of relation names in the organization
	 * 
	 * @return boolean value if duplicate(true) or not(false)
	 */
	public static boolean isDuplicate(String relationName, ArrayList<String> relationNames){
		for(int i = 0; i < relationNames.size(); i++){
			if(relationName.equals(relationNames.get(i)))
				return true;	
		}
		return false;
	}

}
