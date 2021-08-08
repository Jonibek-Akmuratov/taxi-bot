package uz.jokker.taxibot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import uz.jokker.taxibot.entity.enums.UserType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @Enumerated
    private UserType posterType;

    @ManyToOne
    private Region qayerga;

    @ManyToOne
    private Region qayerdan;

    @Column(length = 254)
    private String defination;

    @CreationTimestamp
    private Timestamp createAt;

    private Timestamp activeTime;

    private boolean active=false;
}
