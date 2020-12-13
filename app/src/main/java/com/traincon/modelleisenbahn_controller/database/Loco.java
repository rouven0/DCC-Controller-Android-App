package com.traincon.modelleisenbahn_controller.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Loco {

    @PrimaryKey
    public int address;

    public String designation;

    public void setAddress(int address) {
        this.address = address;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
