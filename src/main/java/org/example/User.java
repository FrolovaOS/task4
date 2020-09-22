package org.example;

import lombok.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class User implements Serializable {

    private int id;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private Integer age;

    @NonNull
    private String role;

    private Long timestamp;

    private Integer count;



}
