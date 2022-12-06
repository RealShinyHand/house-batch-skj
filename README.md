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
</div>