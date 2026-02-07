public class Operation{
       
    public Operation(){
    	name = "NoName";
    }
    
    public Operation(String name){
        setName(name);
    }
    
    public String run(KintermFrame frame){
        return "";
    }
 
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	protected String name;

	public void setTest(boolean test) {
		this.test = test;
	}

	public boolean isTest() {
		return test;
	}
	protected boolean test=false;
}
