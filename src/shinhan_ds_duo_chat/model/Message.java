package shinhan_ds_duo_chat.model;

import java.sql.Date;

import lombok.Data;

@Data
public class Message {
	private String messageNum;
	private String content;
	private Date chatTime;
	private String userID;
	private String roomID;
	private int orderIndex;

}
