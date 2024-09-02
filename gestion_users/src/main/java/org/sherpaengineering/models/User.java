package org.sherpaengineering.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    private String lastName;
    @Column(nullable = false , unique = true)
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;


    private Boolean verified;

}