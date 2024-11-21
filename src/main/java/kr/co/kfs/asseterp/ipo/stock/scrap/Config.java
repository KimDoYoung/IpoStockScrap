package kr.co.kfs.asseterp.ipo.stock.scrap;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 설정값 : DB 연결정보, 테이블명
 */
@Getter
@Setter
public class Config {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbTableName;

    public static Config loadConfig(String filePath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
        }
        Config config = new Config();
        config.setDbUrl(props.getProperty("db.url"));
        config.setDbUsername(props.getProperty("db.username"));
        config.setDbPassword(props.getProperty("db.password"));
        config.setDbTableName(props.getProperty("db.tablename"));
        return config;
    }
}