/*
 *  Monero Miner App (c) 2018 Uwe Post
 *  based on the XMRig Monero Miner https://github.com/xmrig/xmrig
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 * /
 */

package waterhole.miner.monero;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OldXmrMinerActivity extends Activity {

    private final static String[] SUPPORTED_ARCHITECTURES = {"aarch64", "arm64-v8a"};

    private ScheduledExecutorService svc;
    private TextView tvLog;
    private EditText edPool, edUser;
    private EditText edThreads, edMaxCpu;
    private TextView tvSpeed, tvAccepted;
    private CheckBox cbUseWorkerId;
    private boolean validArchitecture = true;

    private MiningService.MiningServiceBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_xmr_miner);

        enableButtons(false);

        // wire views
        tvLog = (TextView) findViewById(R.id.output);
        tvSpeed = (TextView) findViewById(R.id.speed);
        tvAccepted = (TextView) findViewById(R.id.accepted);
        edPool = (EditText) findViewById(R.id.pool);
        edUser = (EditText) findViewById(R.id.username);
        edThreads = (EditText) findViewById(R.id.threads);
        edMaxCpu = (EditText) findViewById(R.id.maxcpu);
        cbUseWorkerId = (CheckBox) findViewById(R.id.use_worker_id);

        // check architecture
        if (!Arrays.asList(SUPPORTED_ARCHITECTURES).contains(Build.CPU_ABI.toLowerCase())) {
            Toast.makeText(this, "Sorry, this app currently only supports 64 bit architectures, but yours is " + Build.CPU_ABI, Toast.LENGTH_LONG).show();
            // this flag will keep the start button disabled
            validArchitecture = false;
        }

        // run the service
        Intent intent = new Intent(this, MiningService.class);
        bindService(intent, serverConnection, BIND_AUTO_CREATE);
        startService(intent);
    }

    private void startMining() {
        if (binder == null) return;
        MiningService.MiningConfig cfg = binder.getService().newConfig(edUser.getText().toString(), edPool.getText().toString(),
                Integer.parseInt(edThreads.getText().toString()), Integer.parseInt(edMaxCpu.getText().toString()), cbUseWorkerId.isChecked());
        binder.getService().startMining(cfg);
    }

    private void stopMining() {
        binder.getService().stopMining();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // the executor which will load and display the service status regularly
        svc = Executors.newSingleThreadScheduledExecutor();
        svc.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                OldXmrMinerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binder != null) {
                            tvLog.setText(binder.getService().getOutput());
                            tvAccepted.setText(Integer.toString(binder.getService().getAccepted()));
                            tvSpeed.setText(binder.getService().getSpeed());
                        }
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onPause() {
        svc.shutdown();
        super.onPause();
    }

    private ServiceConnection serverConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MiningService.MiningServiceBinder) iBinder;
            if (validArchitecture) {
                enableButtons(true);
                findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMining();
                    }
                });
                findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopMining();
                    }
                });
                int cores = binder.getService().getAvailableCores();
                // write suggested cores usage into editText
                int suggested = cores / 2;
                if (suggested == 0) suggested = 1;
                edThreads.getText().clear();
                edThreads.getText().append(Integer.toString(suggested));
                ((TextView) findViewById(R.id.cpus)).setText(String.format("(%d %s)", cores, getString(R.string.cpus)));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
            enableButtons(false);
        }
    };

    private void enableButtons(boolean enabled) {
        findViewById(R.id.start).setEnabled(enabled);
        findViewById(R.id.stop).setEnabled(enabled);
    }
}
