package spring.batch.run.job;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;



public class CustomTasklet implements Tasklet {

    private long sum;
    //이렇게 락을 만들지 않고 Thread가 가지고 있는 lock을 사용해도 된다.
    //모든 객체들은 하나의 lock을 갖는다.
    private Object lock = new Object();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        synchronized (lock){
            for(int i = 0; i < 10000000;i++){
                sum++;
            }

            System.out.println(String.format("%s has been executed on thread %s"
                    ,chunkContext.getStepContext().getStepName()
                    ,Thread.currentThread().getName()));
            System.out.println(String.format("sum : %s",sum));
        }

        //병렬로 실행할 때 가장 문제가 되는게 동시성 문제
        //공유 데이터에 여러 쓰레드들이 접근하면
        //쓰기 작업을 할 때 중복적으로, 동시적으로 실행되면서
        //값이 섞여 버림

        //쓰레드 동시성 문제 해결 전 코드
       /* for(int i = 0; i < 10000000;i++){
            sum++;
        }

        System.out.println(String.format("%s has been executed on thread %s"
                ,chunkContext.getStepContext().getStepName()
                ,Thread.currentThread().getName()));
        System.out.println(String.format("sum : %s",sum));
        */
        return null;
    }
}
