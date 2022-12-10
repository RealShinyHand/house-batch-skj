package com.fastcapus.housebatchskj.job.apt;

import com.fastcapus.housebatchskj.core.repository.LawdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@Slf4j
public class GuLawdTaskLet implements Tasklet {

    private final LawdRepository lawdRepository;
    private List<String> guLawdCdList;
    private int index; //offset

    public GuLawdTaskLet(LawdRepository lawdRepository){
        super();
        this.lawdRepository = lawdRepository;
        System.out.println("스텝은 스텝 끝날 시 죽었다가 새로 만들어지는 걸까?");
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        ExecutionContext executionContext =  chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        // 현재 실행되는 청크로부터 그를 실행시킨 Step을 얻어온다. 그 스텝의 인스턴스를 가져오고, 그 스텝을 실행시키는 Job을 얻어오고 인스턴스를 얻어온다.

        if(!executionContext.containsKey("guLawdCdList")){
            log.info("GuLawdTaskLet 처음 실행");
            //해당 잡에서 이번 청크 첨 실행시
            guLawdCdList = lawdRepository.findDistinctGuLawdCd();
            executionContext.put("guLawdCdList",guLawdCdList);
            executionContext.putInt("guLawdCdListIndex",0);
        }else{
            guLawdCdList= (List<String>) executionContext.get("guLawdCdList");
            //해당 잡에서 이미 이 스텝 실행한 적이 있으면
        }

        index = executionContext.getInt("guLawdCdListIndex");

        if(index == guLawdCdList.size()){
            //다 처리가 됬다면, 종료상태를 COMPLETED로
            log.info("GuLawdTaskLet 종료");
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED; //이건 뭐 지?
        }

        //0~부터 리스트의 사이즈 까지
        String guLawdCd = guLawdCdList.get(index);
        log.info("GuLawdTaskLet 처리중, index = {} , 반환 법정동 코드 : {}",index,guLawdCd);
        executionContext.putString("guLawdCd",guLawdCd);
        executionContext.putInt("guLawdCdListIndex",++index);

        System.out.println("EXIT 상태 확인 =  " + ExitStatus.EXECUTING);
        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));
        return RepeatStatus.FINISHED;
    }
}
