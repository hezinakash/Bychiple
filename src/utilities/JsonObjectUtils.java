package utilities;

public class JsonObjectUtils
{
	private boolean isSucceded;
    private String reason;

    public JsonObjectUtils() {
        this.isSucceded = false;
        this.reason = "";
    }
    
    public boolean getIsSucceded(){
    	return isSucceded;
    }
    public String getReason(){
    	return reason;
    }
    
    public void setIsSucceded(boolean isSucceded){
    	this.isSucceded = isSucceded;
    }
    public void setReason(String reason){
    	this.reason = reason;
    }
}
