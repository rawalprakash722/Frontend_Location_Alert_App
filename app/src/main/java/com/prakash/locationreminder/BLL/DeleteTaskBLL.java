package com.prakash.locationreminder.BLL;



import com.prakash.locationreminder.interfaces.Url;
import com.prakash.locationreminder.models.LongLat;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class DeleteTaskBLL {
    private LongLat longLat;
    private String longlatId;
    private String cookie;
    private boolean isSuccess;

    public DeleteTaskBLL(LongLat longLat, String longlatId, String cookie) {
        this.longLat = longLat;
        this.longlatId = longlatId;
        this.cookie = cookie;
    }

    public boolean deleteTask() {
        Call<Void> voidCall = Url.getEndPoints().deletelonglat(cookie, longlatId);
        try {
            Response<Void> response = voidCall.execute();

            if(response.isSuccessful()){
                isSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

}
