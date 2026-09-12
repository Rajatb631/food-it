package com.foodit.clientside.entity;

import jakarta.persistence.*;

@Entity 
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true ,nullable = false)
    private String email;

    public User(){

    }

    public User( String name, String email){
        this.name=name;
        this.email=email;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    
    public void setName(String name){
        this.name=name;
    }

     public void setEmail(String email){
        this.email=email;
    }
}
