package uncme.seniors_at_work;

import java.io.Serializable;

public class User implements Serializable {

    //test comment from davids branch
    //test comment from Nick's Branch

    //attributes
    int age = 0;
    private String username;
    private String firstName = "Default";
    private String lastName = "User";
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    private String dob; //birthdate
    //String test;

    //constructors
    public User(String email) {
        this.email = email;
    }

    //methods

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
