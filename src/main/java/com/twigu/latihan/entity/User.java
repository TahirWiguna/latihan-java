package com.twigu.latihan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true )
    private String username;

    @Column(nullable = false)
    private String password;

    @Column()
    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt = new Date();

//    @Column(name = "created_by")
//    private Long createdBy;

    @ManyToOne()
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private User createdBy;

//
//    @ManyToOne()
//    @JoinColumn(name = "creator")
//    private User creator;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

}
