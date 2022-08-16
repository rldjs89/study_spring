package com.testboard2.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// @Configuration : 스프링부트 환경설정 클래스임을 명시. 자동으로 빈 등록.
// 이 어노테이션이 붙게 되면 @ComponentScan 이 스캔할 때 이 클래스를 @Bean으로 지정한 모든 빈들도 IoC(Inversion of Control) 컨테이너에 등록
@Configuration
@PropertySource("classpath:/application.properties")
public class DBConfiguration {

    //Hikari 설정1
    //@Bean : return 되는 객체를 IoC컨테이너에 등록
    @Bean
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }
    //Hikari 설정2
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = new HikariDataSource(hikariConfig());
        System.out.println(dataSource.toString());
        return dataSource;
    }

    //Mybatis 설정1 : SqlSessionFactory <-- SqlSessionFactoryBean
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        System.out.println(factoryBean.toString());
        return factoryBean.getObject();
    }
    //Mybatis 설정2 : SqlSessionTemplate <-- SqlSessionFactory
    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }
}
