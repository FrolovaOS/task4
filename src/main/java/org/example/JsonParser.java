package org.example;

import java.sql.Timestamp;
import org.json.JSONObject;


public class JsonParser {


    public User getUser(String response) {
        JSONObject userJson = new JSONObject(response);

        String firstName = userJson.getString("firstName");
        String lastName = userJson.getString("lastName");
        int age = userJson.getInt("age");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userJson.append("handledTimestamp", timestamp.toString());

        return new User( firstName,lastName,age,timestamp.toString());
    }

    public String getJson(User user){
        String firstName = user.getFirstName();
        String lastName =user.getLastName();
        int age = user.getAge();
        String timestamp =user.getTimestamp();
       String message = " {\"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"age\": "+age+"\", \"timestamp\": \""+timestamp+"}";
        return message;
    }
}
