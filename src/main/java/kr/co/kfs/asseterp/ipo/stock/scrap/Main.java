package kr.co.kfs.asseterp.ipo.stock.scrap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

	Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		
        if (args.length < 1) {
            System.err.println("Usage: java -jar IpoStockScrap.jar <property-file>");
            return;
        }
        try {
            Config config = Config.loadConfig(args[0]);
            Scraper scraper = new Scraper();
            List<IpoData> dataList = new ArrayList<>();

            // 스크래핑
            // 1단계 리스트페이지에서 코드 추출
            List<String> codeList = new ArrayList<>();
            for (int page = 1; page <= 3; page++) {
                String url = "http://www.ipostock.co.kr/sub03/ipo04.asp?str1=2024&str2=all&str3=&str4=&page=" + page;
                List<String> codes = scraper.scrapeAnchors(url);
                //System.out.println("codes : " +  String.join(",", codes));
                codeList.addAll(codes);
                System.out.println(url + " : " + codes.size() + " 추출");
            }
            // 2단계 코드로 상세페이지 스크래핑
            for (String code : codeList) {
            	String url = "http://www.ipostock.co.kr/view_pg/view_04.asp?code=" + code + "&schk=2";
            	System.out.println("스크래핑 url : " + url);
                IpoData data = scraper.scrapeDetails(url);
                dataList.add(data);
            }
            
            // 테이블에 저장
            IpoRepository repo = new IpoRepository(config.getDbUrl(), config.getDbUsername(), config.getDbPassword(), config.getDbTableName());
            String trackId = newTrackId();
            System.out.println("TrackId : " + trackId);
            repo.setTrackId(trackId);
            repo.insert( dataList);
            System.out.println("Data successfully scraped and stored!");

        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
    private static String newTrackId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String formattedDate = dateFormat.format(now);
        return formattedDate;
	}

}
