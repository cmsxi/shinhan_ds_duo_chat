package shinhan_ds_duo_chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Session {
	private String userId;
	private String name;
	private String phoneNumber;
	private String password;
}
