import java.util.Hashtable;

public class Processor  {
    
   static Hashtable myHash = new Hashtable(20);
   static  KintermFrame masterFrame;

   public Processor() {
   	
   }
   
   static  public void insert(Operation op) {
       myHash.put(op.getName(),op); 
    }
    
   static public void init(){
        myHash.put("",new Operation());
        myHash.put(null,new Operation());
    }
  
   static  public void reset(){
    }
    
   static  String myResume = "";
    
   static  public boolean resume(){
        if (myResume == "" || myResume == null) return false;
	    return execute(myResume);
    }
    
   static  public boolean execute(String operation){
       for (;;) {
        Operation x = ((Operation) myHash.get(operation));
	        if (x!=null){
	            status = myResume = x.run(masterFrame);
	            if (myResume == "" || myResume == null) return false;
	            if (x.isTest()) continue;
	            if (Mode.getMode() == Mode.MANUAL || Mode.getMode() == Mode.TRACE)
	            	return true;
	        }
	        else {
	            System.out.println("Processor: execute - operation "+operation+" not defined");
	            return false;
	        }
		}
   }
    
   static  public boolean execute(StringVector operations){
    	return false;
    }
    
   static  int myMode = Mode.AUTOMATIC;
    
   static  public void setMode(int mode){
        myMode = mode;
    }

	static public void setStatus(String s) {
		status = s;
	}

	static public String getStatus() {
		return status;
	}
	static protected String status;
}