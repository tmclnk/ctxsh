package be.shouldyou.ctxsh;

import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import be.shouldyou.example.MyConfig;

/**
 * Launches a shell with a Spring ApplicationContext in a terminal.
 */
public class Shell {
	private static final Logger logger = LoggerFactory.getLogger(Shell.class);
	public static void main(String[] args) throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager(Shell.class.getClassLoader());
		final String engineName = "groovy";

		logger.info("Using {}", engineName);
		ScriptEngine engine = factory.getEngineByName(engineName);
	
		int ret = 0;
		try(AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfig.class)){
			// bind all the beans in the app context 
			// to the script so we can access them by name
			for(String beanName : ctx.getBeanDefinitionNames()){
				Object bean = ctx.getBean(beanName);
				engine.put(beanName, bean);
				logger.info("Bound {} to {}", beanName, bean);
			}
			
			// create a transaction to do all our stuff in
			PlatformTransactionManager platformTransactionManager = ctx.getBean(PlatformTransactionManager.class);
			TransactionTemplate txTemplate = new TransactionTemplate(platformTransactionManager);
			
			EntityManager em = ctx.getBean(EntityManager.class);
			engine.put("em", em);
			
			ret = txTemplate.execute( txStatus ->{
				engine.put("txStatus", txStatus);
				engine.put("commit", false);
			
				logger.info("txStatus variable bound");
				
				InputStreamReader reader = new InputStreamReader(System.in);
				
				try {
					engine.eval(reader);
					boolean commit = (boolean)engine.get("commit");
					logger.info("commit={}", commit);
					
					if(commit){
						logger.info("Committing...");
					} else {
						txStatus.setRollbackOnly();;
						logger.info("Rolling back...");
					}
				} catch (ScriptException e) {
					// this will cause a transaction rollback
					txStatus.setRollbackOnly();;
					logger.info("Rolling back...");
					e.printStackTrace();
					return 1;
				}
			
				return 0;
			});
		}
	
		System.exit(ret);
	}
}
