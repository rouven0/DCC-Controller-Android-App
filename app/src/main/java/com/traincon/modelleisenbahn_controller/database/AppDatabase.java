package com.traincon.modelleisenbahn_controller.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Loco.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocoDao locoDao();
}
