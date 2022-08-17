package com.testboard2.configuration;

import javax.sql.DataSource;

import org.apache.catalina.core.ApplicationContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// @Configuration : 스프링부트 환경설정 클래스임을 명시. 자동으로 빈 등록.
// 이 어노테이션이 붙게 되면 @ComponentScan 이 스캔할 때 이 클래스를 @Bean으로 지정한 모든 빈들도 IoC(Inversion of Control) 컨테이너에 등록
@Configuration
@PropertySource("classpath:/application.properties")
public class DBConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /* Hikari 설정1
     * @Bean : return 되는 객체를 IoC컨테이너에 등록
     * 특별히 지정하는 이름이 없다면 IOC 컨테이너에 해당 메서드명으로 등록. 물론 이름 지정도 가능. 보통은 메서드명으로 등록. 중복 X.
     * application.properties 파일로부터 DB정보를 읽어와서 히카리 설정객체로 리턴.  
     * @ConfigurationProperties 는 위 @PropertySource파일의 해당 접두어로 시작하는 정보를 읽어온다
     * 
     */
    @Bean
    @ConfigurationProperties(prefix="spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /* Hikari 설정2
     * 해당 단계에서 DB연결은 완료
     * HikariCP 연결이 완료되었고, DB연결이 필요한 부분에서 해당 dataSource를 가지고 연결함.
     */
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = new HikariDataSource(hikariConfig());
        System.out.println(dataSource.toString());
        return dataSource;
    }

    /* Mybatis 설정1 : SqlSessionFactory <-- SqlSessionFactoryBean
     * SqlSessionFactory 생성을 위해 내부의 SqlSessionFactoryBean을 사용 
     * 이때, 데이터소스 객체를 넘겨받아서 처리해도 되고, 아니면 setDataSource(dataSource()) 이렇게 해줘도 됨.
     * 
     * 기본적인 설정 3가지
     * SetDataSource : 빌드된 DataSource를 세팅
     * SetMapperLocations : SQL 구문이 작성된 *Mapper.xml의 경로를 정확히 등록.
     * SetTypeAliasPackage : 인자로 Alias 대상 클래스가 위치한 패키지 경로.
     * 
     * 주의사항 
     * SqlSessionFactory에 저장할 config 설정시 Mapper에서 사용하고자하는 DTO, VO, Entity에 대해서 setTypeAliasesPackge 지정필요.
     * 만약 지정해주지 않는다면 alias 찾지 못한다는 오류가 발생할 수 있음.
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        //factoryBean.setMapperLocations(applicationContext.getResource("classpath:/mapper/**/*Mapper.xml"));
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*Mapper.xml"));

        /*
         * mapper에 대한 리소스는 어디서 가져오지?
         * --ApplicationContext 객체에서 가져올 수 있다.
         * --ApplicationContext는 쉽게말해 framework container 라고 생각하면 됨.
         * --ApplicationContext는 애플리케이션이 스타트해서 끝나는 순간까지 애플리케이션에서 필요한 모든 자원들을 모아놓고 관리.
         * 
         */
        factoryBean.setTypeAliasesPackage("com.testboard2.dto");


        return factoryBean.getObject();
    }
    
    /* Mybatis 설정2 : SqlSessionTemplate <-- SqlSessionFactory
     * SqlSessionTemplate 객체 생성 <<SqlSessionFactory
     * 넘겨받은 sqlSessionFactory를 통해서 객체 생성및 리턴
     * SQL 구문의 실행과 트랜잭션 등을 관리하는 바쁜 애
     * MyBatis의 sqlSession객체가 Spring+MyBatis연동 모듈에서는 sqlSessionTelmplate로 대체
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {

        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
