import java.awt.*;
import java.net.URL;
import java.awt.Dimension;
import java.awt.Panel;



public class Message implements MessageAction
{
	//insert class definition here
	
	public Message()
	{
	}


	public Message(String alert, String summary, String URL, 
					int priority, MessageAction action) {
		setMessageAlert(alert);
		setMessageURL(URL);
		setMessageLevel(priority);
		setMessageSummary(summary);
		setMessageAction(action);
	}

	public Message(String alert, String summary, String URL, int priority) {
		setMessageAlert(alert);
		setMessageURL(URL);
		setMessageLevel(priority);
		setMessageSummary(summary);
	}
	
	/** create a message and put it on the message board
	*	param mode - MANUAL, AUTOMATIC, TRACE 
	*	 param alert - short headline message for user
	*	param summary - longer descriptive message
	*	URL - URL to full description and methods for problem
	*	priority - low number = high priority < 34 High, <67 medium 67+ low/informative
	*/
	
	static public Message create(int mode, String alert, String summary, String URL, int priority) {
		if (Mode.is(mode)) {
			Message m = new Message(alert, summary,  URL,  priority);
			MessageLine.theMessenger.addMessage(m);
			return m;
		}
		return null;
	}

	static public Message create(int mode, String alert, String summary, String URL, int priority, MessageAction action) {
		if (Mode.is(mode)) {
			Message m = new Message(alert, summary,  URL,  priority, action);
			MessageLine.theMessenger.addMessage(m);
			return m;
		}
		return null;
	}
	
	public void setMessageAlert(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageLevel(int messageLevel) {
		this.messageLevel = messageLevel;
	}

	public int getMessageLevel() {
		return messageLevel;
	}

	public void setMessageSummary(String messageSummary) {
		this.messageSummary = messageSummary;
	}

	public String getMessageSummary() {
		return messageSummary;
	}

	public void setMessageAction(MessageAction messageAction) {
		this.messageAction = messageAction;
	}

	public MessageAction getMessageAction() {
		return messageAction;
	}

	public void setMessageAlert(boolean messageAlert) {
		this.messageAlert = messageAlert;
	}

	public boolean isMessageAlert() {
		return messageAlert;
	}

	public void setMessageIcon(java.net.URL messageIcon) {
		this.messageIcon = messageIcon;
	}

	public java.net.URL getMessageIcon() {
		return messageIcon;
	}

	/** 
     * Returns the minimum size of this container.  
     * @see #getPreferredSize
     */
  
  	protected String messageTitle="Message";
	protected int messageLevel=1;
	protected Message messageList;
	protected String messageSummary="";
	protected MessageAction messageAction=this;
	protected boolean messageAlert=false;
	protected java.net.URL messageIcon=null;

	public void setActionLabel(String actionLabel) {
		this.actionLabel = actionLabel;
	}

	public String getActionLabel() {
		return actionLabel;
	}
	protected String actionLabel;


	public void setKindOfMessage(String kindOfMessage) {
		this.kindOfMessage = kindOfMessage;
	}

	public String getKindOfMessage() {
		return kindOfMessage;
	}
	protected String kindOfMessage;
	
	public void addSeeAlso(String url) {
		
	}

	public void setMessageURL(String messageURL) {
		this.messageURL = messageURL;
	}

	public String getMessageURL() {
		return messageURL;
	}
	protected String messageURL="Help";
	
	public void go(Message m) {
		m.go(this);
	}
	
	public void addMessage() {
		MessageLine.theMessenger.addMessage(this);
	}
	
	public static void removeMessages() {
		MessageLine.theMessenger.removeMessages();
	}
}
