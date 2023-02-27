package spring.batch.run.job;

import org.springframework.batch.item.*;

import java.util.List;

public class CustomItemStreamReader implements ItemStreamReader<String> {
    private final List<String> items;
    //item을 읽다가 조건에 의해서 실패하면 실패한 시점의 데이터를 DB에 저장할 것임
    private int index = -1;

    //재시작 여부 기본값은 하지 않겠다.
    private boolean restart = false;

    public CustomItemStreamReader(List<String> items){
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        //read 할때마다 데이터를 하나씩 읽어야 함
        String item = null;
        if(this.index < this.items.size()){
            item = this.items.get(index++);

        }

        //실패 조건
        if(this.index == 6 && !restart){
            throw new RuntimeException("Restart is required");
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        //초기화 작업을 해줌
        //Open이 실행되면 DB에 index가 있는 지 확인 하고
        //다시 재시작 할 수 있도록 restart를 true로 변경
        if(executionContext.containsKey("index")){
            index = executionContext.getInt("index");
            this.restart = true;
        }else{
            index = 0;
            executionContext.put("index",index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("index",index);
    }

    @Override
    public void close() throws ItemStreamException {
        //예외가 발생해서 Job이 실패하면 close가 실행됨
        System.out.println("close");
    }
}
