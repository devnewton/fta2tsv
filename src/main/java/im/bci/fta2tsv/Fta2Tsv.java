package im.bci.fta2tsv;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author devnewton
 */
public class Fta2Tsv {

    private static final String[] TRIBUNES = {"batavie", "dlfp", "euromussels", "finss", "eurofaab", "old-dlfp"};
    private static final LocalDate START_DATE = LocalDate.of(2006, 12, 01);
    private static final LocalDate END_DATE = LocalDate.now();
    private static final File OUTPUT_DIR = new File("output");

    public static void main(String[] args) throws InterruptedException {
        OkHttpClient client = new OkHttpClient();
        for (final String tribune : TRIBUNES) {
            File outputDir = new File(OUTPUT_DIR, tribune);
            outputDir.mkdirs();
            for (LocalDate dateIterator = END_DATE; dateIterator.isAfter(START_DATE); dateIterator = dateIterator.minusDays(1)) {
                final LocalDate date = dateIterator;
                final File outputFile = new File(outputDir, date + ".tsv");
                if (outputFile.length() <= 0) {
                    Request request = new Request.Builder()
                            .url("http://bombefourchette.com/t/" + tribune + "/" + date)
                            .header("Accept", "text/tab-separated-values")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException ioe) {
                            System.err.println("Cannot retrieve " + tribune + " at " + date + " :");
                            System.err.println(ioe);
                        }

                        @Override
                        public void onResponse(Call call, Response res) throws IOException {
                            if (res.isSuccessful()) {
                                String body = res.body().string();
                                if (StringUtils.isNotBlank(body)) {
                                    try {
                                        FileUtils.writeStringToFile(outputFile, body, "UTF-8");
                                        System.out.println("Retrieved " + tribune + " at " + date);
                                    } catch (Exception ex) {
                                        System.err.println("Cannot retrieve " + tribune + " at " + date + " :");
                                        System.err.println(ex);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }

}
