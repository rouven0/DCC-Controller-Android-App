package com.traincon.modelleisenbahn_controller.database;

import android.app.Application;

import com.traincon.modelleisenbahn_controller.database.AppDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;

/**
 * This ViewModel is transferred between all the activities that access the Loco Database
 */
public class DatabaseViewModel extends AndroidViewModel {
    public final AppDatabase appDatabase = Room.databaseBuilder(this.getApplication(), AppDatabase.class, "loco_database").build();

    public DatabaseViewModel(@NonNull Application application) {
        super(application);
    }

}
