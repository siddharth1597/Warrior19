package sidsim.techwarriors;

public class RegistrationDetails {
        String name;
        String email;
        String password;
        String key;
        public RegistrationDetails(){
            //For Reading
        }
        public RegistrationDetails( String name, String email,String password,String key) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

    public String getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }
}
