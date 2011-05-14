package controllers;

import java.util.ArrayList;
import java.util.List;

import models.BannedUser;
import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import java.lang.*;
import java.lang.reflect.*;
import java.util.*;

import controllers.CRUD.ObjectType;

import play.data.binding.*;
import play.db.*;
import play.exceptions.*;
import play.i18n.*;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Topics extends CRUD {

	/**
	 * This method first checks if the user is allowed to tag the topic,
	 * searches for the tag in the global list of tags, if found => check if it
	 * already the topic had the same tag already or add the new one to the list
	 * if not => create a new tag, save it to db, add it to the list send
	 * notifications to followers, organizers and organization lead of the
	 * tagged topic
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S2
	 * 
	 * @param topicId
	 *            : the topic that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void tagTop(long topicId) {

	}

	public static void tagTopic(long topicId, String tag) {

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		User user = (User) Security.getConnected();
		Topic topic = (Topic) Topic.findById(topicId);
		MainEntity entity = topic.entity;

		if (!tag.equals("@@")) {

			if (topic == null)
				System.out.println("topic");
			else
				System.out.println("safe");
			if (user == null)
				System.out.println("user");
			else
				System.out.println("safe");
			// if (((ArrayList<User>)(topic.getOrganizer())) == null)
			// System.out.println("list"); else System.out.println("safe");
			if (!Users.isPermitted(user, "tag topics", entity.id, "entity")) {
				// user not allowed
				userNotAllowed = true;
			} else {
				for (int i = 0; i < globalListOfTags.size(); i++) {
					if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
							|| topic.entity.organization
									.equals(globalListOfTags.get(i).createdInOrganization)) {
						listOfTags.add(globalListOfTags.get(i));
					}
				}
				for (int i = 0; i < listOfTags.size(); i++) {
					if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
						if (!topic.tags.contains(listOfTags.get(i))) {
							topic.tags.add(listOfTags.get(i));

							for (int j = 0; j < listOfTags.get(i).followers
									.size(); j++) {
								Notifications.sendNotification(
										listOfTags.get(i).followers.get(j).id,
										topic.tags.get(i).getId(), "tag",
										"This topic has been tagged as " + tag);
							}
						} else {
							// tag already exists error message
							tagAlreadyExists = true;
						}
						tagExists = true;
					}
				}

				if (!tagExists) {
					Tag temp = new Tag(tag, topic.entity.organization);
					temp.save();
					topic.tags.add(temp);
				}

				if (!tagAlreadyExists) {
					for (int j = 0; j < topic.followers.size(); j++) {
						Notifications.sendNotification(
								topic.followers.get(j).id, topicId, "topic",
								"This topic has been tagged as " + tag);
					}

					for (int j = 0; j < topic.getOrganizer().size(); j++) {
						Notifications.sendNotification(topic.getOrganizer()
								.get(j).id, topicId, "topic",
								"This topic has been tagged as " + tag);
					}
					// Notifications.sendNotification(topic.followers, topicId,
					// "topic", "This topic has been tagged as " + tag);
					// Notifications.sendNotification(topic.getOrganizer(),
					// topicId,
					// "topic", "This topic has been tagged as " + tag);
					// List<User> list1 = new ArrayList<User>();
					// list1.add(topic.entity.organization.creator);
					Notifications.sendNotification(
							topic.entity.organization.creator.id, topicId,
							"topic", "This topic has been tagged as " + tag);
				}
			}

		}
		render(tagAlreadyExists, userNotAllowed, topic.tags, topicId);
	}

	/**
	 * This method adds topic2 to the list of related topics in topic
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param topic
	 *            : first topic to be related
	 * 
	 * @param topic2
	 *            : seconf topic to be related
	 */
	public static void relateTopic(Topic topic, Topic topic2) {
		// topic.relatedTopics.add(topic2);
	}

	/**
	 * 
	 * This method is responsible for posting an idea to a topic by a certain
	 * user
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param user
	 *            : the user who posted the idea
	 * 
	 * @param topic
	 *            : the topic which the idea belongs/added to
	 * 
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description of the idea
	 * 
	 * @param privacyLevel
	 *            : the level of privacy of the idea
	 * 
	 */

	public static void postIdea(Topic topic, String title, String description) {
		User user = Security.getConnected();
		Idea idea = new Idea(title, description, user, topic);
		idea.privacyLevel = topic.privacyLevel;
		render(title, description);
	}

	/**
	 * 
	 * This method reopens a closed topic, used after its plan gets deleted
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S22
	 * 
	 * @param topicId
	 *            : the id of the topic that to be reopened
	 */
	public static void reopen(long topicId) {

		Topic targetTopic = Topic.findById(topicId);

		targetTopic.openToEdit = true;
		targetTopic.save();
	}

	/**
	 * This Method returns a list of all closed topics
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S21
	 * 
	 * @return ArrayList<Topics>
	 */
	public static ArrayList<Topic> closedTopics() {
		// renamed from closedtopics to closedTopics;
		List closedTopics = (List) new ArrayList<Topic>();
		closedTopics = (List) Topic.find("openToEdit", false).fetch();
		return (ArrayList<Topic>) closedTopics;
	}

	// /**
	// *
	// * This method gets a list of followers for a certain topic
	// *
	// * @author Omar Faruki
	// *
	// * @story C2S29
	// *
	// * @param id
	// * : id of the topic
	// *
	// * @return void
	// */
	// public static void viewFollowers(long id) {
	// Topic topic = Topic.findById(id);
	// notFoundIfNull(topic);
	// List<User> follow = topic.followers;
	// render(follow);
	// }

	/**
	 * This Method sends a request to post on a topic for a user to the
	 * organizer
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S13
	 * 
	 * @param topicId
	 *            the id of the topic
	 * 
	 */

	public static void addRequest(long topicId) {
		User user = Security.getConnected();
		Topic topict = Topic.findById(topicId);
		topict.requestFromUserToPost(user.id);
	}

	/**
	 * This renders the RequestToPost.html to show the list of all topics where
	 * the user is not allowed to post within an organization
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S13
	 * 
	 * @param orgId
	 *            The organization id where the topics are
	 */

	public static void requestToPost(long orgId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(orgId);
		List<MainEntity> entities = org.entitiesList;
		List<Topic> topics = new ArrayList<Topic>();
		List<Topic> temp;
		List<UserRoleInOrganization> roles = UserRoleInOrganization.find(
				"byEnrolled", user).fetch();
		UserRoleInOrganization role;

		for (int i = 0; i < entities.size(); i++) {
			temp = entities.get(i).topicList;
			for (int j = 0; j < temp.size(); j++) {
				if (temp.get(j).creator.id != user.id && !user.isAdmin
						&& !temp.get(j).hasRequest(user.id)) {
					topics.add(temp.get(j));
				}
			}
		}
		for (int k = 0; k < roles.size(); k++) {
			role = roles.get(k);
			if (role.type.equalsIgnoreCase("topic")
					&& role.organization.id == org.id) {
				topics.remove(Topic.findById(role.entityTopicID));
			}
		}

		render(topics, user);
	}

	/**
	 * searches for unblocked users who are allowed to post in a certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S13
	 * 
	 * @param topicId
	 *            : a long id of the topic to search in
	 * 
	 * @return List<User>
	 */

	public static List<User> searchByTopic(long topicId) {

		Topic topic = Topic.findById(topicId);
		MainEntity entity = topic.entity;
		Organization org = entity.organization;

		ArrayList<User> searchList = (ArrayList) User.find("byIsAdmin", true)
				.fetch();
		if (!searchList.contains(org.creator))
			searchList.add(org.creator);
		// searchList.add(topic.creator);

		ArrayList<User> organizers = (ArrayList) topic.getOrganizer();
		for (int i = 0; i < organizers.size(); i++) {
			if (!searchList.contains(organizers.get(i)))
				searchList.add(organizers.get(i));
		}

		List<BannedUser> bannedUserTopic = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "topic", topicId).fetch(); // List of blocked users from
													// a
		// topic
		List<BannedUser> bannedUserEntity = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "entity", entity.id).fetch(); // list of blocked users
		// from an entity
		List<BannedUser> bannedUserOrg = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "organization", org.id).fetch(); // list of blocked user
		// from an organization
		List<BannedUser> bannedUserPlan = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"can post ideas to a Topic", "topic", topicId).fetch(); // list
																		// of
																		// users
																		// banned
																		// from
		// posting ideas in the
		// topic

		List<User> bannedUsers = new ArrayList<User>();
		List<User> user = new ArrayList<User>();
		List<BannedUser> bannedUser = new ArrayList<BannedUser>(); // list
		// appending
		// all the
		// previous
		// banneduser
		// lists
		bannedUser.addAll(bannedUserTopic);
		bannedUser.addAll(bannedUserEntity);
		bannedUser.addAll(bannedUserOrg);
		bannedUser.addAll(bannedUserPlan);

		for (int i = 0; i < bannedUser.size(); i++) {
			bannedUsers.add((bannedUser.get(i)).bannedUser);
		}

		List<UserRoleInOrganization> allUser = new ArrayList<UserRoleInOrganization>();
		// List<User> u = new ArrayList<User>();
		if ((org.privacyLevel == 0 || org.privacyLevel == 1)
				&& (topic.privacyLevel == 2)) {

			// allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
			// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
			// org, id, "idea developer", "topic");
			allUser = UserRoleInOrganization.find("byEntitytopicIdAndType",
					topicId, "topic").fetch();
			for (int i = 0; i < allUser.size(); i++) {
				if ((allUser.get(i).role.roleName).equals("idea developer")
						&& (allUser.get(i).organization).equals(org)
						&& !(user.contains(allUser.get(i)))) {
					user.add(allUser.get(i).enrolled);
				}
			}

			// for (int i = 0; i < allUser.size(); i++) {
			// user.add((allUser.get(i)).enrolled);
			// }

			for (int i = 0; i < bannedUsers.size(); i++) {
				if (user.contains(bannedUsers.get(i))) {
					user.remove(bannedUsers.get(i));

				}
			}
		} else {

			if ((org.privacyLevel == 0 || org.privacyLevel == 1)
					&& (topic.privacyLevel == 1)) {
				// allUser = (List<UserRoleInOrganization>)
				// UserRoleInOrganization
				// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
				// org, -1, "idea developer", "none");
				allUser = UserRoleInOrganization.find("byOrganization", org)
						.fetch();
				for (int i = 0; i < allUser.size(); i++) {
					if ((allUser.get(i).role.roleName).equals("idea developer")
							&& !(user.contains(allUser.get(i)))) {
						user.add(allUser.get(i).enrolled);
					}
				}

				// for (int i = 0; i < allUser.size(); i++) {
				// user.add((allUser.get(i)).enrolled);
				// }

				for (int i = 0; i < bannedUsers.size(); i++) {
					if (user.contains(bannedUsers.get(i))) {
						user.remove(bannedUsers.get(i));

					}
				}
			}

			else {
				if ((org.privacyLevel == 2) && (topic.privacyLevel == 2)) {
					// allUser = (List<UserRoleInOrganization>)
					// UserRoleInOrganization
					// .find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entitytopicId = ? and r.roleName like ? and and uro.type like ?",
					// org, id, "idea developer", "topic");

					// for (int i = 0; i < allUser.size(); i++) {
					// user.add((allUser.get(i)).enrolled);
					// }

					allUser = UserRoleInOrganization.find(
							"byEntitytopicIdAndType", topicId, "topic").fetch();
					for (int i = 0; i < allUser.size(); i++) {
						if ((allUser.get(i).role.roleName)
								.equals("idea developer")
								&& (allUser.get(i).organization).equals(org)
								&& !(user.contains(allUser.get(i)))) {
							user.add(allUser.get(i).enrolled);
						}
					}

					for (int i = 0; i < bannedUsers.size(); i++) {
						if (user.contains(bannedUsers.get(i))) {
							user.remove(bannedUsers.get(i));

						}
					}
				} else {
					if ((org.privacyLevel == 2) && (topic.privacyLevel == 1)) {

						user = User.findAll();

						for (int i = 0; i < bannedUsers.size(); i++) {
							if (user.contains(bannedUsers.get(i))) {
								user.remove(bannedUsers.get(i));

							}
						}
					}
				}
			}
		}

		for (int i = 0; i < user.size(); i++) {
			if (!searchList.contains(user.get(i)))
				searchList.add(user.get(i));
		}
		// searchList.addAll(user);
		int size = searchList.size();
		for (int i = 0; i < size; i++) {
			if (searchList.get(i).state.equals("d")
					|| searchList.get(i).state.equals("n")) {
				searchList.remove(i);
			}
		}

		return searchList;

	}

	/**
	 * This method closes a topic, return true if was successful, returns false
	 * if the there was ideas and doesn't close the topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S4
	 * 
	 * @param topicId
	 *            : the id of the topic to be closed
	 * 
	 */
	public static void closeTopic(String topicId) {

		System.out.println("entered closeTopic for topic:" + topicId);
		long topicIdLong = Long.parseLong(topicId);
		Topic targetTopic = Topic.findById(topicIdLong);
		User actor = User.findById(Security.getConnected().id);
		List<User> organizers = targetTopic.getOrganizer();
		List<User> followers = targetTopic.followers;

		String action = "close a topic and promote it to execution";
		String notificationDescription = "Topic " + targetTopic.title
				+ " has been closed and promoted to execution.";

		System.out.println("ideas count:" + targetTopic.getIdeas().size()
				+ " in topic" + targetTopic.getId() + "-" + targetTopic.id);

		// checks if topic is empty
		if (targetTopic.getIdeas().size() == 0) {
			System.out.println("Topic has no ideas");
			return;
		}

		// closing the topic to editing
		targetTopic.openToEdit = false;
		targetTopic.save();

		// Sending Notifications
		// send notification to organizers
		for (int i = 0; i < organizers.size(); i++) {
			Notifications.sendNotification(organizers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}
		// send notification to followers
		for (int i = 0; i < followers.size(); i++) {
			Notifications.sendNotification(followers.get(i).getId(),
					targetTopic.getId(), "Topic", notificationDescription);
		}
	}

	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * 
	 * @description This method checks for the Validation of the info inserted
	 *              in the Add form of a Topic and if they are valid the object
	 *              is created and saved.
	 * @throws Exception
	 * 
	 */
	public static void create(long entityId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		String message = "";
		Topic tmp = (Topic) object; // we temporarily save the object created by
									// the form in tmp to validate it before
									// saving
		System.out.println("create() entered");
		MainEntity topicEntity = MainEntity.findById(entityId);
		tmp.entity = topicEntity;
		User myUser = Security.getConnected();
		tmp.creator = myUser;

		/*
		 * if( !Users.isPermitted(tmp.creator , "post topics",
		 * topicEntity.getId(), "entity")) { message =
		 * "Sorry but you are not allowed to post topics in this entity";
		 * System.out.println(message); try {
		 * System.out.println("create() render try");
		 * render(request.controller.replace(".", "/") + "/blank.html",
		 * entityId, type, tmp.title, tmp.entity, tmp.description, message,
		 * entityId); } catch (TemplateNotFoundException e) {
		 * System.out.println("create() render catch");
		 * render("CRUD/blank.html", type, entityId); }}
		 */
		System.out
				.println("the idea beforew validation check" + tmp.toString());

		if (tmp.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, entityId);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, message, entityId);
			}
		}

		Organization topicOrganization = topicEntity.organization;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		if (!(topicEntity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			tmp.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations",
					topicEntity, topicOrganization).fetch();

		if (validation.hasErrors()) {
			if (tmp.title.equals("")) {
				message = "A Topic must have a title";
			} else if (tmp.description.equals("")) {
				message = "A Topic must have a description";

			}

			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, tmp.title, tmp.entity, tmp.description,
						tmp.followers, tmp.tags, message);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, entityId);
			}
		}

		if (tmp.privacyLevel < 1 || tmp.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, tmp.title, tmp.entity, tmp.description,
						tmp.followers, tmp.tags, message);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, entityId);
			}

		}

		System.out.println("create() about to save object");
		object._save();
		System.out.println("create() object saved");
		tmp = (Topic) object;
		Calendar cal = new GregorianCalendar();
		// Logs.addLog( tmp.creator, "add", "Task", tmp.id,
		// tmp.entity.organization, cal.getTime() );
		String message2 = tmp.creator.username + " has Created the topic "
				+ tmp.title + " in " + tmp.entity;
		if (tmp.followers != null) {
			for (int i = 0; i < tmp.followers.size(); i++)
				Notifications.sendNotification(tmp.followers.get(i).getId(),
						tmp.id, "Topic", "A new Topic: '" + tmp.title
								+ "' has been added in entity '"
								+ tmp.entity.name + "'");
		}

		List<User> users = Users.getEntityOrganizers(tmp.entity);
		users.add(tmp.entity.organization.creator);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, tmp.id, "Topic",
					"A new Topic: '" + tmp.title
							+ "' has been added in entity '" + tmp.entity.name
							+ "'");

		// tmp.init();
		flash.success(Messages.get("crud.created", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			System.out
					.println("create() done will redirect to topics/show?topicId "
							+ message2);
			redirect("/topics/show?topicId=" + ((Topic) object).getId());

			// redirect("/topics/show?" + ((Topic) object).getId(), message2);
			// redirect( "/storys/liststoriesinproject?projectId=" +
			// tmp.taskStory.componentID.project.id + "&storyId=" +
			// tmp.taskStory.id );
		}
		if (params.get("_saveAndAddAnother") != null) {
			System.out
					.println("create() done will redirect to blank.html to add another "
							+ message2);
			render(request.controller.replace(".", "/") + "/blank.html",
					message2, entityId);

			/*
			 * render(request.controller.replace(".", "/") + "/blank.html",
			 * entityId, type, tmp.title, tmp.entity, tmp.description,
			 * tmp.followers, tmp.tags, message);
			 */
		}
		System.out
				.println("create() done will redirect to show.html to show created"
						+ message2);
		redirect(request.controller + ".view", ((Topic) object).getId(),
				message2);
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param entityId
	 *            : id of the entity the topic is in
	 * 
	 * @param userId
	 *            : id of the current user
	 * 
	 * @description This method renders the form for creating a topic in the
	 *              entity
	 * 
	 * @throws Exception
	 * 
	 */
	public static void blank(long entityId, long userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		MainEntity topicEntity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		System.out.println("blank() entered entity " + entityId + " and user "
				+ user.toString());
		// List<User> followers = entity.followers;
		// ArrayList<MainEntity> entitiesFollowed = (ArrayList<MainEntity>)
		// user.followingEntities;//display some of them on the side for quick
		// navigation

		// handle permissions, depending on the privacyLevel the user will be
		// directed to a different page

		try {
			System.out.println("blank() done about to render");
			render(type, entityId, user /* , followers, entitiesFollowed */);

		} catch (TemplateNotFoundException e) {
			System.out
					.println("blank() done with exception about to render CRUD/blank.html");
			render("CRUD/blank.html", type, entityId);
		}

	}

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for editing and viewing a topic
	 * 
	 * @throws Exception
	 * 
	 */
	public static void show(String topicId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicId);
		notFoundIfNull(object);
		Topic tmp = (Topic) object;
		System.out.println(tmp.title);
		List<Tag> tags = tmp.tags;
		User creator = tmp.creator;
		List<User> followers = tmp.followers;
		List<Idea> ideas = tmp.ideas;
		List<Comment> comments = tmp.commentsOn;
		MainEntity entity = tmp.entity;
		Plan plan = tmp.plan;
		boolean openToEdit = tmp.openToEdit;
		int privacyLevel = tmp.privacyLevel;
		String deleteMessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = tmp.isDeletable();
		int canClose = 0;
		int canPlan = 0;
		long topicIdLong = Long.parseLong(topicId);
		User actor = Security.getConnected();
		String actionClose = "close a topic and promote it to execution";
		String actionPlan = "create an action plan to execute an idea";
		Topic targetTopic = Topic.findById(topicIdLong);
		int allowed = 0;
		if (Users
				.isPermitted(
						actor,
						"Accept/Reject requests to post in a private topic in entities he/she manages",
						tmp.id, "topic"))
			allowed = 1;
		// Note isPermitted has a bug here!
		boolean canPost = Users.isPermitted(Security.getConnected(),
				"can post ideas to a Topic", tmp.id, "topic");

		if (Users.isPermitted(actor, actionClose, topicIdLong, "topic")) {
			canClose = 1;
		}

		if (Users.isPermitted(actor, actionPlan, topicIdLong, "topic")) {
			canPlan = 1;
		}

		int permission = 1;

		if (!Users.isPermitted(actor, "post topics", entity.id, "entity")) {
			permission = 0;
		}
		boolean isIdeaDeveloper = false;
		List<UserRoleInOrganization> roles = UserRoleInOrganization.find(
				"byEnrolled", actor).fetch();
		for (int k = 0; k < roles.size(); k++) {
			if (roles.get(k).type.equalsIgnoreCase("topic")
					&& roles.get(k).entityTopicID == topicIdLong) {
				isIdeaDeveloper = true;
				break;
			}
		}
		boolean isMemeber = Users.getEnrolledUsers(
				targetTopic.entity.organization).contains(actor);
		boolean pending = targetTopic.hasRequest(actor.id);
		boolean canNotPost = (targetTopic.creator.id != actor.id
				&& !actor.isAdmin && !pending)
				&& !isIdeaDeveloper && isMemeber;
		boolean follower = actor.topicsIFollow.contains(targetTopic);
		try {
			render(type, object, tags, creator, followers, ideas, comments,
					entity, plan, openToEdit, privacyLevel, deleteMessage,
					deletable, topicIdLong, canClose, canPlan, targetTopic,
					allowed, permission, topicId, canPost, canNotPost, pending,
					follower);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object, topicId);
		}
	}

	/**
	 * Topic view method
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we want to show
	 * 
	 * @description This method renders the form for viewing a topic
	 * 
	 * @throws Exception
	 * 
	 */
	public static void view(String topicId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(topicId);
		notFoundIfNull(object);
		System.out.println("entered view() for topic " + topicId);
		Topic tmp = (Topic) object;
		System.out.println(tmp.title);
		System.out.println(tmp.description);
		List<Tag> tags = tmp.tags;
		User creator = tmp.creator;
		List<User> followers = tmp.followers;
		List<Idea> ideas = tmp.ideas;
		List<Comment> comments = tmp.commentsOn;
		MainEntity entity = tmp.entity;
		Plan plan = tmp.plan;
		boolean openToEdit = tmp.openToEdit;
		int privacyLevel = tmp.privacyLevel;
		String deleteMessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = tmp.isDeletable();

		try {
			System.out.println("view() done, about to render");
			render(type, object, tags, creator, followers, ideas, comments,
					entity, plan, openToEdit, privacyLevel, deleteMessage,
					deletable, topicId);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("view() done with exception, rendering to CRUD/show.html");
			render("/topics/view.html", type, object, tags, creator, followers,
					ideas, comments, entity, plan, openToEdit, privacyLevel,
					deleteMessage, deletable, topicId);
		}
	}

	/*
	 * public static void edit(String topicId) {
	 * 
	 * ObjectType type = ObjectType.get(getControllerClass());
	 * notFoundIfNull(type); Model object = type.findById(topicId);
	 * notFoundIfNull(object); System.out.println("entered edit() for topic " +
	 * topicId); Topic tmp = (Topic) object;
	 * flash.success(Messages.get("crud.edit", type.modelName, ((Topic)
	 * object).getId())); System.out.println("About to redirect from edit()");
	 * redirect("/topics/show?topicId=" + ((Topic) object).getId(), tmp); }
	 */

	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param page
	 *            : page of the list we are in
	 * 
	 * @param search
	 *            : search string
	 * 
	 * @param searchFields
	 *            : the fields we want to search
	 * 
	 * @param orderBy
	 *            : criteria to order list by
	 * 
	 * @param order
	 *            : the order of the list
	 * 
	 * @description This method renders the list of topics, with search and sort
	 *              options
	 * 
	 * @throws Exception
	 * 
	 */
	public static void list(int page, String search, String searchFields,
			String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		System.out.println("list() entered ");
		List<Model> objects = type.findPage(page, search, searchFields,
				orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields,
				(String) request.args.get("where"));
		Long totalCount = type.count(null, null,
				(String) request.args.get("where"));
		try {
			System.out.println("list() done, will render ");
			System.out.println("list() done, will render " + type.toString());
			System.out
					.println("list() done, will render " + objects.toString());
			System.out.println("list() done, will render " + count);
			System.out.println("list() done, will render " + totalCount);
			System.out.println("list() done, will render " + page);
			System.out.println("list() done, will render " + orderBy);
			System.out.println("list() done, will render " + order);
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			System.out
					.println("list() done with exceptions, will render CRUD/list.html ");
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * closedTopicsList
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S21
	 * 
	 * @param page
	 *            : page of the list we are in
	 * 
	 * @param search
	 *            : search string
	 * 
	 * @param searchFields
	 *            : the fields we want to search
	 * 
	 * @param orderBy
	 *            : criteria to order list by
	 * 
	 * @param order
	 *            : the order of the list
	 * 
	 * @description This method renders the list of closed topics, with search
	 *              and sort options
	 * 
	 * @throws Exception
	 * 
	 */
	public static void closedTopicsList(int page, String search,
			String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Topic> openTopics = Topic.find("openToEdit", false).fetch();
		Long totalCount = (long) openTopics.size();
		Long count = (long) openTopics.size();
		try {
			render(type, openTopics, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, openTopics, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S1
	 * 
	 * @param topicId
	 *            : id of the topic we're in
	 * 
	 * @description This method renders the form for editing a topic and saving
	 *              it
	 * 
	 * @throws Exception
	 * 
	 */
	public static void save(String topicId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		System.out.println(topicId);
		Model object = Topic.findById(Long.parseLong(topicId));
		notFoundIfNull(object);
		System.out.println("entered save() for " + topicId);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic tmp = (Topic) object; // we temporarily save the object edited in
									// the form in tmp to validate it before
									// saving
		MainEntity entity = tmp.entity;
		User myUser = Security.getConnected();
		tmp.creator = myUser;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		Organization topicOrganization = entity.organization;
		if (!(entity.followers.size() == 0 || topicOrganization.followers
				.size() == 0))
			tmp.followers = User.find(
					"byFollowingEntitiesAndFollowingOrganizations", entity,
					topicOrganization).fetch();
		String message = "";

		if (validation.hasErrors()) {
			if (tmp.description.equals("")) {
				message = "A Topic must have a description";

			}

			/*
			 * else if( !Users.isPermitted(myUser, "edit topics",
			 * topicEntity.getId(), "entity")) { message =
			 * "Sorry but you are not allowed to edit topics in this entity"; }
			 */
			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, tmp.title, tmp.entity, tmp.description,
						tmp.followers, tmp.tags, message, object, topicId);
			} catch (TemplateNotFoundException e) {
				render("CRUD/view.html", type, topicId);
			}
		}

		if (tmp.privacyLevel < 1 || tmp.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/view.html",
						entity, type, tmp.title, tmp.entity, tmp.description,
						tmp.followers, tmp.tags, message, object, topicId);
			} catch (TemplateNotFoundException e) {
				render("CRUD/view.html", type, topicId);
			}

		}

		System.out.println("about to save() topic");
		object._save();
		Calendar cal = new GregorianCalendar();
		// Logs.addLog( myUser, "add", "Task", tmp.id, tmp.entity.organization,
		// cal.getTime() );
		// String message3 = myUser.username + " has editted the topic " +
		List<User> users = Users.getEntityOrganizers(tmp.entity);
		if (!users.contains(tmp.entity.organization.creator))
			users.add(tmp.entity.organization.creator);
		for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(users.get(i).id, tmp.id, "Topic",
					"User: '" + myUser.firstName + "' has edited topic  '"
							+ tmp.title + "' in entity '" + tmp.entity.name
							+ "'");

		System.out.println("save() done, not redirected yet");

		flash.success(Messages.get("crud.saved", type.modelName,
				((Topic) object).getId()));
		if (params.get("_save") != null) {
			redirect("/topics/show?topicId=" + ((Topic) object).getId());
			System.out
					.println("save() done, redirected to topics/show?topicId");

		}
		redirect(request.controller + ".view", ((Topic) object).getId());
		System.out.println("save() done, redirected to default view.html");
	}

	/**
	 * The method that allows a user to follow a certain topic
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param topicId
	 *            : The id of the topic that the user wants to follow
	 * 
	 */
	public static void followTopic(long topicId) {
		User user = Security.getConnected();
		Topic t = Topic.findById(topicId);
		if (t.followers.contains(user))
			System.out.println("You are already a follower");
		else if (Users.isPermitted(user,
				"can follow organization/entities/topics", topicId, "topic")) {
			t.followers.add(user);
			t.save();
			user.topicsIFollow.add(t);
			user.save();
			redirect(request.controller + ".show", t.id,
					"You are now a follower");
		} else
			System.out.println("Sorry! Action cannot be performed");
	}

	/**
	 * The method that renders the page for viewing the followers of a topic
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param topicId
	 *            : The id of the topic that the user wants to view its
	 *            followers
	 * 
	 * @param f
	 *            : The String that is used as a variable for checking
	 * 
	 */
	public static void viewFollowers(long topicId, String f) {
		Topic topic = Topic.findById(topicId);
		if (f.equals("true"))
			followTopic(topicId);
		render(topic);
	}
}
