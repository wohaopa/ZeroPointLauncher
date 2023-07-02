/*
 * MIT License
 * Copyright (c) 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package filesystem;

import java.io.File;

import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.download.DownloadProvider;
import com.github.wohaopa.zeropointlanuch.core.tasks.instances.OnlineInstallTask;

public class InstallTaskTest {

    public static void main(String[] args) throws Exception {
        ZplDirectory.init(new File("D:\\DevProject\\JavaProject\\ZeroPointLaunch\\TestResources\\.GTNH"));
        DownloadProvider.setProvider(new DownloadProvider("http://127.0.0.1"));

        File zip = new File(
            "D:\\DevProject\\JavaProject\\ZeroPointLaunch\\TestResources\\.GTNH\\zip\\GT_New_Horizons_2.3.3_Client.zip");
        File instDir = new File(
            "D:\\DevProject\\JavaProject\\ZeroPointLaunch\\TestResources\\.GTNH\\instances\\2.3.2-Test");
        String name = "2.3.2-webTest";
        String version = "2.3.2";

        // new StandardInstallTask(zip, instDir, name, version, null).call();
        new OnlineInstallTask(new File(ZplDirectory.getInstancesDirectory(), name), name, Log::info).call();
    }

}
