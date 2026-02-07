public class InstructionSet extends ListVector {
	String title="Instruction Set";

	public InstructionSet(String name) {
		title=name;
	}

	public InstructionSet() {
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String n) {
		title=n;
	}
			
	public Instruction create(String name, int opcode, int[] depends, int[]excludes) {
		Instruction ret;
		addElement(ret = new Instruction(name,opcode,depends,excludes));
		return ret;
	}
	
	public Instruction create(String name) {
		int opcode=0;
		Instruction ret;
		if (name.equals("")) {
			addElement(ret = new Instruction(name,32000));
			return ret;
		}
		for(int i=0;i<size();i++) {
			if (((Instruction) elementAt(i)).opcode > opcode)
				opcode = ((Instruction) elementAt(i)).opcode;
		}
		opcode++;
		addElement(ret = new Instruction(name,opcode));
		return ret;
	}
	
	public Instruction create(String name, int opcode) {
		Instruction ret;
		addElement(ret = new Instruction(name,opcode));
		return ret;
	}
	
	public Instruction getInstruction(String name) {
		for(int i=0;i<size();i++) {
			if (((Instruction) elementAt(i)).name.equals(name))
				return ((Instruction) elementAt(i));
		}
		return null;
	}
	
	public Instruction getInstruction(int opcode) {
		for(int i=0;i<size();i++) {
			if (((Instruction) elementAt(i)).opcode == opcode)
				return ((Instruction) elementAt(i));
		}
		return null;
	}
	
	public int getOpcode(String name) {
		for(int i=0;i<size();i++) {
			if (((Instruction) elementAt(i)).name.equals(name))
				return ((Instruction) elementAt(i)).opcode;
		}
		return -1;
	}
	
	public String getName(int opcode) {
		for(int i=0;i<size();i++) {
			if (((Instruction) elementAt(i)).opcode == opcode)
				return ((Instruction) elementAt(i)).name;
		}
		return null;
	}
	
	public String getCurrentName() {
		return ((Instruction) get()).name;
	}
		
	public void disableExclusiveWithInstructions(Instruction ins) {
		for(int i=0;i < ins.getExclusiveWith().size();i++) {
			int itsOpcode = ins.getExclusiveWith(i);
			Instruction it = getInstruction(itsOpcode);
			it.getTheMenuItem().setEnabled(false);
		}
		// check to see that exclusiveees are mutual in all cases
	}
	
	public void disableEnable(Instruction ins) {
		if (ins.dependsOn.size() != 0) {
			ins.getTheMenuItem().setEnabled(false);
			for(int i=0;i < ins.getDependsOn().size();i++) {
				int itsOpcode = ins.getDependsOn(i);
				Instruction it = getInstruction(itsOpcode);
				System.out.println(ins.getName()+" -- "+it.getName());
				if (it.getTheMenuItem().getState()) {
					if (!ins.getTheMenuItem().getState()) 
						ins.getTheMenuItem().setEnabled(true);
					break;
				}
			}
		}
		if (ins.exclusiveWith.size() != 0) {
			if (ins.getTheMenuItem().isEnabled())
				for(int i=0;i < ins.getExclusiveWith().size();i++) {
					int itsOpcode = ins.getExclusiveWith(i);
					Instruction it = getInstruction(itsOpcode);
					if (it.getTheMenuItem().getState()) {
						ins.getTheMenuItem().setEnabled(false);
						return;
					}
				}
		}
			
		// ins.getTheMenuItem().setEnabled(false);
	}
	
	public void enableInstructions() {
		for(int i=0;i<size();i++) {
			disableEnable((Instruction) elementAt(i));
		}
	}
	
}