package be.shouldyou.example.impl;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import be.shouldyou.example.entity.Submission;

@Service("myService")
public class MyServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(MyServiceImpl.class);

	@Transactional
	public boolean doStuff(Submission submission){
		logger.info("[{}]", submission.getSubmissionId());
		submission.setXmlData("<efgh>CHANGED</efgh>");
		return false;
	}
}
