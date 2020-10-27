package com.example.firebaceproject.Model;

public class UserData {
    String Name,Email,Username,Password,Country,profile_image;

    public UserData(String name, String email, String username, String password, String country, String profile_image) {
        Name = name;
        Email = email;
        Username = username;
        Password = password;
        Country = country;
        this.profile_image = profile_image;
    }

    public UserData() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                ", Country='" + Country + '\'' +
                ", profile_image='" + profile_image + '\'' +
                '}';
    }
}
