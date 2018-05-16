/*
 *  File Tools for Monero Miner
 *  (c) 2018 Uwe Post
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

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class OldMinerConfigTools {

    public static String loadConfigTemplate(Context context) {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = context.getAssets().open("config.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeConfig(String configTemplate, String poolUrl, String username, int threads, int maxCpu, String privatePath) {
        String config = configTemplate.replace("$url$", poolUrl)
                .replace("$username$", username)
                .replace("$threads$", Integer.toString(threads))
                .replace("$maxcpu$", Integer.toString(maxCpu));
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(privatePath + "/config.json"));
            writer.write(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) writer.close();
        }
    }
}
