package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String nic;
    private LocalDate dob;
    private String phoneNumber;
}
