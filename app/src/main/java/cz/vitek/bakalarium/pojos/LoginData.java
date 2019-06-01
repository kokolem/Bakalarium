package cz.vitek.bakalarium.pojos;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("UserName")
    private String realName;

    public LoginData(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
