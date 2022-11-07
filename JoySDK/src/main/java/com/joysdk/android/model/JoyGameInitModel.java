package com.joysdk.android.model;

import java.util.List;
import java.util.Map;

public class JoyGameInitModel {
    private String lobby;
    private int height;
    private int width;
    private List<JoyGameHoleInfoModel> gameList;
    private Map<Integer, JoyGameHoleInfoModel> gamesUrlMap;

    public String getLobby() {
        return lobby;
    }
    public void setLobby(String lobby) {
        this.lobby = lobby;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public List<JoyGameHoleInfoModel> getGameList() {
        return gameList;
    }
    public void setGameList(List<JoyGameHoleInfoModel> gameList) {
        this.gameList = gameList;
    }

    public Map<Integer, JoyGameHoleInfoModel> getGamesUrlMap() {
        return gamesUrlMap;
    }
    public void setGamesUrlMap(Map<Integer, JoyGameHoleInfoModel> gamesUrlMap) {
        this.gamesUrlMap = gamesUrlMap;
    }

}
