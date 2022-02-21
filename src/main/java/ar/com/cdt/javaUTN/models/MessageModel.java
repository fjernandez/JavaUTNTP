package ar.com.cdt.javaUTN.models;

public class MessageModel {
	
	public String content;
	
	public MessageModel() {
		super();
	}

	public MessageModel(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "MessageModel [content=" + content + "]";
	}
	
}
