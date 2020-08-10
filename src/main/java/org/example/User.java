package org.example;

import lombok.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class User implements Serializable {

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private Integer age;

    private Long timestamp;

}
