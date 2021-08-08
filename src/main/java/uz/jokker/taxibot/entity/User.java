package uz.jokker.taxibot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.jokker.taxibot.entity.enums.UserType;

import javax.persistence.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column( length = 15)
    private String firstName;

    @Column( length = 15)
    private String lastName;


    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToOne
    private Car carType;

    @Column(nullable = false)
    private Long chatId;

    private String state;

    private long yulduzcha;
}
