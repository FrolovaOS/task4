package org.example;

import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
public class User {

    private String firstName;
    private String lastName;
    private int age;
    private String timestamp;


    public User(String firstName, String lastName, int age, String timestamp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.timestamp = timestamp;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String ToString()
    {
        String message = " {\"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"age\": "+age+"\", \"timestamp\": \""+timestamp+"}";
        return message;
    }
}
