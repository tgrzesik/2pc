package example.PC;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.*; 
import java.rmi.Naming;
//import java.net.MalformedURLException;

public class Coordinator implements CoordinatorInterface {


	private ParticipantInterface[] participants = new ParticipantInterface[5];
    
    	private int numParticipants = 0;  
	private String state = "Initial"; 
	

	private boolean[] voteCommitArray = {false,false}; 
    
    	private boolean[] ackCommitArray = {false,false}; 
    	private boolean[] ackAbortArray = {false,false}; 
    	private String fail; 

	private Coordinator() {
        super();
    }

    /*broadcast*/
    private synchronized void broadcastPrepare(){
        logString("Broadcast"); readInput();
        for(ParticipantInterface participant : this.participants){
    		this.createThread(participant); 
    	}
    }
    private void broadcastGlobalCommit(){
        logString("Broadcast: COMMIT"); readInput();
        for(ParticipantInterface participant : this.participants){
            this.createThread(participant); 
        }
    }
    private synchronized void broadcastGlobalAbort(){
        logString("Broadcast: ABORT"); readInput();
        for(ParticipantInterface participant : this.participants){
            this.createThread(participant); 
        }
    }

    private synchronized void createThread(ParticipantInterface tempParticipant){
        final ParticipantInterface participant = tempParticipant; 
        if(this.state.equals("Abort")){ 
            (new Thread(){ public synchronized void run(){
                try { 
                    logString("participant.receiveAbort() aboutToBeCalled"); 
                    readInput();
                    participant.receiveAbort(); 
                }
                catch (Exception e) { 
                }
            }}).start(); 
        }

        if(this.state.equals("Commit")){ 
            (new Thread(){ public synchronized void run(){
                try { 
                    logString("participant.receiveCommit() aboutToBeCalled"); 
                    readInput();
                    participant.receiveCommit(); 
                }
                catch (Exception e) { 
                }
            }}).start(); 
        }
        if(this.state.equals("Initial")) {
            (new Thread(){ public synchronized void run(){
                try { 
                    logString("participant.receivePrepare() aboutToBeCalled"); 
                    readInput();
                    participant.receivePrepare(); 
                }
                catch (Exception e) { 
                    Coordinator.this.broadcastGlobalAbort();
                }
            }}).start(); 
        }
    }


    /*Receive*/

    public synchronized void receiveVote(String abortOrCommit, int participantNum){
        logString("receiveVote: " + abortOrCommit); readInput();
        if(abortOrCommit.equals("Commit")){
            voteCommitArray[participantNum] = true; 
            if(allVoted(voteCommitArray)){
                setFalse(voteCommitArray); 
                state="Commit"; logState(); 
                broadcastGlobalCommit(); 
            }
        }
        else{ //votedAbort
            setFalse(voteCommitArray); state="Abort"; logState();
            broadcastGlobalAbort();
        }
    }
    public synchronized void receiveAck(String abortOrCommit, int participantNum){
        logString("receiveAck: " + abortOrCommit); readInput();
        if(abortOrCommit.equals("Commit")){
            ackCommitArray[participantNum] = true; 
            if(allVoted(ackCommitArray)){
                state="END_TRANSACTION"; logState(); 
            }
        }
        else{ //ackAbort
            ackAbortArray[participantNum] = true; 
            if(allVoted(ackAbortArray)){
                state="END_TRANSACTION"; logState(); 
            }
        }
    }



    

    private synchronized boolean allVoted(boolean[] arr){
        return (arr[0] == true) && (arr[1] == true); 
    }
    private synchronized void setFalse(boolean[] arr){
        arr[0] = false; arr[1] = false; 
    }

    public synchronized void addParticipant(ParticipantInterface participant, int participantNum){
        logString("adding new Participant"); readInput();
        this.participants[participantNum] = participant; 
        this.createThread(participant); 
    }

    
    private synchronized void lookupParticipants(){
        logString("lookup for Participants"); readInput();
        Registry registry; 
        try{
            registry = LocateRegistry.getRegistry(); 
            ParticipantInterface temp0 = 
                (ParticipantInterface) registry.lookup("participant0");
            temp0.addCoordinator(this); 
            this.addParticipant(temp0, 0); 
        } catch(Exception e){ 
        }
        try{
            registry = LocateRegistry.getRegistry(); 
            ParticipantInterface temp1 = 
                (ParticipantInterface) registry.lookup("participant1");
            temp1.addCoordinator(this);
            this.addParticipant(temp1, 1);  
        } catch(Exception e){  
        }
  	 try{
            registry = LocateRegistry.getRegistry(); 
            ParticipantInterface temp2 = 
                (ParticipantInterface) registry.lookup("participant2");
            temp2.addCoordinator(this); 
            this.addParticipant(temp2, 2); 
        } catch(Exception e){  
        }
	 try{
            registry = LocateRegistry.getRegistry(); 
            ParticipantInterface temp3 = 
                (ParticipantInterface) registry.lookup("participant3");
            temp3.addCoordinator(this); 
            this.addParticipant(temp3, 3); 
        } catch(Exception e){ 
        }
	 try{
            registry = LocateRegistry.getRegistry(); 
            ParticipantInterface temp4 = 
                (ParticipantInterface) registry.lookup("participant4");
            temp4.addCoordinator(this); 
            this.addParticipant(temp4, 4); 
        } catch(Exception e){ 
        }
    }

    private synchronized void logState(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/damian/Desktop/2PCGlobal/coordinatorState.txt"), false)); 
            bw.write(this.state);
            System.out.println("this.state= " + this.state); 
            bw.close();
        }catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private synchronized void logString(String mystring){
        try {
            File log = new File("/home/damian/Desktop/2PCGlobal/Coordinator0Log.txt"); 
            if(!log.exists()){log.createNewFile();}
            BufferedWriter bw = new BufferedWriter(new FileWriter(log , true)); 
            bw.write(mystring+"\n");
            System.out.println(mystring); 
            bw.close();
        }catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

   private synchronized void readInput(){ 
        String input = System.console().readLine("\tType:y for continue ora n for exit: "); 
        if(input.equals("n")){
            System.exit(0); 
        }
    }





	public synchronized static void main(String[] args) {
         int port = 1099;
	String hostname = "0.0.0.0";
	String bindLocation="//"+ hostname + ":" + port + "/stub";
        Coordinator coordinator = new Coordinator();
        try { 
            CoordinatorInterface stub = 
                (CoordinatorInterface) UnicastRemoteObject.exportObject(coordinator, 0);
            Registry registry = LocateRegistry.getRegistry(port);
            Naming.rebind(bindLocation, stub);
	  System.out.println("Server start at: "+bindLocation);
        } catch (Exception e) {
            System.err.println(" exception:");
            e.printStackTrace();
        }
        try { 
            File file = new  File("/home/damian/Desktop/2PCGlobal/coordinatorState.txt"); 

            if (file.exists()) { 
                Scanner s = new Scanner(file); 
                coordinator.state = s.useDelimiter("\\Z").next();
                s.close(); 
                coordinator.logString("Recovering"); coordinator.readInput();

                coordinator.lookupParticipants();
                 
            }
            else{ 
                coordinator.state = "Initial"; 
                file.createNewFile();
                coordinator.logState();
            }

        } catch (IOException e) { 
            e.printStackTrace();
        }
    } 
}
