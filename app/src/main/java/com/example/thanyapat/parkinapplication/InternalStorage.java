package com.example.thanyapat.parkinapplication;

import android.content.Context;

import com.example.thanyapat.parkinapplication.History.HistoryContent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage{

    public final static String HISTORY_KEY = "history-key";

    private InternalStorage() {}

    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    public static void writeHistoryObject(Context context) throws IOException {
        writeObject(context, HISTORY_KEY, HistoryContent.ITEMS);
    }

    public static Object readHistoryObject(Context context) throws IOException,
            ClassNotFoundException {
        return readObject(context,HISTORY_KEY);
    }
}
