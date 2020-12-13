package com.traincon.modelleisenbahn_controller.database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LocoDao {
    @Query("SELECT * FROM loco")
    List<Loco> getAll();

// --Commented out by Inspection START (13.12.20 20:10):
//    @Query("SELECT * FROM loco WHERE designation LIKE :targetDesignation")
//    Loco findByDesignation(String targetDesignation);
// --Commented out by Inspection STOP (13.12.20 20:10)

    @Insert
    void insertLoco(Loco loco);

// --Commented out by Inspection START (13.12.20 20:10):
//    @Update
//    void updateLoco(Loco locos);
// --Commented out by Inspection STOP (13.12.20 20:10)

// --Commented out by Inspection START (13.12.20 20:10):
//    @Delete
//    void delete(Loco loco);
// --Commented out by Inspection STOP (13.12.20 20:10)
}
