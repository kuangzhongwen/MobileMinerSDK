package waterhole.miner.core.temperature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ThermalInfoUtil {

    public  static List<String> getThermalInfo() {
        String[] input = {"/system/bin/cat", "sys/class/thermal/thermal_zone0/temp"};
        ProcessBuilder pB;
        String result = "";

        try {
            //String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"};
            pB = new ProcessBuilder(input);
            pB.redirectErrorStream(false);
            Process process = pB.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) //default -1
            {
                //System.out.println(new String(re));
                result = new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<String> list = new ArrayList<>();
        list.add(result);
        return list;
    }
}