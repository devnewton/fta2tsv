package im.bci.fta2tsv;

import io.parallec.core.ParallecHeader;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author devnewton
 */
public class Fta2Tsv {

    private static final String[] TRIBUNES = {"batavie", "dlfp", "euromussels", "finss", "eurofaab", "old-dlfp"};
    private static final LocalDate START_DATE = LocalDate.of(2017, 6, 20);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(1);

    public static void main(String[] args) {
        ArrayList<String> requests = new ArrayList<>();
        for (String tribune : TRIBUNES) {
            for (LocalDate date = START_DATE; date.isBefore(END_DATE); date = date.plusDays(1)) {
                requests.add(tribune + "/" + date.toString());
            }
        }
        ParallelClient pc = new ParallelClient();
        pc.prepareHttpGet("/t/$REQ")
                .setHttpHeaders(new ParallecHeader().addPair("Accept", "text/tab-separated-values"))
                .setReplaceVarMapToSingleTargetSingleVar("REQ", requests, "bombefourchette.com")
                .execute(new ParallecResponseHandler() {
                    @Override
                    public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
                        String body = res.getResponseContent();
                        if(StringUtils.isNotBlank(body)) {
                            try {
                                String filename = StringUtils.removeStart(res.getRequest().getResourcePath(), "/t/");
                                filename = StringUtils.replace(filename, "/", "_") + ".tsv";
                                FileUtils.write(new File(filename), body, "UTF-8");
                            } catch (IOException ex) {
                                Logger.getLogger(Fta2Tsv.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        System.out.println(body);
                    }
                });

    }

    private static final DateTimeFormatter POST_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    private static final DateTimeFormatter POST_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");/*
	private static final Whitelist MESSAGE_WHITELIST = Whitelist.none().addTags("b", "i", "s", "u", "tt", "code", "spoiler");

    
    private static void retrieve(String tribune, LocalDate date) {
        String url = "http://bombefourchette.com/t/" + tribune + "/" + date.toString();
        Element debugPost = null;
        try (FileWriter fw = new FileWriter(tribune + "-" + date.toString() + ".tsv")) {
            Document doc = Jsoup.connect(url).get();
            final CSVPrinter printer = CSVFormat.TDF.print(fw);
            for (Element post : doc.select("#posts > li")) {
                debugPost = post;
                Element firstA = post.select("a:first-child").get(0);
                printer.print(firstA.attr("id"));
                LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(firstA.select(".horloge").text(), POST_TIME_FORMATTER));
                printer.print(dateTime.format(POST_DATE_TIME_FORMATTER));
                String info = "";
                for (Element infoElement : firstA.select(".info")) {
                    info = StringUtils.removeEnd(infoElement.text(), ">");
                    infoElement.remove();
                }
                String login = "";
                for (Element loginElement : firstA.select(".login")) {
                    login = StringUtils.removeEnd(loginElement.text(), ">");
                    loginElement.remove();
                }
                printer.print(info);
                printer.print(login);
                firstA.replaceWith(TextNode.createFromEncoded(firstA.html(), null));

                for (Element element : doc.body().children().select(":not(a,b,i,s,u,tt,code,spoiler)")) {
                    element.replaceWith(TextNode.createFromEncoded(element.toString(), null));
                }
                for (Element a : post.select("a")) {
                    a.replaceWith(TextNode.createFromEncoded(a.attr("href"), null));
                }
                		Cleaner cleaner = new Cleaner(MESSAGE_WHITELIST);
		String message = cleaner.clean(Jsoup.parse(post.html())).html();

                printer.print(message);
                printer.println();
            }
        } catch (Exception e) {
            System.err.println("Cannot retrieve " + url);
            System.err.println(debugPost);
            System.err.println(e);
        }
    }*/
}
