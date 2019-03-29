package uncme.seniors_at_work;

public class User {

    //test comment from davids branch
    //test comment from Nick's Branch

    //attributes
    int age = 0;
    String firstName = "Default";
    String lastName = "User";
    //String email;

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
