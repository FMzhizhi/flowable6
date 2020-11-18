package com.zjj.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HolidayRequest20 {

    static ProcessEngine processEngine=null;
    static {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable6?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();
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
                .deploymentId("2501")
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());
        System.out.println(processDefinition.getDiagramResourceName());
    }

    //部署
    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable6?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
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
