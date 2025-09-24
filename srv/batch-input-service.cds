using {com.customer.captemplateproject as po} from '../db/schema';

service BatchInputService {
    entity JobInstance          as projection on po.job_instance;
    entity JobExecution         as projection on po.job_execution;
    entity JobExecutionContext  as projection on po.job_execution_context;
    entity JobExecutionParams   as projection on po.job_execution_params;
    entity StepExecution        as projection on po.step_execution;
    entity StepExecutionContext as projection on po.step_execution_context;
}
