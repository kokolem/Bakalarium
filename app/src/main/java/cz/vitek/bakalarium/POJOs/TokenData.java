package cz.vitek.bakalarium.POJOs;

import com.google.gson.annotations.SerializedName;

public class TokenData {

    @SerializedName("Type")
    private String type;

    @SerializedName("ID")
    private String ID;

    @SerializedName("Salt")
    private String salt;

    public TokenData(String type, String ID, String salt) {
        this.type = type;
        this.ID = ID;
        this.salt = salt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "type='" + type + '\'' +
                ", ID='" + ID + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
