package waterhole.miner.core.controller;

import android.content.Context;

/**
 * Created by huweidong on 2018/06/10
 * email : huwwds@gmail.com
 */

public class BaseController {
    protected ITempTask tempTask;
    protected long pollingTime = 1000L;
    protected boolean fullPower = false;

    public void setTask(ITempTask iTempTask) {
        tempTask = iTempTask;
    }

    public void startControl(final Context context) {
    }

    public void setPollingTime(long pollingTime) {
        this.pollingTime = pollingTime;
    }

}
