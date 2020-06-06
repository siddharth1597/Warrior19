package sidsim.techwarriors_users;

public class StatusUpdateDetails {
    int status,totalBeds,vacantBeds,ventilators , vacantVentilaor,key;
    String name;
    String phone;
    String location_add;
    String location_lat;
    String location_long;
    String email;
    String state, city;
    public StatusUpdateDetails() {
        //Reading
    }

    public StatusUpdateDetails(int status, int totalBeds, int vacantBeds, int ventilators, int vacantVentilaor, int key, String name, String phone, String add , String lat, String lang, String email,String state, String city) {
        this.status = status;
        this.totalBeds = totalBeds;
        this.vacantBeds = vacantBeds;
        this.ventilators = ventilators;
        this.name = name;
        this.phone = phone;
        this.location_add = add;
        this.location_lat = lat;
        this.location_long = lang;
        this.email = email;
        this.key= key;
        this.state= state;
        this.city= city;
        this.vacantVentilaor = vacantVentilaor;
    }

    public int getStatus() { return status; }

    public int getTotalBeds() { return totalBeds; }

    public int getVacantBeds() { return vacantBeds; }

    public String getEmail() {return email; }

    public int getVentilators() { return ventilators; }
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public void setVacantBeds(int vacantBeds) {
        this.vacantBeds = vacantBeds;
    }

    public void setVentilators(int ventilators) {
        this.ventilators = ventilators;
    }

    public void setVacantVentilaor(int vacantVentilaor) {
        this.vacantVentilaor = vacantVentilaor;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation_add(String location_add) {
        this.location_add = location_add;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public void setLocation_long(String location_long) {
        this.location_long = location_long;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation_add() { return location_add; }

    public String getLocation_lat() {
        return location_lat;
    }

    public String getLocation_long() {
        return location_long;
    }

    public int getVacantVentilaor() { return vacantVentilaor; }

    public int getKey() { return key; }

    public String getState() { return state; }
    public String getCity() { return city; }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }
}