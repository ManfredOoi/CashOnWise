package com.example.cashonwise.cashonwise;

/**
 * Created by but on 20/8/2017.
 */

public class Account {
    private String id;
    private String name;
    private String icnum;
    private String contactnum;
    private String address;
    private String email;
    private String password;
    private String pin;

    public Account() {

    }

    public Account(String name, String icnum, String contactnum, String address, String email, String password, String pin) {
        this.name = name;
        this.icnum = icnum;
        this.contactnum = contactnum;
        this.address = address;
        this.email = email;
        this.password = password;
        this.pin = pin;
    }

    public Account(String id, String name, String icnum, String contactnum, String address, String email, String password, String pin) {
        this.id = id;
        this.name = name;
        this.icnum = icnum;
        this.contactnum = contactnum;
        this.address = address;
        this.email = email;
        this.password = password;
        this.pin = pin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcnum() {
        return icnum;
    }

    public void setIcnum(String icnum) {
        this.icnum = icnum;
    }

    public String getContactnum() {
        return contactnum;
    }

    public void setContactnum(String contactnum) {
        this.contactnum = contactnum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", icnum='" + icnum + '\'' +
                ", contactnum='" + contactnum + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
