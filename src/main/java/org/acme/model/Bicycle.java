package org.acme.model;

public class Bicycle {

    long id;
    String brand;
    String name;
    String type;
    int gear;
    double price;

    public Bicycle() {
    }

    public static Bicycle of(long id, String brand, String name, String type, int gear, double price) {
        Bicycle data = new Bicycle();
        data.setId(id);
        data.setBrand(brand);
        data.setName(name);
        data.setType(type);
        data.setGear(gear);
        data.setPrice(price);
        return data;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Bicycle{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", gear=" + gear +
                ", price=" + price +
                '}';
    }
}
