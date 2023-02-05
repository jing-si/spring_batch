package spring.batch.run.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

public class PassCheckingListener implements org.springframework.batch.core.StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if(!exitCode.equals(ExitStatus.FAILED.getExitCode()))
        return new ExitStatus("PASS");
        return null;
    }
}
