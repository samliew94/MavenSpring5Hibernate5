/** @author Sam Liew 11 Nov 2022 8:23:27 PM */
package sam.configurations;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class HibernateConf {
	
	public HibernateConf() {
		LogManager logManager = LogManager.getLogManager();
		Logger logger = logManager.getLogger("");
		logger.setLevel(Level.OFF);
	}
	
	@Bean
	public LocalSessionFactoryBean sessionFactory()
	{
	    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	    sessionFactory.setDataSource(dataSource());
	    sessionFactory.setPackagesToScan("sam.models");
	    sessionFactory.setHibernateProperties(hibernateProperties());

	    return sessionFactory;
	}

	@Bean
	public DataSource dataSource()
	{
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://localhost:3306/lis_local?useSSL=false");
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setUsername( "root" );
        config.setPassword( "root" );
		
		DataSource dataSource = new HikariDataSource(config); // default to HikariCP
		
//		BasicDataSource dataSource = new BasicDataSource(); // optionally use tomcat-dbcp
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/lis_local?useSSL=false");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");

	    return dataSource;
	}
	
	private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
          "hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty(
          "hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect"); // change this if using mysql 8

        return hibernateProperties;
    }
}
