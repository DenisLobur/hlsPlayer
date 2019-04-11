package com.denys.hlsplayer.model;

public class MainPlaylistModel {

  public MainPlaylistModel(String groupID, String uri) {
    this.groupID = groupID;
    this.uri = uri;
  }

  private String groupID;
  private String uri;

  public String getGroupID() {
    return groupID;
  }

  public void setGroupID(String groupID) {
    this.groupID = groupID;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
