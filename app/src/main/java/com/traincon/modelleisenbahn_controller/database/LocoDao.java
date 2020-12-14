package com.traincon.modelleisenbahn_controller.database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface LocoDao {
    @Query("SELECT * FROM loco")
    List<Loco> getAll();

    @Insert
    void insertLoco(Loco loco);

    @Update
    void updateLoco(Loco loco);

    @Delete
    void delete(Loco loco);
}
