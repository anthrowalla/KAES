import java.util.*;

public interface CayleyEntity {
	
	public Enumeration keys();
	public String getTerm(String s);
	public StringVector compareCayleyTables(CayleyEntity aMap);
}