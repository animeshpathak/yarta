package fr.inria.arles.yarta.middleware.communication;

public class ChunkMessage extends Message {

	private static final long serialVersionUID = 1L;

	private String updateId;
	private int current;
	private int total;

	public ChunkMessage() {
	}

	public ChunkMessage(String updateId, int current, int total, byte[] data) {
		super(Message.TYPE_UPDATE_REPLY_MULTIPART, data);
		this.updateId = updateId;
		this.current = current;
		this.total = total;
	}

	public int getCurrent() {
		return current;
	}

	public int getTotal() {
		return total;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

}
