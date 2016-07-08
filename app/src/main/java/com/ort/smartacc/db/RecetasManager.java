package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class RecetasManager extends TableManager {

    public RecetasManager() {
        super("recetas", "CREATE TABLE IF NOT EXISTS `recetas` (" +
                "  `IDRecetas` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  `Texto` varchar(2048) NOT NULL," +
                "  `Imagen` varchar(80) NOT NULL," +
                "  `Nombre` varchar(80) NOT NULL," +
                "  `TiempoPrep` tinyint(4) NOT NULL," +
                "  `Puntaje` tinyint(4) NOT NULL" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);

        Receta[] recetas = (new Gson()).fromJson(json, Receta[].class);

        for(Receta receta:recetas) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDRecetas", receta.IDRecetas);
            contentValues.put("Texto", receta.Texto);
            contentValues.put("Imagen", receta.Imagen);
            contentValues.put("Nombre", receta.Nombre);
            contentValues.put("TiempoPrep", receta.TiempoPrep);
            contentValues.put("Puntaje", receta.Puntaje);
            database.insert(name, null, contentValues);
        }
    }

    private class Receta{
        long IDRecetas;
        String Texto;
        String Imagen;
        String Nombre;
        int TiempoPrep;
        int Puntaje;
    }
}
//Cada usuario tiene un numero y horario de llegada, queda registrado en un ticket. se registra la hora de llegada, atencion, y salida.
//Resultados: tiempo promedio de espera, tiempo promedio de atencion, mayor tiempo de espera+atencion, cantidad de usuarios atendidos.