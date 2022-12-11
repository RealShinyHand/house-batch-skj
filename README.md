# house-batch-skj

<h2>좋은 배치 프로그램 설계하기</h2>
<ul>
    <li>재사용 가능하다.</li>
    <li>재시작 가능해야한다. &ac; 언제든 돌려도 똑같은 결과가 발생해야한다.<br/><i>파라메터가 같다면</i></li>
</ul>

<h2>기능</h2>
<table>
<thead>
<tr>
<th>기능</th>
<th>정의</th>
<th>배치 주기</th>
<th>데이터 저장</th>
</tr>
</thead>
<tbody>
<tr>
<td>1.동 코드 마이그레이션 배치</td>
<td>(데이터 생성용)으로 법정통 파일을 DB에 저장한다.</td>
<td>최초,데이터가 수정되었을 시 </td>
<td>법정동 파일을 DB table에 저장한다. .... <p>공공데이터꺼 db에 저장해도 되던가?.. 서비스 운영할 꺼 아니라서 가능</p></td>
</tr>
<tr>
<td>2.실거래가 수집 배치 설계</td>
<td>매일 실거래가 정보를 가져와 DB에 저장</td>
<td>매일 새벽 1시</td>
<td>reader : 법정동 '구' 코드 불러오기<br/>
    processor : '구' 마다 현재 월에 대한 API 호출<br/>
    writer : 새롭게 생성된 실거래가 정보만 DB에 upsert
</td>
</tr>
<tr>
<td>3.실거래가 알림 배치</td>
<td>유저가 관심 설정한 구에 대해 실거래가 정보를 알린다</td>
<td>매일 오전 8시</td>
<td>reader : 유저 관심 테이블 & 아파트 거래 테이블 조회-> 알림 대상 추출<br/>
    processor : 데이터 -> 전송용 데이터로 변환<br/>
    writer : 전송 인터페이스 구현
</td>
</tr>
</tbody>


</table>

<div>
<h3>동 코드 마이그레이션 배치</h3>
<h5>1. https://www.code.go.kr/index.do 에서 받은 파일을 메모장으로 열고 UTF-8로 저장 후 resources 에 저장</h5>
<h5>2. Configuration Class 설정
    <p style="background-color:#EEEEEE;">
<div>
<pre>
@Configuration
@EnableJpaAuditing
public class HouseBatchJpaConfig {
}
</pre>
<p>JPA Auditing 기능을 설정한다. JPA 에서 지원하는 자동 로직 기능을 이용할 수 있음 <br/>
여기서는 created와 updated를 위해 사용한다.</p>
</div>
<h5>3.엔티티 클래스 생성 - Lawd entity, Repository  생성</h5>
<div>Lawd Entity 구현 시 @Created,@LastUpdated 는 삽입 수정 시 해당 필드에 시간을 기록</div>
<div>사용하기 위해서는 JpaAuditing 기능을 활성화 해야한다.</div>

<h5>4. @EnableBatchProcessing 추가</h5>
<div>스프링 배치를 구현하는데 필요한, JobBuilderFactory, 잡생성 등을 사용할 수 있게 해준다.</div>
<h5>5. FlatFileItemReaderBuilder 와 이것에 사용할 FieldSetMapper 구현,JobParameterValidator도 구현</h5>
<div>
아이템 리더 -  package com.fastcapus.housebatchskj.job.lawd; <br/>
필드 셋 맵퍼 - package com.fastcapus.housebatchskj.job.lawd;<br/>
바리데이터 - package com.fastcapus.housebatchskj.job.validator;
</div>
<h5>LawDto 를 read하고 write부분에서는 단순 출력만하여 테스트한다.</h5>
<pre>
@Configuration
@Slf4j
@RequiredArgsConstructor
public class LawdInsertJobConfig {
private final JobBuilderFactory jobBuilderFactory;
private final StepBuilderFactory stepBuilderFactory;

    @Bean(name="lawdInsertJob")
    public Job lawdInsertJob(
            @Qualifier("lawdInsertStep") Step lawdInsertStep
    ){
        return jobBuilderFactory.get("lawdInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathParameterValidator())
                .start(lawdInsertStep)
                .build();
    }
    @Bean("lawdInsertStep")
    @JobScope
    public Step lawdInsertStep(
            @Qualifier("flatFileItemReader") FlatFileItemReader flatFileItemReader,
            @Qualifier("flatFileItemWriter") ItemWriter flatFileItemWriter
    ){
        return stepBuilderFactory.get("lawdInsertStep")
                .<LawdDto,LawdDto>chunk(1000) //4만 6000개를 천 개씩 처리
                .reader(flatFileItemReader)
                .writer(flatFileItemWriter)
                .build(); //파일을 읽고 바로 쓰기에 프로세서 x
    }

    @Bean("flatFileItemReader")
    @StepScope
    public FlatFileItemReader<LawdDto> flatFileItemReader(@Value("#{jobParameters['filePath']}") String filePath){
        return new FlatFileItemReaderBuilder<LawdDto>()
                .name("flatFileItemReader")
                .delimited() //딜리미터 사용하기 위해서
                .delimiter("\t")// 탭으로 구분 1111000000	서울특별시 종로구   존재 탭탭탭으로 구분하는 거 맞음
                .names(LawdFieldSetMapper.LAWD_CD,LawdFieldSetMapper.LAWD_DONG,LawdFieldSetMapper.EXISTS) //mapper에서 토크나이저한 토큰들에 대해 순서대로 이름을 부여
                .linesToSkip(1)
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

    @Bean("flatFileItemWriter")
    @StepScope
    public ItemWriter<LawdDto> flatFileItemWriter(){
        return new ItemWriter<LawdDto>() {
            @Override
            public void write(List<? extends LawdDto> items) throws Exception {
                items.forEach(System.out::println);
            }
        };
    }
}
</pre>
<div>
하다가 매우 이상한 현상을 경험하였다. 토큰나이저 예측이 3인데 실제로는 1이여서 오류가 났었다.
LAWD_CODE.txt 의 딜리미터가 탭이아니라 공백으로 바껴있는 것이다. ......
그래서 그냥 파일 다시 받아서 저장 시키닌 탭으로 인식되어 정상작동 하였다. 

프로그램 실행시킬 때의 설정은 다음과 같다. <br/>
vmOptions : -Dspring.profiles.active=local <br/>
program Argument : --spring.batch.job.names=lawdInsertJob -filePath=LAWD_CODE.txt<br/>
프로그램 알규먼트는 Main 함수의 인자로 넘어오는 부분인데, 스프링 에플리케이션이 알아서 잘 처리하겠지만
-- 이렇게 하이픈을 2개하면 application.yml에 설정한 부분에 사용된다고 한다.
<br/>?? 솔직히 차이 잘 모른다.

<br/><br/>
실제 빌드하고 실행시키기 위해서는 다음과 같이 하여야한다.
java -jar -Dspring.profiles.active=local house-batch-skj-0.0.1-SNAPSHOT.jar --spring.batch.job.names=lawdInsertJob -filePath=LAWD_CODE.txt
이러면 뭔가 차이가 느껴지긴하는데... ㅎㅎ 
하여튼 VM argument는 jvm 에 전해져 VM 인프라를 구성해 무엇을 할지 결정하고, 더 나아가 heap size, 디버깅 설정등을 할 수 있다.
<br/>Program Argument는 jar실행시 main함수의 인자로 정해지는 파라메터로 -- 하이픈 2개 사용하면 spring boot app 개발시 application.yml 속성 값을 오버라이딩 할 수 있다.
<br/>
<br/>이렇게 안하고 그냥 환경변수(export a=anyeonhaseyo) 써서 하는 경우도 있다.
<br/>사용할 떄는 spring.somting={a:-jalgaseyo} => a환경변수 값, 없으면 -뒤에 부분
</div>
</div>
<h5>6. Writer 구현을 하여 DB에 저장 , Service 개발</h5>
<div>
<pre>
    @Transactional
    public void upsert(LawdDto lawdDto){
        Lawd saved = lawdRepository.findByLawdCd(lawdDto.getLawdCd())
                //.orElseGet(()->new Lawd());
                .orElse(mapLawdDtoToLawd(lawdDto));
        lawdRepository.save(saved);
    }
</pre>
우선 동 코드를 이용하여 DB 조회, 없으면 그냥 저장
있으면 받아온 데이터로 업데이트 후 저장
</div>
<hr/>
<h3>아파트 매매 정보 받아오기</h3>
package com.fastcapus.housebatchskj.job.apt; -- api Job Configuration<br/>
package com.fastcapus.housebatchskj.adapter; -- api 호출하는 URL Resource 주는 객체<br/>

<ul>
<li>StaxEventItemReader 를 통해 XML 파싱
<pre>
    @StepScope
    @Bean("aptDealResourceReader")
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
            Jaxb2Marshaller aptDealDtoMarshaller,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobParameters['lawdCd']}") String lawdCd
    ){
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartApiResource.getResources(lawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }
</pre>
<div>
FragmentRootElements : xml 태그 요소 지정, 여기서는 아파트 매매 정보가 &lt item&gt ... &lt /item&gt 안에 있음으로 요소의 루트를 item으로 지정
</div>
<div>
marshaller : 맵핑할 클래스를 지정
</div>
</li>
<li>Jaxb2Marshaller 사용법 
<div><br/>
패키지는 이거 3개 추가하니깐 되더라. <br/>
	implementation 'org.springframework:spring-oxm' <br/>
	implementation 'javax.xml.bind:jaxb-api:2.3.1' <br/>
	implementation 'javax.activation:activation:1.1.1' <br/>
</div>
<pre>    @StepScope
    @Bean
    public Jaxb2Marshaller aptDealDtoMarshaller(){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }
----------------------

@ToString
@Getter
@XmlRootElement(name="item")
public class AptDealDto {

    //거래 금액
    @XmlElement(name="거래금액")
    private String dealAmount;
    //건축년도
    @XmlElement(name="건축년도")
    private Integer builtYear;
    //년
........
}
</pre>
</li>
<li>
CompositeJobParametersValidator
<div>
Job 실행 시 파라메터를 검사하는 것을 지정할 수 있는데, 
JobBuilderFactory에는 하나 밖에 지정을 못하더라.
그래서 아래와 같이 해야한다.
<pre>
    private JobParametersValidator validator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(new YearMonthParameterValidator(),new LawdCdParameterValidator()));
        return validator;
    }
</pre>
구조를 보니깐, GoF CompositePattern이랑 동일한다. 
Component 와 Componet를 관리하는? 객체가 동일 메서드(validate)로 클라이언트에서 호출되나, 
Composite는 자신의 필드에 속한 모든 Leaf를 호출하는 식으로 되어 있더라. 

<li>
StepContribution,ChunkContext
<div>
Tasklet에서는  위와 같은 파라메터를 받는다.
위의 두 인자를 통해, Step의 상태 설정과 Job,Step 간 데이터를 공유할 수 있다. 
근데 나는 많은 문제를 겪었고 이를 여기서 서술하겠다. <br/>

contribution.setExitStatus(new ExitStatus("QWE"));
chunkContext.getStepContext().getStepExecution().setExitStatus(ExitStatus.FAILED);
https://devfunny.tistory.com/483
위 블로그에서 contribution 과 chunkContext 차이는
아직 키밋되지 않은 현재 트랜잭션에 대한 정보, 실행 시점의 잡 상태 제공이라고 한다. 
그래서 Step의 상태는 누가 조절하는데? 가 궁금하여 실험해보니
위 코드에서는 QWE가 나왔다. 그렇다고 FAILED가 완전히 적용이 안되는 것은 아니였다. QWE 상태 지정 코드를
지우면 FAILED라고 리스터 afterStep에 뜬다. 우선 순위차이인 것이였다. 
<br/>
<br/>
법정동 코드당 API 요청을 해야했기에 Step을 나눠 첫 Step에서는 법정동 코드를 읽어오고
두번쨰에서는 법정동 코드롤 API 호출을 해, 아파트 매매 정보를 받아왔다. 거기서 Chunk를 순차적으로 하기 위해
첫 스텝에서 하나의 법정동 코드만 넘겨주고, 두번쨰 스텝에서는 하나의 법정동 코드로 API호출하게 Flow를 만들어서 했다.
첫 스텝에서 스텝 상태를 조절해 FLOW를 구현했는데, 
ExitStatus.EXECUTING 을 보고 나는 의미상으로 아직 끝나지 않았고 다시 호출 시 다음 법정동 코드를 주는 로직이길래 이 상태 코드를
넘겨주면 될 줄 알았는데.... 결과는 COMPLETED로 변환되어 상태가 반환되었다??? 
그래서 결국 new ExitStatus("CONTINUABLE")로 바꿨다.
</div>
<div>
나머지는 아파트에 맞게 거래내역 저장하면된다.
</div>
</li>

<h3>실거래가 알림배치</h3>
실제 알림을 보내는 줄 알았는데.. 안보낸다.
위 에서 작성한 것이랑 로직 차이도 별로 없고 연습할 이유도 없는 것 같다 . 따라서 안한다.
특이한 점이 있다면 Reader 과정에서 쓰는 객체가 다르다는 것이다. 이런 거는 예전에 알림 배치 구현할 떄 아래 블로그를 많이
참조 했었다.
<br/>
https://khj93.tistory.com/entry/Spring-Batch%EB%9E%80-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B3%A0-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
</div>
<h2>종료</h2>

</li>
</ul>