public class KinTerm {
	String term="";
	String sex="N";
	
	public KinTerm() {
		
	}
	
	public KinTerm(String term) {
		this.term = term;
	}

	public KinTerm(String term, String sex) {
		this.term = term;
		this.sex = sex;
	}

	public boolean isMe(String f) {
		return sex.equals(f);
	}
	
	public String getTerm() {
		return term;
	}
	
	public String getSex() {
		return sex;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}

	public void setSex(String mm) {
		sex=mm;
	}
}