import java.util.Vector;


public class Announcer extends Thread{
	
	static Event event = null;
	private static long startTime = System.currentTimeMillis();
	
	private static int num_contestants;
	private static Integer random;
	
	private Vector<Integer> examScores = new Vector();
	private Vector<Integer> highScores = new Vector();
	
	
	public Announcer(Event event, int num_contestants){
		this.num_contestants = num_contestants;
		setName("Announcer");
		this.event = event;
	
	}
	
	public void msg(String m){
		
		System.out.println("[" + (System.currentTimeMillis()-startTime)+"] " + getName() + " " + m);
			
	}
	
	protected static final long age(){
		
		return System.currentTimeMillis() - startTime;
		
	}
	
	
	public void run(){
		
		alertGroup();
		getExamWinner();
		startShow();
		
	}

	private void startShow() {
		try {
			sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// creates the host for the show to start
		msg("Show will now start. Will introudce the winners!");
		Host host = new Host(startTime, event);
		host.start();
		
		
		event.notifyContestants();
		try {
			sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("Here comes the host and the gameshow will start shortly");
		event.wakeUpHost();
	}

	private void getExamWinner() {
		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// generate random scores for each contestant
		for(int i=0; i < num_contestants; i++){
			random = (int) (Math.random()*50);
			examScores.addElement(random);
		}
		
		// get the highScores from the examScores
		int[] examScoresArray = new int[examScores.size()];
		for(int i = 0; i < examScores.size(); i++)
			examScoresArray[i] = examScores.elementAt(i);
		
		int address = 0;
		for(int i=0; i < 4; i++){
			Integer hiScore = examScores.elementAt(0);
			address = 0;
			for(int j=i; j < examScores.size(); j++){
				if(hiScore < examScores.elementAt(j)){
					address = j;
					hiScore = examScores.elementAt(j);
				}		
			}
			examScores.removeElementAt(address);
			highScores.addElement(hiScore);
			if(highScores.size() >= 4 )
				break;
		}
		// go to event class to release contestants and assign if they won or not
		event.getWinner(getName(), examScoresArray, highScores);
	
	}

	private void alertGroup() {
	
		// notifies the contestant before giving them the grades
		event.notifyGroup(getName());
		msg("all contestants made groups and seated.");
		

	}
	

}
