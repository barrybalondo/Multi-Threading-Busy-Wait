import java.util.Vector;


public class Event {

	private static long startTime = System.currentTimeMillis();
	
	private int num_contestants = 12;
	private int room_capacity = 4;
	
	// these are used to group the
	// contestants and block in group
	private int groupCount;
	private int groupAvailable;
	private int groupAssignment = 0;
	private int contestantCount;
	private Object room;

	private boolean groupsFormed = false;
	Vector<Object> waitList = new Vector();
	
	// used to determine winners
	// based on the scores
	private int i;
	private int winnersPicked = 4;
	int[] examScores;
	Vector<Integer> highScores;
	
	// used to store contestants taking the exam in FCSFS
	Vector<Object> examTaking = new Vector();
	
	// used to lock the Host while announcer introduces the contestants
	Object hostWait = new Object();
		
	// used for the game
	private static int numRounds;
	private static int numQuestions;
	private static int questionValues ;
	private static double rightPercent;
	
	public boolean gameShowIsOn = true;	
	public boolean answered = false;
	private int scoreGot = 0;
	private static int answerCount = 0;
	Object waitForAnswer = new Object();
	Object waitForHost = new Object();
	
	
	
	public Event(int num_contestants, int room_capacity, int numRounds, int numQuestions, double rightPercent){
		this.num_contestants = num_contestants;
		this.room_capacity = room_capacity;
		this.numRounds = numRounds;
		this.numQuestions = numQuestions;
		this.rightPercent = rightPercent;
		
		// calaculates the room avaialble
		groupAvailable = num_contestants/room_capacity;	
		room = new Object();
		groupCount = 0;
		contestantCount = 0;
		
	}
	

	public void msg(String id, String m){
		
		System.out.println("[" + (System.currentTimeMillis()-startTime)+"] " + id + " " + m);
			
	}
	
	
	protected static final long age(){
		
		return System.currentTimeMillis() - startTime;	
		
	}

	// contestants enter the room and group up
	// returns false if they can't make the group
	// annd they go home
	public boolean enterRoom(String id){
		if(canEnterRoom(id, room)){				
			synchronized(room){
				try {		
					room.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}		
			return true;
		}
		else
			return false;	
	}

	
	// used to determine if a group has been made and block them
	private synchronized boolean canEnterRoom(String id, Object roomToLock) {
		
		groupCount++; // counter for groups
		contestantCount++; // counter for number of contestants who tried to enter
		if(contestantCount == num_contestants){
			groupsFormed = true;
			notifyAll();
		}
		
		if(groupAssignment > groupAvailable -1 ) // kicks contestants that cannot fill a group
			return false;
		
		// if room_capacity is reached, resets group count
		// and adds the object to a vector waiting to be 
		// notified, and creates a new object for the new group.
		if(groupCount == room_capacity){
			msg(id, "enters, last member of group " + (groupAssignment+1) + ".");
			waitList.add(roomToLock);	// places group waiting in a vector
			notifyAll(); // to notify announcer
			groupCount = 0;	
			groupAssignment++;
		    roomToLock = new Object();
		}
		else
			msg(id, "enters.");
		return true;
		
	}

	// used by announcer to notify groups that are waiting
	public synchronized void notifyGroup(String id){
		int counter = 0;
		while(!groupsFormed){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			if(waitList.size() > 0){			
				msg(id, "notifies group " +  ++counter + " ready to enter room");
				synchronized(waitList.elementAt(0)){
					waitList.elementAt(0).notifyAll();
				}
				waitList.removeElementAt(0);
			}		
		}
	}

	// used by contestants if they are taking the exam
	public int takeExam(String id) {
		
		// contestants are now waiting for their exam scores
		// and are waiting in a vector.
		Object convey = new Object();
		synchronized(convey){
			examTaking.addElement(convey);
			while(true)
				try { convey.wait(); break; } 
				catch (InterruptedException e) { continue; }
		}
		return examScores[i++];
		
	}

	// notify contestants exam is done and will give give 
	// them generated scores made by the announcer
	// random scores are done in the announcer thread
	public synchronized void getWinner(String id, int[] examScores, Vector<Integer> highScores) {
		msg(id, "will now generate random scores for each contestant");
		
		// set the generated scores by announcers to the local 
		// one in events so that contestants can access it
		this.examScores = examScores;
		this.highScores = highScores;
		
		// removed in a first come first serve basis.
		if(examTaking.size() > 0 ){
			while(examTaking.size() > 0 ){
				synchronized(examTaking.elementAt(0)){
					examTaking.elementAt(0).notify();
				}
				examTaking.removeElementAt(0);
			}	
		}
		
	}

	public synchronized boolean checkWin(String name, int score) {	
		// check to see if it matches the highs scores that is already
		// calculated and if yes, return true to continue the game
		// else false and stop the thread.
		boolean status = false;
		for(int i = 0; i < highScores.size(); i++)
			if(score == highScores.elementAt(i))	
				status = true;
		if(status && winnersPicked > 0){
			winnersPicked--;
			return true;
		}
		else return false;
	}

	// game starts here
	public synchronized void startGameContestants(String id) {	
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg(id, "Hi, thanks for having me.");	
		
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void notifyContestants() {
		// notify waiting contestants that are waiting 
		// for the game to start
		notifyAll();
	}

	// used by host, waits until announcer notifies 
	// him in wakeUpHost()
	public void startToHost(String id) {
		
		synchronized(hostWait){
			try {
				hostWait.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		msg(id, "Hi I'm the host, Let's start the show.");
	}

	
	//  used by announcer to wake up host
	public void wakeUpHost() {
		
		// notifies the host trapped in this object
		synchronized(hostWait){
			hostWait.notify();
		}
		
	}

	// host will now start answering questions
	public void askQuestions(String id) {
		int roundNum = 0;
		int questionNum = 0;
		// notify waiting contestants that are waiting 
		// for the game to start
		synchronized(this){
			notifyAll();
		}
		
		// games of rounds done here
		while(numRounds > 0){
			msg(id, "Round " + ++roundNum + ".");
			int question = numQuestions;
			while(question > 0){
				scoreGot = 0;
				msg(id, "has asked a question number " + ++questionNum +".");
				question--;
				synchronized(waitForAnswer){
					try {
						waitForAnswer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
						
				// calculate the score if you win get 100
				// if you lose -100
				double random = (Math.random()*100); 
				if(random > rightPercent){
					scoreGot = 100;
				}
				else
					scoreGot = -100;
				
				synchronized(waitForHost){
					waitForHost.notifyAll();
				}		
			
			}				
				answered = false;
				numRounds--;
				questionNum = 0;
						
			
		}
		
		gameShowIsOn = false;
	}

	// first contestants that wakes up 
	// enters this thread and answers the question
	// 
	public int answerGameQuestion(String id) {
			
		// bug is in here. i'm having a hard time
		// figuring out how to signal host properly
		// and get the scores proper for each
		// questions preceding.
		synchronized(waitForHost){
			
			synchronized(waitForAnswer){
				answerCount++;
				if(answerCount == 4){		
					waitForAnswer.notify();
					answerCount = 0;
				}
				
			}
						
			try {
				waitForHost.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!answered){
				msg(id, "first to answer gets " + scoreGot + ".");		
				answered = true;
				return scoreGot;
			}
			else
				return 0;	
		}
	} 
		
	
	
	
}
	
	

