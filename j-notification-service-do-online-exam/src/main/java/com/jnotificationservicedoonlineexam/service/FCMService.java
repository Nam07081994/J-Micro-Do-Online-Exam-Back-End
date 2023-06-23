package com.jnotificationservicedoonlineexam.service;

import org.springframework.stereotype.Service;

@Service
public class FCMService {

	//    public String sendFCMNotification(NotificationEventRequest note, String token){
	//        Notification notification = Notification.builder()
	//                .setTitle(note.getTitle()).setBody(note.getContent()).build();

	// send notification without data
	//        Message messageWithoutData = Message.builder()
	//                .setToken(token).setNotification(notification).build();

	//        send notification with data
	//        JSONObject obj = new JSONObject();
	//        obj.put("test",1);
	//        Message messageWithData = Message.builder()
	//                .setToken(token).setNotification(notification).putData("data",
	// String.valueOf(obj))
	//                .build();

	//        try {
	//            FirebaseMessaging.getInstance().sendAsync(messageWithoutData).get();
	//        }catch (Exception ex){
	//            return ex.getMessage();
	//        }
	//
	//        return "Send message success";
	//    }

	public void sendFCMNotifyByTopic() {}

	public void sendFCMNotify() {}

	public void subscribeFCMTopic(String clientID, String topic) {}

	public void unsubscribeFCMTopic(String clientID, String topic) {}
}
