
public class mainClass {

	// Threads
	public static Announcer announcer;
	public static Contestants[] contestants;
	
	// Event Handler
	public static Event event;
	
	// Default Values
	public static int numRounds = 2;
	public static int numQuestions = 5;
	public static int questionValues = 200;
	public static double rightPercent = 65;
	public static int room_apacity = 4;
	public static int num_contestants = 13;
	
	public static void main(String[] args){
		
		
		// Command line is not tried with changed values. Every test was 
		// done in default values.
		if(args.length > 0){
			numRounds = Integer.parseInt(args[0]);
			numQuestions = Integer.parseInt(args[1]);
			questionValues = Integer.parseInt(args[2]);
			rightPercent =  Integer.parseInt(args[3]);
			room_apacity = Integer.parseInt(args[4]);
			num_contestants = Integer.parseInt(args[5]);
		}
		
		initializeClasses();
		
	}

	private static void initializeClasses() {
		// Initialization of event
		event = new Event(num_contestants, room_apacity, numRounds, numQuestions, rightPercent);
		
		// Initialization of the threads
		announcer = new Announcer(event, num_contestants);
		announcer.start();
		contestants = new Contestants[num_contestants];
		
		for(int i = 0; i < num_contestants; i++){
			contestants[i] = new Contestants(i+1, event);
			contestants[i].start();
		}	
	}
	
	
}
