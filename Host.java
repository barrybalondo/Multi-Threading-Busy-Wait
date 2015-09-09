
public class Host extends Thread{
	
	private Event event = null;
	public static long startTime = System.currentTimeMillis();
	
	public Host(long startTime, Event event){
		
		setName("Host");
		this.startTime = startTime;
		this.event = event;
	}
	
	public void msg(String m){
		
		System.out.println("[" + (System.currentTimeMillis()-startTime)+"] " + getName() + " " + m);
			
	}
	
	protected static final long age(){
		
		return System.currentTimeMillis() - startTime;
		
	}
	
	public void run(){
		// flowchart of the story
		startHosting();
		startAskingQuestions();
		
		
	}
	
	private void startAskingQuestions() {
		// starts to question.
		msg("I will now ask you questions");
		event.askQuestions(getName());
	}

	private synchronized void startHosting() {
		// starts to host
		event.startToHost(getName());	
	}

}
