package waterhole.miner.core.controller;

import android.content.Context;

/**
 * Created by huweidong on 2018/06/10
 * email : huwwds@gmail.com
 */
public class BaseController {

    ITempTask tempTask;
    long pollingTime = 1000L;
    boolean fullPower = false;

    public void setTask(ITempTask iTempTask) {
        tempTask = iTempTask;
    }

    public void startControl(final Context context) {
    }

    public void setPollingTime(long pollingTime) {
        this.pollingTime = pollingTime;
    }

}
