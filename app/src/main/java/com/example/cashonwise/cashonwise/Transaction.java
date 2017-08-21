package com.example.cashonwise.cashonwise;

/**
 * Created by but on 21/8/2017.
 */

public class Transaction {

    private String id;
    private String date;
    private String location;
    private String amount;
    private String status;
    private String cow_id;

    public Transaction() {
    }

    public Transaction(String id, String date, String location, String amount, String status, String cow_id) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.amount = amount;
        this.status = status;
        this.cow_id = cow_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCow_id() {
        return cow_id;
    }

    public void setCow_id(String cow_id) {
        this.cow_id = cow_id;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", amount='" + amount + '\'' +
                ", status='" + status + '\'' +
                ", cow_id='" + cow_id + '\'' +
                '}';
    }
}
