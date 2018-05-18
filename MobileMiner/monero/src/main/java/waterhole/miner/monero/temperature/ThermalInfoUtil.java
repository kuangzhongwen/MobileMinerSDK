package waterhole.miner.monero.temperature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ThermalInfoUtil {

    //    public static List<String> getThermalInfo() {
//        List<String> result = new ArrayList<>();
//        BufferedReader br = null;
//
//        try {
//            File dir = new File("/sys/class/thermal/");
//
//            File[] files = dir.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File file) {
//                    if (Pattern.matches("thermal_zone[0-9]+", file.getName())) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//            final int SIZE = files.length;
//            String line = null;
//            String type = null;
//            String temp = null;
//            for (int i = 0; i < SIZE; i++) {
//                br = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone" + i + "/type"));
//                line = br.readLine();
//                if (line != null) {
//                    type = line;
//                }
//
//                br = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone" + i + "/temp"));
//                line = br.readLine();
//                if (line != null) {
//                    long temperature = Long.parseLong(line);
//                    if (temperature < 0) {
//                        temp = "Unknow";
//                    } else {
//                        temp = (float) (temperature / 1000.0) + "°C";
//                    }
//
//                }
//
//                result.add(type + " : " + temp);
//            }
//
//            br.close();
//        } catch (FileNotFoundException e) {
//            result.add(e.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return result;
//    }

    static List<String> getThermalInfo() {
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