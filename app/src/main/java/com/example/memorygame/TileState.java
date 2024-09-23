package com.example.memorygame;

class TileState implements java.io.Serializable {
    public int resourceid;
    public boolean turned;

    TileState(int r, boolean t) {
        resourceid = r;
        turned = t;
    }
}