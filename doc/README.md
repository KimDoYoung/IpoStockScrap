# IpoStockScrap

## 개요

[Ipo Stock](http://www.ipostock.co.kr/main/main.asp) 사이트에서 Scrapping을 통해서
데이터를 가져온 후 postgresql table에 저장한다.


## 기술 스펙

1. 언어 : java 1.8
2. 빌드 : gradle 
3. 결과물 : 실행가능 jar 즉 command line 실행 (예 java -jar IpoStockScrap property-file)
4. property-file에 아래와 같이 기술
```text
db.url=jdbc:postgresql://localhost:5432/fbhdb
db.username=kdy987
db.password=kalpa987!
db.tablename=public.ipo_data
```
5. lombok사용
6. sts (eclipse) 로 개발
7. git 사용 respository [https://github.com/KimDoYoung/IpoStockScrap.git](https://github.com/KimDoYoung/IpoStockScrap.git) 

## 로직

1. 아래 3개의 사이트를 순차적으로 접속
http://www.ipostock.co.kr/sub03/ipo04.asp?str1=2024&str2=all&str3=&str4=&page=1
http://www.ipostock.co.kr/sub03/ipo04.asp?str1=2024&str2=all&str3=&str4=&page=2
http://www.ipostock.co.kr/sub03/ipo04.asp?str1=2024&str2=all&str3=&str4=&page=3  
2. 각 페이지에서 앵커 추출
<a href="/view_pg/view_04.asp?code=B202405201&amp;schk=2">
3. 각 앵커에서 B202405201 코드추출
4. 수집된 앵커 리스트를 순회하면서 아래 페이지 접속
	http://www.ipostock.co.kr/view_pg/view_04.asp?code=B202405271&schk=2
5. 페이지에서 데이터 추출
6. 모든 데이터를 추출 후 db에 저장

	