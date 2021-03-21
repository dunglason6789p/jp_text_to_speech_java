package ntson.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GreetingPersonalRequest{
    private String name;
    private String personalId;
}
