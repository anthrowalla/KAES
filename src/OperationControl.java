public interface OperationControl{
    public void reset();
    public boolean resume();
    public boolean execute(String operation);
    public boolean execute(StringVector operations);
    public void setMode(int mode);
}
