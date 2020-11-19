package com.zjj.flowable;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HolidayRequest20 {

    static ProcessEngine processEngine=null;
    static Scanner scanner=new Scanner(System.in);
    static {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable6?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();
    }

    //https://www.cnblogs.com/nanstar/p/11959389.html  rest_api
    //历史任务节点查询
    @Test
    public void test5(){
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId("2501")
                        .finished()
                        .orderByHistoricActivityInstanceEndTime().asc()
                        .list();
        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityId() + " took "
                    + activity.getDurationInMillis() + " milliseconds");
        }
    }

    //根据协助人完成任务
    @Test
    public void test4(){
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holidayRequest").taskAssignee("zhijiaju").singleResult();
        taskService.complete(task.getId());


    }

    //我们通过TaskService创建一个TaskQuery，并且配置查询只返回管理员组的任务：
    @Test
    public void test3(){

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i=0; i<tasks.size(); i++) {
            System.out.println((i+1) + ") " + tasks.get(i).getName());
        }

        System.out.println("Which task would you like to complete?");
        int taskIndex = Integer.valueOf(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants " +
                processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");
        //完成任务
        boolean approved = scanner.nextLine().toLowerCase().equals("y");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);
    }

    /**
     * 启动流程实例
     */
    @Test
    public void test2(){
        Scanner scanner= new Scanner(System.in);
        System.out.println("Who are you?");
        String employee = "zhijiaju";
        System.out.println("How many holidays do you want to request?");
        Integer nrOfHolidays = 5;
        System.out.println("Why do you need them?");
        String description = "got to home";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        System.out.println(processInstance.getProcessVariables());
        System.out.println(processInstance.toString());
    }

    @Test
    public void test1(){
        //获取流程定义信息--act_re_procdef
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId("1")
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());
        System.out.println(processDefinition.getDiagramResourceName());
    }

    //部署
    public static void main(String[] args) {
//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable6?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT")
//                .setJdbcUsername("root")
//                .setJdbcPassword("root")
//                .setJdbcDriver("com.mysql.jdbc.Driver")
//                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//        ProcessEngine processEngine = cfg.buildProcessEngine();
        //部署  act_ge_bytearry   act_re_deployment
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getKey());
        System.out.println(deployment.getTenantId());
        System.out.println(deployment.getDeploymentTime());
    }

}
