
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Loader{
	
// Class Variables
	
	private String jobFilePointerCSX = "/home/opsys/OS-I/SP17/17s-ph1-data";
	private String jobFilePointerLocalWin = "C:\\Users\\Dillon LeDoux\\Desktop\\17s-ph1-data.txt";
	private String jobFilePointerLocalMac = "/Users/dillonledoux/Desktop/testData.txt";
	
	private final File jobFile = new File(jobFilePointerLocalMac);
	private Scanner scanner;
	
	private boolean moreJobsInFile = true;
	
	protected ArrayList<ArrayList<Integer>> jobQ = new ArrayList<ArrayList<Integer>>(); 
		
	SYSTEM system;
	Mem_manager mem_manager;
	Scheduler scheduler;
		
// Constructor
	public Loader(SYSTEM systemIn, Mem_manager mem_managerIn, Scheduler schedulerIn){
	    system = systemIn;
	    mem_manager = mem_managerIn;
	    scheduler = schedulerIn;
	    
	    try{
	    	scanner = new Scanner(jobFile);
	    }
	    catch(IOException e){
	    	System.out.println("Could not load form Job File");
	    	System.exit(1);
	    }
	}
	
// Function Methods     

    public boolean hasMoreJobsInFile(){
      return moreJobsInFile;
    }
    
    public void loadFromJobQ(){
    	int index = 0;
    	boolean canAllocate; 
    	while(system.getFreeMemory()>=mem_manager.getMemCutoff()  
    			&& scheduler.getTotalPCBs()<15 && index<jobQ.size()){
    		
    			canAllocate = mem_manager.allocate(jobQ.get(index).get(1));
    			
    			if(canAllocate){
    				scheduler.setup(jobQ.remove(index));
    			}
    			index++;
    	}
    }
   
    public void loadTasks(){
    	loadFromJobQ();
    	boolean canAllocate;
    	ArrayList<Integer> newJob;   	
    	while(system.getFreeMemory()>=mem_manager.getMemCutoff() && scheduler.getTotalPCBs()<15
    			&& moreJobsInFile ){
    		newJob = getNextJob();
    		if(newJob.get(0) != 0){
    			canAllocate = mem_manager.allocate(newJob.get(1));
    			if(canAllocate){
    				scheduler.setup(newJob);    				
    			}
    			else{
    				jobQ.add(newJob);   				
    			}
    		}
    		else{
    			break;
    		}
    	}
    }
    
// Initialization Methods
    
    public ArrayList<Integer> getNextJob(){
    	if(scanner.hasNextLine() == false){
    		moreJobsInFile = false;
    		ArrayList<Integer> list = new ArrayList<>();
    		list.add(0);
    		return list;
    	}
    	String line = scanner.nextLine();
    	
    	
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(line.split("\\W+")));
        try{
        	Integer.parseInt(stringList.get(0));
        }
        catch(Exception e){
        	stringList.remove(0);
        }
        
        ArrayList<Integer> jobInfo = new ArrayList<>();
        for(String s: stringList){
        	jobInfo.add(Integer.valueOf(s));
        }
        return jobInfo;   
     }

    public ArrayList<ArrayList<Integer>> getJobQ(){
    	return jobQ;
    }
    public void addToJobQ(ArrayList<Integer> job){
     	jobQ.add(job);
    }
    public int getJobQSize(){
    	return jobQ.size();
    }
}