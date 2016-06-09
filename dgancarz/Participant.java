package example.PC;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.lang.*;  
import java.io.*;
import java.rmi.Naming;
import java.net.MalformedURLException;


public class Participant implements ParticipantInterface {


    private String state = "Initial"; 
    private CoordinatorInterface coordinator; 
    private int participantNum; 
    private String fail; 

	public void receivePrepare(){
		this.state = "Ready"; 
        	logString("receivedPrepare"); 
        	String vote = readVoteInput(); 

        try{
            System.out.println("aboutToSendVote: " + vote); 
            coordinator.receiveVote(vote, this.participantNum);
            return;
        }
        catch(Exception e){ 
            logString("receiveVote('commit') Exception"); 
            lookupCoordinator(); 
            
        }

	}

    public void receiveAbort(){
        this.state = "Abort";
        logString("receivedAbort"); readInput(); 
        try{
            System.out.println("aboutToSendAck:" + this.state); 
            coordinator.receiveAck("Abort", this.participantNum);
        }
        catch(Exception e){
            logString("coordinator.receiveAck('Abort') Exception");
            
        }

    }

    public void receiveCommit(){
        this.state = "Commit"; 
        logString("receivedCommit"); readInput();
 

        try{
            System.out.println("aboutToSendAck:" + this.state); 
            coordinator.receiveAck("Commit", this.participantNum);
        }
        catch(Exception e){
            logString("coordinator.receiveAck('Commit') Exception");
            
        } 
    }

    private synchronized void logString(String mystring){
        System.out.println("this.state:" +  this.state); 
        try {
            File log = new File("/home/damian/Desktop/2PCGlobal/Participant"+ participantNum + "Log.txt"); 
            if(!log.exists()){log.createNewFile();}
            BufferedWriter bw = new BufferedWriter(new FileWriter(log , true)); 
            bw.write(mystring+"\n");
            System.out.println(mystring); 
            bw.close();
        }catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private synchronized String readVoteInput(){
        String input = System.console().readLine("\tPleaseVote: Commit || Abort\n"); 
        if(input.equals("Abort")){ return "Abort"; }
        else if(input.equals("Commit")){return "Commit";}
        else{
            System.out.println("incorrectInput VoteAgain"); 
            return this.readVoteInput();
        }
    }

    public synchronized void addCoordinator(CoordinatorInterface coordinator0){
        this.coordinator = coordinator0; 
    }


   private synchronized void readInput(){ 
        String input = System.console().readLine("\tType:y to continue or n for exit: "); 
        if(input.equals("n")){
            System.exit(0); 
        }
    }

    private synchronized void lookupCoordinator(){
        try {
            Registry registry = LocateRegistry.getRegistry(); 
            this.coordinator = 
                (CoordinatorInterface) Naming.lookup("coordinator0");
        }
        catch(Exception e){
            System.out.println("Coordinator couldn't be restarted"); 
        }
    }



    public static void main(String args[]) {
         int port = 1099;
 	String remoteHostname="192.168.0.17";
	String connectLocation= "//"+ remoteHostname + ":" + port + "/stub";
        long start=System.currentTimeMillis();
        try { 
            Participant participant = new Participant();
            ParticipantInterface stub = 
                (ParticipantInterface) UnicastRemoteObject.exportObject(participant, 0);
           
            participant.coordinator = 
                (CoordinatorInterface) Naming.lookup(connectLocation);

            participant.coordinator.addParticipant(participant, participant.participantNum); 
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
       long stop=System.currentTimeMillis();
       System.out.println("Czas wykonania [s]: "+((stop-start)/1000));
    }    
}
