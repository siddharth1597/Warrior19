package sidsim.techwarriors;

public class StatusUpdateDetails {
    int status,totalBeds,vacantBeds,ventilators , vacantVentilaor,key;
    String name;
    String phone;
    String location_add;
    String location_lat;
    String location_long;
    String email;
    String state;
    String city;
    String date;

    public StatusUpdateDetails() {
        //Reading
    }

    public StatusUpdateDetails(int status, int totalBeds, int vacantBeds, int ventilators,int vacantVentilaor,int key,String name,String phone,String add , String lat,String lang,String email,String state,String city,String date) {
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
        this.vacantVentilaor = vacantVentilaor;
        this.state = state;
        this.city = city;
        this.date = date;

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

    public String getLocation_add() { return location_add; }

    public String getLocation_lat() {
        return location_lat;
    }

    public String getLocation_long() {
        return location_long;
    }

    public int getVacantVentilaor() { return vacantVentilaor; }

    public int getKey() { return key; }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCity(String city) {
        this.city = city;
    }
}