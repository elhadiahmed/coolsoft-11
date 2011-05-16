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
public class TagRelationships extends CRUD {

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
	 *            : id of the first Tag to be related
	 * 
	 * @param destinationId
	 *            : id of the second Tag to be related
	 */
	public static void createRelationship(String name, long sourceId,
			long destinationId) {
		
		Tag source = Tag.findById(sourceId);
		Tag destination = Topic.findById(destinationId);

		TagRelationship relation = new TagRelationship(name, source,
				destination);
		relation.save();
		relation.source.relationsSource.add(relation);
		relation.destination.relationsDestination.add(relation);		
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
		TagRelationship relation = TagRelationship
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
		TagRelationship relation = TagRelationship.findById(relationId);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		relation.delete();
	}

	/**
	 * checks if the user is able to create relationships or not
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param tagId
	 *            : the id of the tag that the user belongs to
	 * 
	 * @return boolean value whether the user is allowed(True) or not(False)
	 */

	public static boolean isAllowedTo(long tagId) {
		User user = Security.getConnected();
		if (Users
				.isPermitted(
						user,
						"create relationships between entities/sub-entities/topics/tags",
						tagId, "entity"))
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
	 * @param relationNamesInOrganization : list of relation names in the organization
	 * 
	 * @return boolean value if duplicate(true) or not(false)
	 */
	public static boolean isDuplicate(String relationName, ArrayList<String> relationNamesInOrganization){
		for(int i = 0; i < relationNamesInOrganization.size(); i++){
			if(relationName.equals(relationNamesInOrganization.get(i)))
				return true;	
		}
		return false;
	}

}
