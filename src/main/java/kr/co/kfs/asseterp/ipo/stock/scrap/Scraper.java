package kr.co.kfs.asseterp.ipo.stock.scrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * IpoStock 웹페이지 스크래핑
 */
public class Scraper {
    public List<String> scrapeAnchors(String url) throws IOException {
        List<String> codes = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements anchors = doc.select("a[href*=/view_pg/view_04.asp]");
        for (Element anchor : anchors) {
            String href = anchor.attr("href");
            String code = href.split("code=")[1].split("&")[0];
            codes.add(code);
        }
        return codes;
    }

    public IpoData scrapeDetails(String url) throws IOException {
    	String text = "";
        Document doc = Jsoup.connect(url).get();
        IpoData ipoData = new IpoData();        
        // stockname, code  추출
        ipoData.setStockName(findWithCssQuery(doc, "strong.view_tit"));
        ipoData.setStockCode(findWithCssQuery(doc, "strong.view_txt01"));
        
        //ceo, 대표전화
        ipoData.setCeo(findWithCssQuery(doc, "td:contains(대표이사) + td"));
        ipoData.setPhoneNumber(findWithCssQuery(doc, "td:contains(대표전화) + td"));
        
        //공모주식수, 액면가
        ipoData.setTotalIpoShares(findWithCssQuery(doc, "td:contains(공모주식수) + td"));
        ipoData.setFaceValue(findWithCssQuery(doc, "td:contains(액면가) + td"));
        
        //희망공모가액,청약경쟁률,확정공모가
        ipoData.setDesiredIpoPrice(findWithCssQuery(doc, "td:contains((희망)공모가격) + td"));
        ipoData.setSubscriptionCompetitionRate(findWithCssQuery(doc, "td:contains(청약경쟁률) + td"));
        ipoData.setFinalIpoPrice(findWithCssQuery(doc, "td:contains((확정)공모가격) + td"));
        ipoData.setIpoProceeds(findWithCssQuery(doc, "td:contains((희망)공모금액) + td"));
        //주관사
        String leadManager = findLeadManager(doc);
        ipoData.setLeadManager(leadManager);
        //수
        Element table = findScheduleTable(doc);
        if (table != null) {
        	text = findInElement(table,"td:contains(수요예측일) + td");
        	ipoData.setDemandForecastDate(text);
        	
        	text = findInElement(table,"td:contains(공모청약일) + td");
        	ipoData.setIpoSubscriptionDate(text);
        	
        	text = findInElement(table,"td:contains(환불일) + td");
        	ipoData.setRefundDate(text);

        	text = findInElement(table,"td:contains(납일일) + td");
			if (text == null || text.isEmpty()) {
				text = findInElement(table, "td:contains(납입일) + td");
			}
        	ipoData.setPaymentDate(text);
        	text = findInElement(table,"td:contains(상장일) + td");
        	ipoData.setListingDate(text);   	
        }
        return ipoData;
    }
    private String findInElement(Element elm, String cssQuery) {
		Element found = elm.selectFirst(cssQuery);
		return found != null ? found.text() : "";
    }
    private Element findScheduleTable(Document doc) {
    	
        Elements tables = doc.select("table.view_tb");

        for (Element table : tables) {
            // 첫 번째 row의 첫 번째 td 찾기
            Element firstRowFirstTd = table.selectFirst("tr:first-of-type td:first-of-type");

            if (firstRowFirstTd != null && firstRowFirstTd.text().contains("대규모 IR일정")) {
            	return table;
            }
        }
        return null;
	}

	/**
     * 주관사를 찾는다.
     * @param doc
     * @return
     */
	private String findLeadManager(Document doc) {
		
		String leadManager = "";
        Elements tables = doc.select("table.view_tb");

        // 조건에 맞는 테이블 탐색
        for (Element table : tables) {
            // 첫 번째 <tr>이 '증권회사, 배정수량...'인지 확인
            Element headerRow = table.selectFirst("tr");
            if (headerRow != null && headerRow.text().contains("증권회사")) {
                
            	Elements trs= table.select("tr");
            	int i = 0; 
            	for(Element tr : trs) {
					if (i > 0) {
						Elements tds = tr.select("td");
						leadManager += tds.get(0).text() + ",";
					}
					i++;
            	}
            	if (leadManager.length() > 0 && leadManager.endsWith(",")) {
            		leadManager = leadManager.substring(0, leadManager.length() - 1);
            	}
                break;// 테이블을 찾았으면 작업 종료
            }
        }
        return leadManager;
	}

	private String findWithCssQuery(Document doc, String cssQuery) {
		Element elm = doc.selectFirst(cssQuery);
        String stockName = elm != null ? elm.text().replaceAll("\u00A0", " ") : "";
		return stockName;
	}
}