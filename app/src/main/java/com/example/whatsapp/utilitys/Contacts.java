package com.example.whatsapp.utilitys;

public class Contacts {

    public String name, status, profile_image;

    public Contacts() {

    }

    public Contacts(String name, String status, String profile_image) {
        this.name = name;
        this.status = status;
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
