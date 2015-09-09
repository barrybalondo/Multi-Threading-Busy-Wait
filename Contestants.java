
public class Contestants extends Thread{
	
	private static Event event = null;
	
	private static int random;
	private static long startTime = System.currentTimeMillis();
	private int examScore = 0;
	private int gameScore = 0;
	private static boolean gotIn;

	public Contestants(int id, Event event){
		
		setName("Contestant-" + id);
		this.event = event;
		
	} // constructor
	
	
	public void msg(String m){
		
		System.out.println("[" + (System.currentTimeMillis()-startTime)+"] " + getName() + " " + m);
			
	}
	
	protected static final long age(){
		
		return System.currentTimeMillis() - startTime;
		
	}
	
	public void run(){
		// flowchart of the story
		formGroup();
		takeExam();
		startGame();
	}


	private void startGame() {
		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		event.startGameContestants(getName());
	
		while(event.gameShowIsOn){
			random = (int) (Math.random()*20 + 50);
			try {
				sleep(random);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gameScore = gameScore + event.answerGameQuestion(getName());
		}
		
	}


	@SuppressWarnings("deprecation")
	private void formGroup() {
		random = (int) (Math.random()*10+1);
		try {
			sleep(random);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gotIn = event.enterRoom(getName());
		// kicks contestant if it doesn't make the group here
		if(!gotIn){
			msg("insuffecient to fill group, leaves");
			this.stop();
		}
		
	}



	@SuppressWarnings("deprecation")
	private void takeExam() {
		// gets the exam score and exits if it doesn't get highest four
		examScore = event.takeExam(getName());	
		if(event.checkWin(getName(), examScore))
			msg("You scored " + examScore + " on the exam and is 1 out of the 4 high scorers");
		else{
			msg("You scored " + examScore + ", better luck next time.");
			stop();
		}
	}


	
	
	
	

}
