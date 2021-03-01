package com.traincon.modelleisenbahn_controller.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * All locos will be stored in this database
 * @see androidx.room.RoomDatabase
 */
@Database(entities = {Loco.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocoDao locoDao();
}
