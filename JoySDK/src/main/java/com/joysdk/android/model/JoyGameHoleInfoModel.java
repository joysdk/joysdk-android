package com.joysdk.android.model;

public class JoyGameHoleInfoModel {

    private String iconUrl;
    private String gameName;
    private String gameUrl;
    private int gameId;
    private int height;
    private int width;
    private int isCPGame;

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String var1) {
        this.iconUrl = var1;
    }

    public String getGameName() {
        return this.gameName;
    }

    public void setGameName(String var1) {
        this.gameName = var1;
    }

    public String getGameUrl() {
        return this.gameUrl;
    }

    public void setGameUrl(String var1) {
        this.gameUrl = var1;
    }

    public int getGameId() {
        return this.gameId;
    }

    public void setGameId(int var1) {
        this.gameId = var1;
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

    public int getIsCPGame() {
        return isCPGame;
    }

    public void setIsCPGame(int isCPGame) {
        this.isCPGame = isCPGame;
    }
}
