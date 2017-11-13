package be.shouldyou.ctxsh;

import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import be.shouldyou.example.MyConfig;

public class GroovyShell implements Closeable{
	private static final Logger logger = LoggerFactory.getLogger(GroovyShell.class);
	private static final String engineName = "groovy";
	
	private final AnnotationConfigApplicationContext appCtx;
	private final ScriptEngineManager factory = new ScriptEngineManager(Shell.class.getClassLoader());
	private final ScriptEngine engine = factory.getEngineByName(engineName);

	/**
	 * Initialize an {@link ApplicationContext} with the given config
	 * and bind all of its beans to the {@link #engine}.
	 * @param configClass a Spring {@link Configuration} class
	 */
	public GroovyShell(Class<?> configClass) {
		logger.info("Using {}", engineName);
		
		appCtx = new AnnotationConfigApplicationContext(MyConfig.class);
		// bind all the beans in the app context 
		// to the script so we can access them by name
		for(String beanName : appCtx.getBeanDefinitionNames()){
			Object bean = appCtx.getBean(beanName);
			engine.put(beanName, bean);
			logger.info("Bound {} to {}", beanName, bean);
		}
		
		// create a transaction to do all our stuff in
		EntityManager em = appCtx.getBean(EntityManager.class);
		engine.put("em", em);
	}
	
	public ApplicationContext getApplicationContext(){
		return this.appCtx;
	}
	
	public ScriptEngine getScriptEngine(){
		return engine;
	}
	
	/**
	 * Run the script from given stream, returning 0 if successful
	 * and nonzero if an error occurred.  Errors will be dumped
	 * to stderr.
	 * @param in
	 * @return 0 if successful, a nonzero value otherwise
	 */
	public int eval(InputStream in){
		PlatformTransactionManager platformTransactionManager = appCtx.getBean(PlatformTransactionManager.class);
		TransactionTemplate txTemplate = new TransactionTemplate(platformTransactionManager);
		
		return txTemplate.execute( txStatus ->{
			engine.put("txStatus", txStatus);
			engine.put("commit", false);
		
			logger.info("txStatus variable bound");
			InputStreamReader reader = new InputStreamReader(in);
			
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
	
	@Override
	public void close(){
		if(appCtx != null){
			this.appCtx.close();
		}
	}
}
