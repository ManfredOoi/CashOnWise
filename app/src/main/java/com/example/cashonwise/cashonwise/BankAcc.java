package com.example.cashonwise.cashonwise;

/**
 * Created by but on 21/8/2017.
 */

public class BankAcc {
    private String id;
    private String name;
    private String icnum;
    private String contactnum;
    private String address;
    private String email;
    private String password;
    private String type;
    private String balance;
    private String cow_id;

    public BankAcc() {

    }

    public BankAcc(String name, String icnum, String contactnum, String address, String email) {
        this.name = name;
        this.icnum = icnum;
        this.contactnum = contactnum;
        this.address = address;
        this.email = email;
    }

    public BankAcc(String id, String name, String icnum, String contactnum, String address, String email, String password, String type, String cow_id) {
        this.id = id;
        this.name = name;
        this.icnum = icnum;
        this.contactnum = contactnum;
        this.address = address;
        this.email = email;
        this.password = password;
        this.type = type;
        this.cow_id = cow_id;
    }

    public BankAcc(String id, String name, String icnum, String contactnum, String address, String email, String password, String type, String balance, String cow_id) {
        this.id = id;
        this.name = name;
        this.icnum = icnum;
        this.contactnum = contactnum;
        this.address = address;
        this.email = email;
        this.password = password;
        this.type = type;
        this.balance = balance;
        this.cow_id = cow_id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCow_id() {
        return cow_id;
    }

    public void setCow_id(String cow_id) {
        this.cow_id = cow_id;
    }

    @Override
    public String toString() {
        return "BankAcc{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icnum='" + icnum + '\'' +
                ", contactnum='" + contactnum + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", cow_id='" + cow_id + '\'' +
                '}';
    }
}
