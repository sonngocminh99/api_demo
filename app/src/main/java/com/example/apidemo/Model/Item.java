package com.example.apidemo.Model;

import androidx.annotation.NonNull;

public class Item {
    private String objectId;
    private String name;
    private String createDate;
    private String updateDate;

    @NonNull
    @Override
    public String toString() {
        return "ObjectId: "+ this.objectId+"\nName: "+this.name+"\nCreateDate: "+this.createDate+"\nUpdateDate: "+this.updateDate;
    }

    public Item(String objectId, String createDate, String updateDate, String name) {
        this.objectId = objectId;
        this.name = name;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Item() {
        this.objectId = "";
        this.name = "";
        this.createDate = "";
        this.updateDate = "";
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
