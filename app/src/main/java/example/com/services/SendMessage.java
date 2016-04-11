package example.com.services;

import java.util.Date;

/**
 * Created by GEEKER on 2016/4/10.
 */
public interface SendMessage {
    void sendmessages(boolean now, Date d, String phone, String content);
}
