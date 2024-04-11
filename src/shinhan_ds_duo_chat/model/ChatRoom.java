package shinhan_ds_duo_chat.model;

import java.sql.Date;

import lombok.Data;

@Data
public class ChatRoom {
	private String roomID;
	private String roomName;
	private String recentMsg;
	private Date recentTime; 
}
