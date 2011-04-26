package models;


import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	@Required
	@Email
	public String email;
	@Required
	@MaxSize(10)
	public String username;
	@Required
	@MaxSize(25)
	@MinSize(4)
	public String password;
	@Required
	public String firstName;
	public String lastName;
	public String country;
	public Date dateofBirth;
	public int communityContributionCounter;
	// public List<Role> roles;
	// added to know whether a user is an Admin or not
	public boolean isAdmin;
	//public boolean isDeleted;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.PERSIST)
	public List<Topic> topicsCreated;

	@ManyToMany
	public List<Organization> enrolled;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	public List<Organization> createdOrganization;

	@ManyToMany(mappedBy = "organizers", cascade = CascadeType.PERSIST)
	public List<Topic> topicsIOrganize;

	/**
	 * Added by Alia, but whoever is responsible for it please check the cascade
	 * etc.
	 */

	@ManyToMany(mappedBy = "followers", cascade = CascadeType.PERSIST)
	public List<Topic> topicsIFollow;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	public List<Idea> ideasCreated;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public List<Item> itemsAssigned;

	@OneToMany(mappedBy = "user")
	public List<NotificationProfile> notificationProfiles;
	@OneToMany(mappedBy = "directedTo")
	public List<Notification> notifications;
	@ManyToMany
	List<MainEntity> followingEntities;

	// related to sprint 2
	@ManyToMany
	public List<Tag> followingTags;

	@ManyToMany
	public List<Organization> followingOrganizations;
	@ManyToMany
	public List<MainEntity> entitiesIOrganize;

	// List<Request> requests;
	// List<Comment> commentsPosted;
	// List<LinkDuplicates> linkDuplicates;

	// List<RequestToJoin> requestsToJoin;
	// List<RequestOfRelationship> requestRelationship;
	// List<TopicInvitation> topicInvitations;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<Invitation> invitation;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	public List<AssignRequest> sentAssignRequests;

	@OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
	public List<AssignRequest> receivedAssignRequests;

	@OneToMany(mappedBy = "bannedUser", cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;

	@OneToMany(mappedBy = "enrolled", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRolesInOrganization;

	@OneToMany(mappedBy = "madeBy", cascade = CascadeType.PERSIST)
	public List<Plan> planscreated;

	public User(String email, String password, String firstName,
			String lastName, String username, int communityContributionCounter,
			Date dateofBirth, String country) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;
		// this.roles = new ArrayList<Role>();
		this.ideasCreated = new ArrayList<Idea>();
		this.itemsAssigned = new ArrayList<Item>();
		this.topicsIOrganize = new ArrayList<Topic>();
		this.topicsCreated = new ArrayList<Topic>();
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.sentAssignRequests = new ArrayList<AssignRequest>();
		this.receivedAssignRequests = new ArrayList<AssignRequest>();
		notificationProfiles = new ArrayList<NotificationProfile>();
		notifications = new ArrayList<Notification>();
		bannedUsers = new ArrayList<BannedUser>();
		//added
		userRolesInOrganization = new ArrayList<UserRoleInOrganization>();
		invitation = new ArrayList<Invitation>();
		this.enrolled = new ArrayList<Organization>();
		this.createdOrganization = new ArrayList<Organization>();
		followingOrganizations = new ArrayList<Organization>();
		planscreated = new ArrayList<Plan>();

		// requests=new ArrayList<Request>();
		// commentsPosted = new ArrayList<Comment>();
		// linkDuplicates = new ArrayList<LinkDuplicates>();

		// requestsToJoin = new ArrayList<RequestToJoin>();
		// requestRelationship = new ArrayList<RequestOfRelationship>();
		// topicInvitations = new ArrayList<TopicInvitation>();

	}
	
	
	
	
	public void addInvitation(String email,String role,Organization organization,
	        MainEntity entity,Topic topic,User sender,String comment) {
	    	 
	    	Invitation invite = new Invitation(comment,email,entity,organization,role,
	        		sender,topic).save();
	          this.save();
	    }
	
	
	
	/**
	 * 
	 * This Method adds a volunteer request sent by the user to work on a certain item in a plan to the list of volunteer requests sent by the user given the volunteer request
	 * 
	 * @author 	salma.qayed
	 * 
	 * @story 	C5S10
	 * 
	 * @param 	volunteerRequest 	: the VolunteerRequest that needs to be added to the list of volunteer requests of the user
	 * 
	 * @return	void
	 */
	
	public void addVolunteerRequest(VolunteerRequest volunteerRequest){
		volunteerRequests.add(volunteerRequest);
		
	}
	
	/**
	 * 
	 * This Method adds an assign request sent by the user to another user assigning him to a certain item in a plan to the list of assign requests sent by the user given the assign request
	 * 
	 * @author 	salma.qayed
	 * 
	 * @story 	C5S4
	 * 
	 * @param 	sentAssignRequest 	: the AssignRequest that needs to be added to the list of assign requests sent by the user
	 * 
	 * @return	void
	 */
	public void addSentAssignRequest(AssignRequest sentAssignRequest) {
		sentAssignRequests.add(sentAssignRequest);
	}
	
	/**
	 * 
	 * This Method adds an assign request received by the user to work on a certain item in a plan to the list of assign requests received by the user given the assign request
	 * 
	 * @author 	salma.qayed
	 * 
	 * @story 	C5S4
	 * 
	 * @param 	receivedAssignRequest 	: the AssignRequest that needs to be added to the list of assign requests received by the user
	 * 
	 * @return	void
	 */
	public void addReceivedAssignRequest(AssignRequest receivedAssignRequest) {
		sentAssignRequests.add(receivedAssignRequest);
	}

	/**
	 * This Method removes a tag from the followers list
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param t
	 *            : the tag that the user is following
	 * 
	 * @return void
	 */

	public void unfollow(Tag t) {
		followingTags.remove(t);
	}

	/**
	 * This Method removes a topic from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param t
	 *            : the topic that the user is following
	 * 
	 * @return void
	 */

	public void unfollow(Topic t) {
		topicsIFollow.remove(t);
	}

	/**
	 * This Method removes a mainEntity from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param m
	 *            : the MainEntity that the user is following
	 * 
	 * @return void
	 */
	public void unfollow(MainEntity m) {
		followingEntities.remove(m);
	}

	/**
	 * This Method removes a organization from the followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param o
	 *            : the Organization that the user is following
	 * 
	 * @return void
	 */
	public void unfollow(Organization o) {
		followingOrganizations.remove(o);
	}
}
