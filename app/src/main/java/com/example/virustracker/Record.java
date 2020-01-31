package com.example.virustracker;

import androidx.annotation.NonNull;

public class Record {
    @NonNull Integer confirmed, suspected, cured, dead;

    Record(@NonNull Integer confirmed, @NonNull Integer suspected, @NonNull Integer cured, @NonNull Integer dead){
        this.confirmed = confirmed;
        this.suspected = suspected;
        this.cured = cured;
        this.dead = dead;
    }

    @NonNull
    @Override
    public String toString() {
        return "{confirmed="+confirmed+", suspected="+suspected+", cured="+cured+"}";
    }
}
