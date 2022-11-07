package com.joysdk.android.model;

public class JoyGameInfoModel {

    private String iconUrl;  //游戏图标链接
    private String gameName; //游戏名字
    private int gameId;      //游戏Id

    public String getIconUrl(){
        return this.iconUrl;
    }
    public void setIconUrl(String var1){
        this.iconUrl = var1;
    }

    public String getGameName(){
        return this.gameName;
    }
    public void setGameName(String var1){
        this.gameName = var1;
    }

    public int getGameId(){
        return this.gameId;
    }
    public void setGameId(int var1){
        this.gameId = var1;
    }
}
