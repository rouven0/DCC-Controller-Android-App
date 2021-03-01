package com.traincon.modelleisenbahn_controller.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A loco is given to a Controller to allocate a session
 * @see com.traincon.modelleisenbahn_controller.ui.ControllerFragment
 */
@SuppressWarnings("unused")
@Entity
public class Loco {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * In the DCC-protocol the address is used to specify which loco belongs to the session
     */
    public int address;

    /**
     * The designation is used to display the loco to the user
     */
    public String designation;

    public void setAddress(int address) {
        this.address = address;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
