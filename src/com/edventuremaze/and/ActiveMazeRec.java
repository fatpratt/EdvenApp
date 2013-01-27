package com.edventuremaze.and;

import java.io.Serializable;

/**
 * Author: Brian Pratt
 */
public class ActiveMazeRec implements Serializable {

    private static final long serialVersionUID = 1L;

    private long fId;
    private String fMazeName;
    private String fMazeAuthor;
    private String fExpiredStr;
    private String fMazeId;

    public long getId() {
        return fId;
    }

    public void setId(long id) {
        this.fId = id;
    }

    public String getMazeName() {
        return fMazeName;
    }

    public void setMazeName(String mazeName) {
        this.fMazeName = mazeName;
    }

    public String getMazeAuthor() {
        return fMazeAuthor;
    }

    public void setMazeAuthor(String mazeAuthor) {
        this.fMazeAuthor = mazeAuthor;
    }

    public String getExpiredStr() {
        return fExpiredStr;
    }

    public void setExpiredStr(String expiredStr) {
        this.fExpiredStr = expiredStr;
    }

    public String getMazeId() {
        return fMazeId;
    }

    public void setMazeId(String mazeId) {
        this.fMazeId = mazeId;
    }

}
